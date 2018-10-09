package patternMatcher.attackreporter;

import java.util.ArrayList;
import java.util.Stack;

import models.cfg.MethodSignature;
import models.cfg.Parameter;
import models.symboltable.SourceInfo;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import configuration.Config;

public class InformationStoringIntoDBReport extends Report {

	private String sinkAPI = "";
	private String sourceAPI = "";
	private ArrayList<SourceInfo> sourceInfoList;

	private String message = "";
	private String permutationStr = "";
	private Stack functionCallStack;
	private String instrContainerCls = "";
	private String instContainerMthd = "";
	private String currComponentClsName = "";
	private String compPkgName = "";
	private String compCallbackMethdName = "";

	private Logger logger;

	public InformationStoringIntoDBReport() {
		logger = Logger.getLogger("");
		this.setSourceInfoList(new ArrayList<SourceInfo>());
	}

	@Override
	public boolean equals(Object obj) {
		InformationStoringIntoDBReport rep = (InformationStoringIntoDBReport) obj;
		ArrayList<SourceInfo> repSourceInfoList = rep.getSourceInfoList();

		for (SourceInfo si : repSourceInfoList) {
			if (!this.getSourceInfoList().contains(si))
				return false;
		}
		for (SourceInfo si : this.getSourceInfoList()) {
			if (!repSourceInfoList.contains(si))
				return false;
		}

		if (!rep.getSinkAPI().trim().equalsIgnoreCase(this.getSinkAPI().trim()))
			return false;

		return true;
	}

	public void printReport() {

		Config.getInstance().setAttackReported(true);

		logger.fatal("\n\n[msg] = " + this.getMessage());

		for (SourceInfo si : this.getSourceInfoList()) {
			logger.fatal("[srcInfo]= " + si.getSrcAPI());
			logger.fatal("[srcInstrInfo]= " + si.getSrcInstr());
		}

		//			logger.fatal("[src] = " + this.getSourceAPI());
		logger.fatal("[sink] = " + this.getSinkAPI());

		logger.fatal("\n\n[sinkContainerClsMthd] = " + this.getInstrContainerCls() + " " + this.getInstContainerMthd());
		logger.fatal("[ComponentInfo] = " + this.getCompPkgName() + "/" + this.getCurrComponentClsName() + "; "
				+ this.getCompCallbackMethdName());

		logger.fatal(" [CFGPermutation] = " + this.getPermutationStr());

		Stack funcCallStack = this.getFunctionCallStack();

		if (funcCallStack.size() > 0) {
			logger.fatal("\n\n [Function call stack] from first-method to last-invoked-method order");
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

	public String getSinkAPI() {
		return sinkAPI;
	}

	public void setSinkAPI(String sinkAPI) {
		this.sinkAPI = sinkAPI;
	}

	public String getSourceAPI() {
		return sourceAPI;
	}

	public void setSourceAPI(String sourceAPI) {
		this.sourceAPI = sourceAPI;
	}

	public ArrayList<SourceInfo> getSourceInfoList() {
		return sourceInfoList;
	}

	public void setSourceInfoList(ArrayList<SourceInfo> sourceInfoList) {
		this.sourceInfoList = sourceInfoList;
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

	public String getCurrComponentClsName() {
		return currComponentClsName;
	}

	public void setCurrComponentClsName(String currComponent) {
		this.currComponentClsName = currComponent;
	}

	public String getCompPkgName() {
		return compPkgName;
	}

	public void setCompPkgName(String compName) {
		this.compPkgName = compName;
	}

	public String getCompCallbackMethdName() {
		return compCallbackMethdName;
	}

	public void setCompCallbackMethdName(String compMethdName) {
		this.compCallbackMethdName = compMethdName;
	}

	public Stack getFunctionCallStack() {
		return functionCallStack;
	}

	public void setFunctionCallStack(Stack functionCallStack) {
		this.functionCallStack = functionCallStack;
	}

}
