package models.cfg;

import java.util.ArrayList;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class MethodSignature {

	private String name;
	private String type; //android? user-defined?
	private String returnType;
	private int maxRegNo; // this number is provided at the end of function signature where it starts.

	private String PkgClsName;
	private ArrayList<Parameter> params = new ArrayList<Parameter>();

	public MethodSignature(MethodSignature oldMS) {
		this.name = oldMS.name;
		this.type = oldMS.type;
		this.returnType = oldMS.returnType;
		this.maxRegNo = oldMS.maxRegNo;
		this.PkgClsName = oldMS.PkgClsName;
		if (oldMS.params != null && oldMS.params.size() > 0) {
			this.params = new ArrayList<Parameter>();
			for (Parameter pm : oldMS.params) {
				this.params.add(new Parameter(pm));
			}
		} else {
			this.params = oldMS.params;
		}
	}

	public MethodSignature() {
	}

	@Override
	public boolean equals(Object obj) {
		MethodSignature m = (MethodSignature) obj;

		if (!(m instanceof MethodSignature))
			return false;

		//		if(m == null)
		//			return false;
		//		name = name;

		if (!m.getPkgClsName().equalsIgnoreCase(this.getPkgClsName()))
			return false;

		if (!this.getName().equalsIgnoreCase(m.getName()))
			return false;

		if (this.getParams().size() != m.getParams().size())
			return false;

		//This is intentional.
		//		private String returnType;
		//		private int maxRegNo;   

		//		if(! this.getType().equalsIgnoreCase(m.getType()))
		//			return false;

		//		if(this.getMaxRegNo() != m.getMaxRegNo())
		//			return false;

		if (!this.getReturnType().equalsIgnoreCase(m.getReturnType()))
			return false;

		int paramSize = this.getParams().size();
		for (int i = 0; i < paramSize; i++) {
			Parameter thisParam = this.getParams().get(i);
			Parameter mParam = m.getParams().get(i);

			if (!thisParam.getType().trim().equalsIgnoreCase(mParam.getType().trim()))
				return false;
		}
		return true;
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

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public ArrayList<Parameter> getParams() {
		return params;
	}

	public void setParams(ArrayList<Parameter> params) {
		this.params = params;
	}

	public int getMaxRegNo() {
		return maxRegNo;
	}

	public void setMaxRegNo(int maxRegNo) {
		this.maxRegNo = maxRegNo;
	}

	public String getPkgClsName() {
		return PkgClsName;
	}

	public void setPkgClsName(String pkgClsName) {
		PkgClsName = pkgClsName;
	}

}
