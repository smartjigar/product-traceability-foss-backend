package net.catenax.traceability.common.test;

@Deprecated
//TODO temporary commit
public class ChildDescription {

	private String id;
	private String idShort;

	public ChildDescription() {
	}

	public ChildDescription(String id, String idShort) {
		this.id = id;
		this.idShort = idShort;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdShort() {
		return idShort;
	}

	public void setIdShort(String idShort) {
		this.idShort = idShort;
	}
}
