package net.catenax.traceability.investigation.infrastructure.adapters.jpa;

import net.catenax.traceability.investigation.domain.model.InvestigationType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class InvestigationsEntity extends AuditDataField {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	private String id;
	private String description;
	private boolean isDeleted;

	@Enumerated(EnumType.STRING)
	private InvestigationType type;

	public InvestigationsEntity() {

	}

	public InvestigationsEntity(String description, InvestigationType type) {
		this.description = description;
		this.isDeleted = false;
		this.type = type;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean deleted) {
		isDeleted = deleted;
	}

	public InvestigationType getType() {
		return type;
	}

	public void setType(InvestigationType type) {
		this.type = type;
	}
}
