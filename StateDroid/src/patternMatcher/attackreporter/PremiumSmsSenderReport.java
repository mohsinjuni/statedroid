package patternMatcher.attackreporter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import models.cfg.MethodSignature;
import models.symboltable.SourceInfo;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import configuration.Config;

public class PremiumSmsSenderReport extends Report {

	private String recpNo = "";
	private String sinkAPI = "";
	private String smsContent = "";

	private String message = "";
	private String permutationStr = "";
	private MultiValueMap functionCallStack;
	private String instrContainerCls = "";
	private String instContainerMthd = "";
	private String compPkgName = "";
	private String compCallbackMethdName = "";
	private String currComponentClsName = "";

	private Logger logger;

	public PremiumSmsSenderReport() {
		logger = Logger.getLogger("");
		//		this.setSourceInfoList(new ArrayList<SourceInfo>());

	}

	@Override
	public boolean equals(Object obj) {
		PremiumSmsSenderReport rep = (PremiumSmsSenderReport) obj;

		if (!rep.getRecpNo().equalsIgnoreCase(this.getRecpNo()))
			return false;

		if (!rep.getSinkAPI().equalsIgnoreCase(this.getSinkAPI()))
			return false;

		if (!rep.getCurrComponentClsName().equalsIgnoreCase(this.getCurrComponentClsName()))
			return false;

		return true;
	}

	public void printReport() {

		Config.getInstance().setAttackReported(true);

		logger.fatal("\n\n[msg] = " + this.getMessage());
		logger.fatal("[recpNo] = " + this.getRecpNo());

		if (!this.getSmsContent().isEmpty()) {
			logger.fatal("[smsContent] = " + this.getSmsContent());
		}

		logger.fatal(" [instrInfo]= " + this.getSinkAPI());

		logger.fatal("[ComponentInfo] = " + this.getCompPkgName() + "/" + this.getCurrComponentClsName() + "; "
				+ this.getCompCallbackMethdName());

		logger.fatal(" [CFGPermutation]= " + this.getPermutationStr());

		Stack funcCallStack = Config.getInstance().getFuncCallStack();

		if (funcCallStack.size() > 0) {
			logger.fatal("\n\n [Function call stack] ");
			for (int i = 0; i < funcCallStack.size(); i++) {
				MethodSignature ms = (MethodSignature) funcCallStack.get(i);
				if (ms != null) {
					logger.fatal(" [pkgClassName]= " + ms.getPkgClsName() + ", [methodName]= " + ms.getName() + ", [paramsCount]= "
							+ ms.getParams().size());
				}
			}
		}
		logger.fatal("\n\n");

	}

	public String getRecpNo() {
		return recpNo;
	}

	public void setRecpNo(String recpNo) {
		this.recpNo = recpNo;
	}

	public String getSinkAPI() {
		return sinkAPI;
	}

	public void setSinkAPI(String sinkAPI) {
		this.sinkAPI = sinkAPI;
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

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
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

}
