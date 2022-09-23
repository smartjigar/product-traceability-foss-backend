package net.catenax.traceability.investigation.domain.model;

import net.catenax.traceability.investigation.infrastructure.adapters.jpa.InvestigationsEntity;
import net.catenax.traceability.investigation.infrastructure.adapters.jpa.NotificationEntity;

import java.util.Date;
import java.util.List;

public class Notification {

	private String notificationId;
	private String description;
	private String investigationId;
	private Date createdDate;
	private String createdBy;
	private List<String> partIds;
	private InvestigationStatus status;
	private String notificationReferenceId;


	public Notification(NotificationEntity notificationEntity, List<String> partIds) {
		this.notificationId = notificationEntity.getId();
		this.description = notificationEntity.getInvestigations().getDescription();
		this.investigationId = notificationEntity.getInvestigations().getId();
		this.createdDate = Date.from(notificationEntity.getCreatedDate());
		this.createdBy = notificationEntity.getCreatedBy();
		this.partIds = partIds;
		this.status = notificationEntity.getStatus();
		this.notificationReferenceId =notificationEntity.getNotificationReferenceId();
	}

	public Notification(InvestigationsEntity investigationsEntity) {
		this.description = investigationsEntity.getDescription();
		this.investigationId = investigationsEntity.getId();
		this.createdDate = Date.from(investigationsEntity.getCreatedDate());
		this.createdBy = investigationsEntity.getCreatedBy();
	}


	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInvestigationId() {
		return investigationId;
	}

	public void setInvestigationId(String investigationId) {
		this.investigationId = investigationId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public List<String> getPartIds() {
		return partIds;
	}

	public void setPartIds(List<String> partIds) {
		this.partIds = partIds;
	}

	public InvestigationStatus getStatus() {
		return status;
	}

	public void setStatus(InvestigationStatus status) {
		this.status = status;
	}

	public String getNotificationReferenceId() {
		return notificationReferenceId;
	}

	public void setNotificationReferenceId(String notificationReferenceId) {
		this.notificationReferenceId = notificationReferenceId;
	}
}
