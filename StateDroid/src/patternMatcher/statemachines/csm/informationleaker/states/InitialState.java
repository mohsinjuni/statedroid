package patternMatcher.statemachines.csm.informationleaker.states;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

import models.cfg.MethodSignature;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.SourceInfo;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.InformationLeakerReport;
import patternMatcher.events.asm.InformationLeakerASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.informationleaker.InformationLeakerStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends InformationLeakerStates {

	private String currInstr = "";

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(InformationLeakerASMEvent e) {

		AndroidManifest am = Config.getInstance().getAndroidManifest();

		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("Initial State");

		String currPkgClsInfo = e.getCurrPkgClsName();

		ComponentManifest compInfo = am.findComponentManifest(currPkgClsInfo);

		ArrayList<String> permList = am.getUsedPermStrList();

		//		System.out.println("InformationLeakageEvent Received");

		Hashtable eventInfo = e.getEventInfo();
		ArrayList<SourceInfo> srcList = (ArrayList<SourceInfo>) eventInfo.get("sources");
		ArrayList<String> currCFGPermutation = (ArrayList<String>) eventInfo.get("currCFGPermutation");
		String instr = (String) eventInfo.get("instrText");

		//		logger.fatal("<<<<<<< [Source Info] >>>>");
		ArrayList<SourceInfo> reportedSources = new ArrayList<SourceInfo>();

		for (SourceInfo si : srcList) {
			if (!reportedSources.contains(si)) {
				reportedSources.add(si);
			}
		}
		String info1 = instr;

		String permStr = Config.getInstance().getCurrCFGPermutationString();

		InformationLeakerReport rep = new InformationLeakerReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		rep.setSourceInfoList(reportedSources);
		rep.setSinkAPI(info1);
		rep.setPermutationStr(permStr);
		rep.setMessage(" ##### This API can leak Information::");

		Stack<MethodSignature> funcCallStack = Config.getInstance().getFuncCallStack();
		Stack<MethodSignature> funcCallStackCopy = new Stack<MethodSignature>();

		//Default stack.clone() provides shallow-copy, so I will make deep-copy on my own.
		for (int i = 0; i < funcCallStack.size(); i++) {
			MethodSignature oldMS = funcCallStack.get(i);
			MethodSignature newMS = new MethodSignature(oldMS);
			funcCallStackCopy.add(newMS);
		}
		rep.setFunctionCallStack(funcCallStackCopy);

		if (Config.getInstance().isUniqueWarningEnabled()) {
			if (!AttackReporter.getInstance().checkIfInfoLeakExists(rep)) {
				AttackReporter.getInstance().getInfoLeakReportList().add(rep);
				rep.printReport();
			}
		} else {
			AttackReporter.getInstance().getInfoLeakReportList().add(rep);
			rep.printReport();
		}

		return this;
	}

}
