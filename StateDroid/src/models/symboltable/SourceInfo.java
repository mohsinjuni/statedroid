package models.symboltable;

public class SourceInfo {

	private String srcAPI = "";
	private String srcInstr = "";

	public SourceInfo() {
	}

	public SourceInfo(SourceInfo arg) {
		srcAPI = arg.getSrcAPI();
		srcInstr = arg.getSrcInstr();
	}

	//	@Override
	//	public boolean equals(Object obj) {
	//		if (this == obj)
	//			return true;
	//		if (obj == null)
	//			return false;
	//		if (!(obj instanceof SourceInfo))
	//			return false;
	//		
	//		SourceInfo other = (SourceInfo) obj;
	//		if (srcAPI == null) {
	//			if (other.srcAPI != null)
	//				return false;
	//		} else if (!srcAPI.trim().equalsIgnoreCase(other.srcAPI.trim()))
	//			return false;
	//		if (srcInstr == null) {
	//			if (other.srcInstr != null)
	//				return false;
	//		} else if (!srcInstr.trim().equalsIgnoreCase(other.srcInstr.trim()))
	//			return false;
	//		return true;
	//	}

	public String getSrcAPI() {
		return srcAPI;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((srcAPI == null) ? 0 : srcAPI.hashCode());
		result = prime * result + ((srcInstr == null) ? 0 : srcInstr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SourceInfo))
			return false;
		SourceInfo other = (SourceInfo) obj;
		if (srcAPI == null) {
			if (other.srcAPI != null)
				return false;
		} else if (!srcAPI.equals(other.srcAPI))
			return false;
		if (srcInstr == null) {
			if (other.srcInstr != null)
				return false;
		} else if (!srcInstr.equals(other.srcInstr))
			return false;
		return true;
	}

	public void setSrcAPI(String srcAPI) {
		this.srcAPI = srcAPI;
	}

	public String getSrcInstr() {
		return srcInstr;
	}

	public void setSrcInstr(String srcInstr) {
		this.srcInstr = srcInstr;
	}

}
