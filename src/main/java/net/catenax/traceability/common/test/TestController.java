package net.catenax.traceability.common.test;

import net.catenax.traceability.assets.domain.model.Asset;
import net.catenax.traceability.assets.domain.ports.AssetRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.*;

@Deprecated
//TODO temporary commit
@RestController
@RequestMapping("/test")
public class TestController {

	private final AssetRepository assetRepository;

	public TestController(AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
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


	public static void main(String args[]){


		Map<String, String> mp = new HashMap<>();
		Set<String> s = new HashSet<>();

	}
}
