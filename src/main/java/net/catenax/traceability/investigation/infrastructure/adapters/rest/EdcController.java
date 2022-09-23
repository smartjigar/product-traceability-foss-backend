package net.catenax.traceability.investigation.infrastructure.adapters.rest;

import net.catenax.traceability.investigation.domain.model.NotificationDTO;
import net.catenax.traceability.investigation.domain.service.InvestigationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EdcController {
	private static final Logger logger = LoggerFactory.getLogger(EdcController.class);
	private final InvestigationService investigationService;


	public EdcController(InvestigationService investigationService) {
		this.investigationService = investigationService;
	}

	/**
	 * Receiver API call for EDC Transfer
	 */
	@PostMapping("/qualitynotifications/receive")
	public void qualityNotificationReceive(@RequestBody NotificationDTO notificationDTO) {
		logger.info("EdcController [qualityNotificationReceive] notificationId:{}", notificationDTO.getNotificationId());
		investigationService.qualityNotificationReceive(notificationDTO);
	}
}

