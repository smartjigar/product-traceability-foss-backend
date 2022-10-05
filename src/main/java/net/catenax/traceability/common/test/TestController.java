package net.catenax.traceability.common.test;

import net.catenax.traceability.assets.domain.model.Asset;
import net.catenax.traceability.assets.domain.model.PageResult;
import net.catenax.traceability.assets.domain.ports.AssetRepository;
import net.catenax.traceability.investigation.domain.model.AddQualityInvestigation;
import net.catenax.traceability.investigation.domain.model.InvestigationStatus;
import net.catenax.traceability.investigation.domain.model.Notification;
import net.catenax.traceability.investigation.domain.model.UpdateQualityInvestigationStatus;
import net.catenax.traceability.investigation.domain.service.InvestigationService;
import net.catenax.traceability.investigation.domain.service.SdHubService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.*;

@Deprecated
//TODO temporary commit
@RestController
@RequestMapping("/test")
public class TestController {

	private final AssetRepository assetRepository;

	private final SdHubService sdHubService;

	private final InvestigationService investigationService;

	public TestController(AssetRepository assetRepository,SdHubService sdHubService,
						  InvestigationService investigationService) {
		this.assetRepository = assetRepository;
		this.sdHubService=sdHubService;
		this.investigationService=investigationService;
	}

	@PostMapping("/asset")
	public void savePart(@Valid @RequestBody AssetRequest assetRequest) {
		Asset asset;
		try {
			asset = new Asset(assetRequest);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		assetRepository.saveAll(List.of(asset));
	}

	@PutMapping("/edc-url/config")
	public void addUpdateEdcUrlConfig(@Valid @RequestBody AddUpdateEdcUrl addUpdateEdcUrl){
		sdHubService.addUpdateSdHubUrl(addUpdateEdcUrl.bpnNumber(), addUpdateEdcUrl.edcUrl());
	}

	@GetMapping("/edc-url/config")
	public Map<String, String> getEdcUrlConfig(){
		return sdHubService.fetchSdHubUrl();
	}

	@PostMapping("/investigation")
	public void createApproveInvestigation(@Valid @RequestBody AddQualityInvestigation investigationReq){
		Notification notification =investigationService.handle(investigationReq);
		investigationService.updateInvestigationStatus(notification.getInvestigationId(), new UpdateQualityInvestigationStatus(InvestigationStatus.APPROVED));
	}

	@GetMapping("/assets")
	public PageResult<Asset> assets(Pageable pageable) {
		return assetRepository.getAssets(pageable);
	}



}
