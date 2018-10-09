package decompilation;

import java.util.ArrayList;

import models.cfg.CFG;
import models.cfg.CFGComponent;

public class Layout {

	private String id="";
	private String type="";
	private String name="";
	private ArrayList<String> methods;
	
	public Layout()
	{
		setMethods(new ArrayList<String>());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getMethods() {
		return methods;
	}

	public void setMethods(ArrayList<String> methods) {
		this.methods = methods;
	}

	
}
