package net.catenax.traceability.investigation.domain.service;

import net.catenax.traceability.investigation.domain.model.GetSdHubResponse;
import net.catenax.traceability.investigation.infrastructure.adapters.feign.sdhub.SdHubApiClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class SdHubService {

	private static final Map<String, String> bpnEdcMap = new HashMap<>();
	private final SdHubApiClient sdHubApiClient;

	public SdHubService(SdHubApiClient sdHubApiClient) {
		this.sdHubApiClient = sdHubApiClient;
	}


	@PostConstruct
	public void testMethod() {
		bpnEdcMap.put("man-1", "https://consumer-controlplane.dev.demo.ftcpro.co"); // http://localhost:
		bpnEdcMap.put("BPNL00000003AXS3", "https://consumer-controlplane-3.dev.demo.ftcpro.co"); // http://localhost:
		bpnEdcMap.put("BPNL00000002AXS2", "https://consumer-controlplane.dev.demo.ftcpro.co"); //http://localhost:
		bpnEdcMap.put("BPNL00000001AXS1", "https://trace-consumer-controlplane.dev.demo.ftcpro.co"); //http://localhost:
	}

	private GetSdHubResponse fetchSdHubData(@RequestParam(value = "id", required = false) List<String> ids,
											@RequestParam(value = "companyNumbers", required = false) List<String> companyNumbers,
											@RequestParam(value = "headquarterCountries", required = false) List<String> headquarterCountries,
											@RequestParam(value = "legalCountries", required = false) List<String> legalCountries,
											@RequestParam(value = "serviceProviders", required = false) List<String> serviceProviders,
											@RequestParam(value = "sdTypes", required = false) List<String> sdTypes, List<String> bpns) {

		return sdHubApiClient.getSelfDescriptions(ids, companyNumbers, headquarterCountries, legalCountries, serviceProviders, sdTypes, bpns);
	}

	private String getEdcUrlFromSdHub(String bpn) {
		GetSdHubResponse sdHubResponse = fetchSdHubData(null, null, null, null, null, null, List.of(bpn));

		Assert.notNull(sdHubResponse, "Not received any response from SD Hub");
		Assert.notEmpty(sdHubResponse.getVerifiableCredential(), "Did not get any verifiable credential");

		String edcUrl = sdHubResponse.getVerifiableCredential().get(0).credentialSubject().service_provider();
		putEdcUrlCache(bpn, edcUrl);
		return edcUrl;
	}

	public Map<String, String> getEDCUrl(List<String> bpns) {
		List<String> bpnList = new ArrayList<>();
		Map<String, String> bpnEdcUrlMap = new HashMap<>();

		for (String bpn : bpns) {
			String edcUrl = fetchEdcUrlFromCache(bpn);
			if (edcUrl == null) {
				bpnList.add(bpn);
			} else {
				bpnEdcUrlMap.put(bpn, edcUrl);
			}
		}

		if (bpnEdcUrlMap.size() == bpns.size()) {
			return bpnEdcUrlMap;
		}
		GetSdHubResponse sdHubResponse = fetchSdHubData(null, null, null, null, null, null, bpnList);
		getEDCUrlFromSdHubResponse(sdHubResponse, bpnEdcUrlMap);
		return bpnEdcUrlMap;
	}

	private void getEDCUrlFromSdHubResponse(GetSdHubResponse sdHubResponse, Map<String, String> bpnEdcUrlMap) {
		Assert.notNull(sdHubResponse, "Not received any response from SD Hub");
		Assert.notEmpty(sdHubResponse.getVerifiableCredential(), "Did not get any verifiable credential");
		sdHubResponse.getVerifiableCredential().forEach(e -> {
			bpnEdcUrlMap.put(e.credentialSubject().bpn(), e.credentialSubject().service_provider());
		});
	}

	private void putEdcUrlCache(String bpnNumber, String edcUrl) {
		bpnEdcMap.put(bpnNumber, edcUrl);
	}

	private String fetchEdcUrlFromCache(String bpn) {
		return bpnEdcMap.get(bpn);
	}

	public String fetchEdcUrl(String bpnNumber) {
		String edcUrl = bpnEdcMap.get(bpnNumber);
		if (edcUrl == null) {
			return getEdcUrlFromSdHub(bpnNumber);
		}
		return edcUrl;
	}

	public void addUpdateSdHubUrl(String bpnNumber, String edcUrl){
			bpnEdcMap.put(bpnNumber,edcUrl);
	}

	public Map<String, String> fetchSdHubUrl(){
		return bpnEdcMap;
	}

}
