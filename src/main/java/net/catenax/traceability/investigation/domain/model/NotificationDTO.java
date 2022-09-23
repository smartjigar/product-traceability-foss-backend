package net.catenax.traceability.investigation.domain.model;

import net.catenax.traceability.assets.domain.model.QualityType;
import net.catenax.traceability.investigation.infrastructure.adapters.jpa.InvestigationsEntity;

import java.util.List;


public class NotificationDTO {

	private String notificationId;
	private String senderBPN;
	private String senderAddress;
	private String recipientBPN;
	private String information;
	private List<AffectedItem> listOfAffectedItems;
	private InvestigationStatus status;

	private NotificationType classification;


	public NotificationDTO() {
	}

	public NotificationDTO(String notificationId, InvestigationsEntity investigationsEntity, List<AffectedItem> listOfAffectedItems, String senderBPN, String senderAddress, String recipientBPN, InvestigationStatus status) {
		this.notificationId = notificationId;
		this.senderBPN = senderBPN;
		this.senderAddress = senderAddress;
		this.recipientBPN = recipientBPN;
		this.information = investigationsEntity.getDescription();
		this.listOfAffectedItems = listOfAffectedItems;
		this.status = status;
		this.classification = NotificationType.QMINVESTIGATION;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getSenderBPN() {
		return senderBPN;
	}

	public void setSenderBPN(String senderBPN) {
		this.senderBPN = senderBPN;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getRecipientBPN() {
		return recipientBPN;
	}

	public void setRecipientBPN(String recipientBPN) {
		this.recipientBPN = recipientBPN;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public InvestigationStatus getStatus() {
		return status;
	}

	public void setStatus(InvestigationStatus status) {
		this.status = status;
	}

	public List<AffectedItem> getListOfAffectedItems() {
		return listOfAffectedItems;
	}

	public void setListOfAffectedItems(List<AffectedItem> listOfAffectedItems) {
		this.listOfAffectedItems = listOfAffectedItems;
	}

	public NotificationType getClassification() {
		return classification;
	}

	public void setClassification(NotificationType classification) {
		this.classification = classification;
	}

	public record AffectedItem(String partId, QualityType qualityType) {

	}
}
