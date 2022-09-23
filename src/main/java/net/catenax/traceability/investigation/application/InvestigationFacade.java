package net.catenax.traceability.investigation.application;

import net.catenax.traceability.assets.domain.model.PageResult;
import net.catenax.traceability.investigation.domain.model.AddQualityInvestigation;
import net.catenax.traceability.investigation.domain.model.InvestigationType;
import net.catenax.traceability.investigation.domain.model.Notification;
import net.catenax.traceability.investigation.domain.model.UpdateQualityInvestigationStatus;
import net.catenax.traceability.investigation.domain.service.InvestigationService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvestigationFacade {

	private final InvestigationService investigationService;

	public InvestigationFacade(InvestigationService investigationService) {
		this.investigationService = investigationService;
	}

	public Notification handle(AddQualityInvestigation addQualityInvestigation) {
		return investigationService.handle(addQualityInvestigation);
	}

	public PageResult<Notification> fetchInvestigations(Pageable pageable, List<String> status, InvestigationType type) {
		return investigationService.fetchInvestigations(pageable, status, type);
	}

	public Notification updateInvestigationStatus(String id, UpdateQualityInvestigationStatus updateStatusRequest) {
		return investigationService.updateInvestigationStatus(id, updateStatusRequest);
	}
}
