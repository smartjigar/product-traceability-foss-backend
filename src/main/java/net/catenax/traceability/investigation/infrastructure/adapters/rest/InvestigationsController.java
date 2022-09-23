package net.catenax.traceability.investigation.infrastructure.adapters.rest;

import net.catenax.traceability.assets.domain.model.PageResult;
import net.catenax.traceability.common.utility.Constants;
import net.catenax.traceability.investigation.application.InvestigationFacade;
import net.catenax.traceability.investigation.domain.model.*;
import net.catenax.traceability.investigation.infrastructure.adapters.exception.BadRequestException;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/investigations")
public class InvestigationsController {

	private final InvestigationFacade investigationFacade;


	public InvestigationsController(InvestigationFacade investigationFacade) {
		this.investigationFacade = investigationFacade;
	}

	@PostMapping
	public AddQualityInvestigationResponse addQualityInvestigation(
		@Valid @RequestBody AddQualityInvestigation request) {
		AddQualityInvestigation addQualityInvestigation = new AddQualityInvestigation(request.partIds(), request.description());
		var investigation = investigationFacade.handle(addQualityInvestigation);

		return new AddQualityInvestigationResponse(investigation);
	}

	@GetMapping
	public PageResult<Notification> fetchInvestigations(Pageable pageable, @RequestParam(value = "status", required = false) List<String> status, @RequestParam(value = "type", required = true) String type) {
		InvestigationType typeEnum = null;
		if (type == null) {
			throw new BadRequestException("Please provide notificationType");
		} else {
			try {
				typeEnum = Enum.valueOf(InvestigationType.class, type);
			} catch (Exception e) {
				throw new BadRequestException("Please provide notificationType");
			}
		}
		return investigationFacade.fetchInvestigations(pageable, status, typeEnum);
	}


	@PutMapping("/{id}/status")
	public Notification updateInvestigationStatus(@Valid @NotBlank(message = Constants.ID_MUST_PRESENT) @PathVariable String id,
												  @Valid @RequestBody UpdateQualityInvestigationStatus updateStatusRequest) {
		return investigationFacade.updateInvestigationStatus(id, updateStatusRequest);
	}

}
