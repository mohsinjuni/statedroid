package models.cfg;

public class Parameter {

	private String name;
	private String type;
	
	public Parameter(Parameter oldParam){
		this.name = oldParam.name;
		this.type = oldParam.type;
	}
	
	public Parameter(){}
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
}
