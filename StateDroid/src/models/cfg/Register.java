package models.cfg;

public class Register {

	String name;
	String type;
	boolean tainted;
	String value;
	boolean constant;
	String referenceObject;
	String callerObjectType;
	String fullyQualifiedName;

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	public String getCallerObjectType() {
		return callerObjectType;
	}

	public void setCallerObjectType(String callerObjectType) {
		this.callerObjectType = callerObjectType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isTainted() {
		return tainted;
	}

	public void setTainted(boolean tainted) {
		this.tainted = tainted;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isConstant() {
		return constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}

	public String getReferenceObject() {
		return referenceObject;
	}

	public void setReferenceObject(String referenceObject) {
		this.referenceObject = referenceObject;
	}

}
