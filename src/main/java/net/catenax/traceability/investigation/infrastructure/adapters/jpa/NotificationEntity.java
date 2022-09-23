package net.catenax.traceability.investigation.infrastructure.adapters.jpa;

import net.catenax.traceability.investigation.domain.model.InvestigationStatus;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
public class NotificationEntity extends AuditDataField {


	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	private String id;
	@Enumerated(EnumType.STRING)
	private InvestigationStatus status;
	@ManyToOne(fetch = FetchType.LAZY)
	private InvestigationsEntity investigations;
	private String bpnNumber;
	private String edcUrl;
	private String contractAgreementId;

	private String notificationReferenceId;

	@ElementCollection
	private List<PartsEntity> partsEntity;

	public NotificationEntity() {
	}

	public NotificationEntity(InvestigationStatus status, InvestigationsEntity investigations, List<PartsEntity> partsEntity, String bpnNumber, String notificationReferenceId) {
		this.partsEntity = partsEntity;
		this.status = status;
		this.investigations = investigations;
		this.bpnNumber = bpnNumber;
		this.notificationReferenceId = notificationReferenceId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public InvestigationStatus getStatus() {
		return status;
	}

	public void setStatus(InvestigationStatus status) {
		this.status = status;
	}

	public List<PartsEntity> getPartsEntity() {
		return partsEntity;
	}

	public void setPartsEntity(List<PartsEntity> partsEntity) {
		this.partsEntity = partsEntity;
	}

	public InvestigationsEntity getInvestigations() {
		return investigations;
	}

	public void setInvestigations(InvestigationsEntity investigationsId) {
		this.investigations = investigationsId;
	}

	public String getBpnNumber() {
		return bpnNumber;
	}

	public void setBpnNumber(String bpnNumber) {
		this.bpnNumber = bpnNumber;
	}

	public String getEdcUrl() {
		return edcUrl;
	}

	public void setEdcUrl(String edcUrl) {
		this.edcUrl = edcUrl;
	}

	public String getContractAgreementId() {
		return contractAgreementId;
	}

	public void setContractAgreementId(String contractAgreementId) {
		this.contractAgreementId = contractAgreementId;
	}

	public String getNotificationReferenceId() {
		return notificationReferenceId;
	}

	public void setNotificationReferenceId(String notificationReferenceId) {
		this.notificationReferenceId = notificationReferenceId;
	}

	@Embeddable
	public static class PartsEntity {
		private String partId;

		public PartsEntity() {
		}

		public PartsEntity(String partId) {
			this.partId = partId;
		}

		public String getPartId() {
			return partId;
		}

		public void setPartId(String partId) {
			this.partId = partId;
		}
	}
}
