package patternMatcher.attackreporter;

import java.util.ArrayList;
import java.util.Stack;

import models.cfg.MethodSignature;
import models.cfg.Parameter;
import models.symboltable.SourceInfo;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import configuration.Config;

public class AudioVideoRecorderReport extends Report {

	private String phoneNo = "";

	private String message = "";
	private String permutationStr = "";
	private MultiValueMap functionCallStack;
	private String instrContainerCls = "";
	private String instContainerMthd = "";
	private String compPkgName = "";
	private String compCallbackMethdName = "";
	private String currComponentClsName = "";

	private String sinkAPI = "";

	private Logger logger;

	public AudioVideoRecorderReport() {
		logger = Logger.getLogger("");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((compCallbackMethdName == null) ? 0 : compCallbackMethdName.hashCode());
		result = prime * result + ((compPkgName == null) ? 0 : compPkgName.hashCode());
		result = prime * result + ((currComponentClsName == null) ? 0 : currComponentClsName.hashCode());
		result = prime * result + ((functionCallStack == null) ? 0 : functionCallStack.hashCode());
		result = prime * result + ((instContainerMthd == null) ? 0 : instContainerMthd.hashCode());
		result = prime * result + ((instrContainerCls == null) ? 0 : instrContainerCls.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((permutationStr == null) ? 0 : permutationStr.hashCode());
		result = prime * result + ((sinkAPI == null) ? 0 : sinkAPI.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AudioVideoRecorderReport))
			return false;
		AudioVideoRecorderReport other = (AudioVideoRecorderReport) obj;
		//		if (compMethdName == null) {
		//			if (other.compMethdName != null)
		//				return false;
		//		} else if (!compMethdName.equals(other.compMethdName))
		//			return false;
		//		if (compName == null) {
		//			if (other.compName != null)
		//				return false;
		//		} else if (!compName.equals(other.compName))
		//			return false;
		//		if (currComponent == null) {
		//			if (other.currComponent != null)
		//				return false;
		//		} else if (!currComponent.equals(other.currComponent))
		//			return false;
		//		if (functionCallStack == null) {
		//			if (other.functionCallStack != null)
		//				return false;
		//		} else if (!functionCallStack.equals(other.functionCallStack))
		//			return false;
		//		if (instContainerMthd == null) {
		//			if (other.instContainerMthd != null)
		//				return false;
		//		} else if (!instContainerMthd.equals(other.instContainerMthd))
		//			return false;
		//		if (instrContainerCls == null) {
		//			if (other.instrContainerCls != null)
		//				return false;
		//		} else if (!instrContainerCls.equals(other.instrContainerCls))
		//			return false;
		//		if (message == null) {
		//			if (other.message != null)
		//				return false;
		//		} else if (!message.equals(other.message))
		//			return false;
		//		if (permutationStr == null) {
		//			if (other.permutationStr != null)
		//				return false;
		//		} else if (!permutationStr.equals(other.permutationStr))
		//			return false;
		if (sinkAPI == null) {
			if (other.sinkAPI != null)
				return false;
		} else if (!sinkAPI.equals(other.sinkAPI))
			return false;

		return true;
	}

	public void printReport() {
		logger.fatal("\n\n[msg] = " + this.getMessage());
		logger.fatal("[sink] = " + this.getSinkAPI());
		logger.fatal("\n\n[sinkContainerClsMthd] = " + this.getInstrContainerCls() + " " + this.getInstContainerMthd());
		logger.fatal("[ComponentInfo] = " + this.getCompPkgName() + "/" + this.getCurrComponentClsName() + "; "
				+ this.getCompCallbackMethdName());

		logger.fatal(" [CFGPermutation] = " + this.getPermutationStr());

		Stack funcCallStack = Config.getInstance().getFuncCallStack();

		if (funcCallStack.size() > 0) {
			logger.fatal("\n\n [Function call stack] ");
			for (int i = 0; i < funcCallStack.size(); i++) {
				MethodSignature ms = (MethodSignature) funcCallStack.get(i);
				if (ms != null) {
					String paramTypes = "";
					for (Parameter param : ms.getParams()) {
						paramTypes += param.getType() + " , ";
					}
					logger.fatal(" [pkgClassName]= " + ms.getPkgClsName() + ", [methodName]= " + ms.getName() + ", [paramTypes]= "
							+ paramTypes);
				}
			}
		}
		logger.fatal("\n\n");
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPermutationStr() {
		return permutationStr;
	}

	public void setPermutationStr(String permutationStr) {
		this.permutationStr = permutationStr;
	}

	public MultiValueMap getFunctionCallStack() {
		return functionCallStack;
	}

	public void setFunctionCallStack(MultiValueMap functionCallStack) {
		this.functionCallStack = functionCallStack;
	}

	public String getInstrContainerCls() {
		return instrContainerCls;
	}

	public void setInstrContainerCls(String instrContainerCls) {
		this.instrContainerCls = instrContainerCls;
	}

	public String getInstContainerMthd() {
		return instContainerMthd;
	}

	public void setInstContainerMthd(String instContainerMthd) {
		this.instContainerMthd = instContainerMthd;
	}

	public String getSinkAPI() {
		return sinkAPI;
	}

	public void setSinkAPI(String sinkAPI) {
		this.sinkAPI = sinkAPI;
	}

	public String getCompPkgName() {
		return compPkgName;
	}

	public void setCompPkgName(String compPkgName) {
		this.compPkgName = compPkgName;
	}

	public String getCompCallbackMethdName() {
		return compCallbackMethdName;
	}

	public void setCompCallbackMethdName(String compCallbackMethdName) {
		this.compCallbackMethdName = compCallbackMethdName;
	}

	public String getCurrComponentClsName() {
		return currComponentClsName;
	}

	public void setCurrComponentClsName(String currComponentClsName) {
		this.currComponentClsName = currComponentClsName;
	}

}
