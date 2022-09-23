package net.catenax.traceability.investigation.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.catenax.traceability.assets.domain.model.Asset;
import net.catenax.traceability.assets.domain.model.AssetNotFoundException;
import net.catenax.traceability.assets.domain.model.PageResult;
import net.catenax.traceability.assets.domain.ports.AssetRepository;
import net.catenax.traceability.common.utility.Constants;
import net.catenax.traceability.investigation.domain.model.*;
import net.catenax.traceability.investigation.domain.model.edc.cache.EndpointDataReference;
import net.catenax.traceability.investigation.domain.model.edc.cache.InMemoryEndpointDataReferenceCache;
import net.catenax.traceability.investigation.infrastructure.adapters.exception.BadRequestException;
import net.catenax.traceability.investigation.infrastructure.adapters.jpa.InvestigationsEntity;
import net.catenax.traceability.investigation.infrastructure.adapters.jpa.InvestigationsRepository;
import net.catenax.traceability.investigation.infrastructure.adapters.jpa.NotificationEntity;
import net.catenax.traceability.investigation.infrastructure.adapters.jpa.NotificationEntityRepository;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class InvestigationService {

	private static final Logger logger = LoggerFactory.getLogger(InvestigationService.class);

	private final AssetRepository assetRepository;
	private final InvestigationsRepository investigationsRepository;
	private final NotificationEntityRepository notificationEntityRepository;
	private final SdHubService sdHubService;
	private final EdcService edcService;
	private final InMemoryEndpointDataReferenceCache endpointDataReferenceCache;

	private final HttpCallService httpCallService;

	private final ObjectMapper objectMapper;

	@Value("${traceability.bpn}")
	private String senderBPN;

	@Value("${edc.ids}")
	private String idsPath;


	@Value("${edc.api.auth.key}")
	private String apiAuthKey;


	public InvestigationService(AssetRepository assetRepository,
								InvestigationsRepository investigationsRepository,
								NotificationEntityRepository investigationsPartsRepository,
								SdHubService sdHubService,
								HttpCallService httpCallService,
								InMemoryEndpointDataReferenceCache endpointDataReferenceCache,
								EdcService edcService,
								ObjectMapper objectMapper) {
		this.assetRepository = assetRepository;
		this.investigationsRepository = investigationsRepository;
		this.notificationEntityRepository = investigationsPartsRepository;
		this.sdHubService = sdHubService;
		this.edcService = edcService;
		this.endpointDataReferenceCache = endpointDataReferenceCache;
		this.httpCallService = httpCallService;
		this.objectMapper = objectMapper;
	}

	/**
	 * Save investigation detail and its Parts detail
	 */
	public Notification handle(AddQualityInvestigation addQualityInvestigation) {
		List<Asset> assets = validateRequestPartWithDbParts(addQualityInvestigation.partIds());

		InvestigationsEntity investigation = new InvestigationsEntity(addQualityInvestigation.description(), InvestigationType.SENT);
		investigationsRepository.save(investigation);

		Map<String, List<Asset>> bpnAssetMap = fetchAssertPerBpn(assets);
		List<NotificationEntity> notificationEntities = new ArrayList<>();

		for (String bpn : bpnAssetMap.keySet()) {
			List<Asset> bpnAssets = bpnAssetMap.get(bpn);
			List<String> partIds = bpnAssets.stream().map(Asset::getId).toList();

			List<NotificationEntity.PartsEntity> partsEntities = new ArrayList<>();
			partIds.forEach(e -> partsEntities.add(new NotificationEntity.PartsEntity(e)));

			notificationEntities.add(new NotificationEntity(InvestigationStatus.CREATED, investigation, partsEntities, bpn,null));
		}
		notificationEntityRepository.saveAll(notificationEntities);

		return new Notification(investigation);
	}

	/**
	 * Check if all requested parts are available in database.
	 */
	private List<Asset> validateRequestPartWithDbParts(Set<String> partIds) {
		List<Asset> assets = assetRepository.getAssetByIdIn(partIds);
		if (assets.size() != partIds.size()) {
			throw new AssetNotFoundException(Constants.ASSET_NOT_MATCH);
		}
		return assets;
	}

	/**
	 * Fetch All non-deleted investigation along with its part information
	 */
	public PageResult<Notification> fetchInvestigations(Pageable pageable, List<String> status, InvestigationType type) {
		Page<NotificationEntity> notificationPage;

		if (status != null) {
			List<InvestigationStatus> statuses = new ArrayList<>();
			status.forEach(e -> statuses.add(Enum.valueOf(InvestigationStatus.class, e)));
			notificationPage = notificationEntityRepository.findByInvestigationsIsDeletedFalseAndStatusInAndInvestigationsType(pageable, statuses, type);
		} else {
			notificationPage = notificationEntityRepository.findByInvestigationsIsDeletedFalseAndInvestigationsType(pageable, type);
		}

		if (!notificationPage.isEmpty()) {
			List<Notification> notifications = notificationPage.stream().map(this::toNotification).toList();
			return new PageResult<>(notifications, notificationPage.getPageable().getPageNumber(), notificationPage.getTotalPages(), notificationPage.getPageable().getPageSize(), notificationPage.getTotalElements());
		}
		return new PageResult<>(Page.empty(pageable));
	}

	private Notification toNotification(NotificationEntity entity) {
		List<String> partIds = entity.getPartsEntity().stream().map(NotificationEntity.PartsEntity::getPartId).toList();
		return new Notification(entity, partIds);
	}

	/**
	 * Check id exists and not-deleted.
	 */
	private InvestigationsEntity validateInvestigationId(String id) {
		InvestigationsEntity investigationsEntity = investigationsRepository.findByIdAndIsDeletedFalse(id);
		if (investigationsEntity == null) {
			throw new AssetNotFoundException(Constants.INVESTIGATION_NOT_FOUND);
		}
		return investigationsEntity;
	}

	/**
	 * Check id exists and not-deleted.
	 */
	private NotificationEntity validateNotificationId(String id) {
		Optional<NotificationEntity> notificationOpt = notificationEntityRepository.findById(id);
		if (notificationOpt.isEmpty()) {
			throw new AssetNotFoundException(Constants.INVESTIGATION_NOT_FOUND);
		}
		return notificationOpt.get();
	}

	/**
	 * Update all parts status from investigationId as requested.
	 */
	public Notification updateInvestigationStatus(String id, UpdateQualityInvestigationStatus updateStatusRequest) {
		InvestigationsEntity investigationsEntity = validateInvestigationId(id);
		List<NotificationEntity> partsEntities = notificationEntityRepository.findByInvestigations(investigationsEntity);

		if (updateStatusRequest.status() == InvestigationStatus.APPROVED || updateStatusRequest.status() == InvestigationStatus.DECLINED) {
			partsEntities.forEach(e -> e.setStatus(updateStatusRequest.status()));
			notificationEntityRepository.saveAll(partsEntities);

			if (updateStatusRequest.status().equals(InvestigationStatus.DECLINED)) {
				investigationsEntity.setDeleted(true);
				investigationsRepository.save(investigationsEntity);

				return new Notification(investigationsEntity);
			} else {
				Set<String> partIds = new HashSet<>();
				partsEntities.forEach(e -> e.getPartsEntity().forEach(p -> partIds.add(p.getPartId())));

				List<Asset> assets = assetRepository.getAssetByIdIn(partIds);
				if (!assets.isEmpty()) {
					Map<String, List<Asset>> assetMap = fetchAssertPerBpn(assets);
					for (String bpn : assetMap.keySet()) {
						updateIndividualPartStatus(bpn, partsEntities);
					}
				}
			}
			return new Notification(investigationsEntity);
		} else {
			throw new BadRequestException("You can not update this status");
		}
	}

	/**
	 * Prepare Assets with individual BPN number.
	 */
	private Map<String, List<Asset>> fetchAssertPerBpn(List<Asset> assets) {
		Map<String, List<Asset>> assetMap = new HashMap<>();
		if (!assets.isEmpty()) {
			assets.forEach(e -> {
				// Considered ManufacturedId is BPN number
				List<Asset> assetList = assetMap.get(e.getManufacturerId());
				if (CollectionUtils.isEmpty(assetList)) {
					assetList = new ArrayList<>();
				}
				assetList.add(e);
				assetMap.put(e.getManufacturerId(), assetList);
			});
		}
		return assetMap;
	}

	/**
	 * Fetch EDC url from SH.Save EDC url in notification. Start EDC Transfer process
	 *
	 */
	private void updateIndividualPartStatus(String receiverBpn, List<NotificationEntity> partsEntities) {
		Optional<NotificationEntity> notificationEntityOpt = partsEntities.stream().filter(e -> e.getBpnNumber().equals(receiverBpn)).findFirst();
		if (notificationEntityOpt.isPresent()) {
			List<String> bpns = new ArrayList<>();
			bpns.add(senderBPN);
			bpns.add(receiverBpn);

			Map<String, String> edcUrlMap = sdHubService.getEDCUrl(bpns);
			String senderEdcUrl = edcUrlMap.get(senderBPN);
			String receiverEdcUrl = edcUrlMap.get(receiverBpn);

			NotificationEntity notificationEntity = notificationEntityOpt.get();
			notificationEntity.setEdcUrl(receiverEdcUrl);

			notificationEntityRepository.save(notificationEntity);

			//TRANSFER PROCESS START
			startEDCTransfer(senderEdcUrl, receiverEdcUrl, notificationEntity, receiverBpn);
		}
	}

	private void updateContractAgreementIdForReceiverBpn(NotificationEntity notificationEntity, String contractAgreementId) {
		notificationEntity.setContractAgreementId(contractAgreementId);
		notificationEntityRepository.save(notificationEntity);
	}

	@Async
	public void startEDCTransfer(String providerEdcUrl, String consumerEdcUrl, NotificationEntity notificationEntity,
								  String receiverBpn) {
		Map<String, String> header = new HashMap<>();
		header.put("x-api-key", apiAuthKey);
		try {

			var contractOffer = edcService.findNotificationContractOffer(
				consumerEdcUrl + Constants.CONSUMER_DATA_PORT,
				providerEdcUrl + Constants.PROVIDER_IDS_PORT + idsPath,
				header
			);

			if (contractOffer.isEmpty()) {
				logger.info("No contractOffer found");
				throw new BadRequestException("No notification contract offer found.");
			}

			String agreementId = edcService.initializeContractNegotiation(
				providerEdcUrl + Constants.PROVIDER_IDS_PORT,
				contractOffer.get().getAsset().getId(),
				contractOffer.get().getId(),
				contractOffer.get().getPolicy(),
				consumerEdcUrl + Constants.CONSUMER_DATA_PORT,
				header
			);

			if (StringUtils.hasLength(agreementId)) {
				updateContractAgreementIdForReceiverBpn(notificationEntity, agreementId);
			}

			EndpointDataReference dataReference = endpointDataReferenceCache.get(agreementId);
			boolean validDataReference = dataReference != null && InMemoryEndpointDataReferenceCache.endpointDataRefTokenExpired(dataReference);
			if (!validDataReference) {
				if (dataReference != null) {
					endpointDataReferenceCache.remove(agreementId);
				}

				// Initiate transfer process
				edcService.initiateHttpProxyTransferProcess(agreementId, contractOffer.get().getAsset().getId(),
					consumerEdcUrl + Constants.CONSUMER_DATA_PORT,
					providerEdcUrl + Constants.PROVIDER_IDS_PORT + idsPath,
					header
				);
				dataReference = getDataReference(agreementId);
			}

			try {
				InvestigationsEntity investigationsEntity = notificationEntity.getInvestigations();
				NotificationDTO notificationDTO = fetchNotificationFromPartsEntity(investigationsEntity, notificationEntity, receiverBpn);
				String body = objectMapper.writeValueAsString(notificationDTO);
				var url = httpCallService.getUrl(dataReference.getEndpoint(), null, null);
				var request = new Request.Builder().url(url).addHeader(dataReference.getAuthKey(), dataReference.getAuthCode())
					.addHeader("Content-Type", Constants.JSON.type()).post(RequestBody.create(body, Constants.JSON))
					.build();

				httpCallService.sendRequest(request);
				updateNotificationStatus(notificationDTO.getNotificationId(), InvestigationStatus.SENT);
			} catch (IOException e) {
				e.printStackTrace();
				throw new BadRequestException("EDC Data Transfer fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private EndpointDataReference getDataReference(String agreementId) throws InterruptedException {
		EndpointDataReference dataReference = null;
		var waitTimeout = 20;
		while (dataReference == null && waitTimeout > 0) {
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			ScheduledFuture<EndpointDataReference> scheduledFuture =
				scheduler.schedule(() -> endpointDataReferenceCache.get(agreementId),1000, TimeUnit.MILLISECONDS);
			try {
				dataReference = scheduledFuture.get();
				waitTimeout--;
				scheduler.shutdown();
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}finally {
				if(!scheduler.isShutdown()){
					scheduler.shutdown();
				}
			}
		}
		if (dataReference == null) {
			throw new BadRequestException("Did not receive callback within 10 seconds from consumer edc.");
		}
		return dataReference;
	}


	/**
	 * Prepare NotificationDTO for EDC data transfer.
	 */
	private NotificationDTO fetchNotificationFromPartsEntity(InvestigationsEntity investigationsEntity, NotificationEntity notificationEntity, String receiverBpn) {
		Map<String, Asset> assetMap = new HashMap<>();
		Set<String> partIds = notificationEntity.getPartsEntity().stream().map(NotificationEntity.PartsEntity::getPartId).collect(Collectors.toSet());
		List<Asset> assets = assetRepository.getAssetByIdIn(partIds);
		assets.forEach(e -> assetMap.put(e.getId(), e));

		List<NotificationDTO.AffectedItem> affectedItems = notificationEntity.getPartsEntity().stream().map(e -> new NotificationDTO.AffectedItem(e.getPartId(), assetMap.get(e.getPartId()) != null ? assetMap.get(e.getPartId()).getQualityType() : null)).toList();
		String sendAddress = sdHubService.fetchEdcUrl(senderBPN);
		return new NotificationDTO(notificationEntity.getId(), investigationsEntity, affectedItems, senderBPN, sendAddress, receiverBpn, notificationEntity.getStatus());
	}

	/**
	 * This api will call for receiving notification from Sender.
	 */
	public void qualityNotificationReceive(NotificationDTO notificationDTO) {
		InvestigationsEntity investigation = new InvestigationsEntity(notificationDTO.getInformation(), InvestigationType.RECEIVED);
		investigationsRepository.save(investigation);


		List<NotificationEntity.PartsEntity> partsEntities = new ArrayList<>();
		notificationDTO.getListOfAffectedItems().forEach(e -> partsEntities.add(new NotificationEntity.PartsEntity(e.partId())));

		NotificationEntity notificationEntities = new NotificationEntity(InvestigationStatus.RECEIVED, investigation, partsEntities, notificationDTO.getRecipientBPN(),notificationDTO.getNotificationId());
		notificationEntityRepository.save(notificationEntities);
	}

	/**
	 * Update Notification status
	 */
	public void updateNotificationStatus(String id, InvestigationStatus status) {
		NotificationEntity notificationEntity = validateNotificationId(id);
		notificationEntity.setStatus(status);
		notificationEntityRepository.save(notificationEntity);
	}

}
