package patternMatcher.statemachines.csm.informationstoringintodb.states;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

import models.cfg.MethodSignature;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.SourceInfo;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.InformationLeakerReport;
import patternMatcher.attackreporter.InformationStoringIntoDBReport;
import patternMatcher.events.asm.InformationLeakerASMEvent;
import patternMatcher.events.csm.SqlLiteDatabaseInsertEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.informationleaker.InformationLeakerStates;
import patternMatcher.statemachines.csm.informationstoringintodb.InformationStoringIntoDBStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends InformationStoringIntoDBStates {

	private String currInstr = "";

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(SqlLiteDatabaseInsertEvent e) {

		AndroidManifest am = Config.getInstance().getAndroidManifest();
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("Initial State");
		String currPkgClsInfo = e.getCurrPkgClsName();

		ComponentManifest compInfo = am.findComponentManifest(currPkgClsInfo);

		Hashtable eventInfo = e.getEventInfo();
		ArrayList<SourceInfo> srcList = (ArrayList<SourceInfo>) eventInfo.get("sources");
		String instr = (String) eventInfo.get("instrText");

		ArrayList<SourceInfo> reportedSources = new ArrayList<SourceInfo>();

		for (SourceInfo si : srcList) {
			if (!reportedSources.contains(si)) {
				reportedSources.add(si);
			}
		}
		String info1 = instr;
		String permStr = Config.getInstance().getCurrCFGPermutationString();

		InformationStoringIntoDBReport rep = new InformationStoringIntoDBReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		rep.setSourceInfoList(reportedSources);
		rep.setSinkAPI(info1);
		rep.setPermutationStr(permStr);
		rep.setMessage(" ##### This API stores sensitive information into a local database which can be later leaked out of the device::");

		Stack<MethodSignature> funcCallStack = Config.getInstance().getFuncCallStack();
		Stack<MethodSignature> funcCallStackCopy = new Stack<MethodSignature>();

		for (int i = 0; i < funcCallStack.size(); i++) {
			MethodSignature oldMS = funcCallStack.get(i);
			MethodSignature newMS = new MethodSignature(oldMS);
			funcCallStackCopy.add(newMS);
		}
		rep.setFunctionCallStack(funcCallStackCopy);

		if (Config.getInstance().isUniqueWarningEnabled()) {
			if (!AttackReporter.getInstance().checkIfInfoStoringIntoDBReportExists(rep)) {
				AttackReporter.getInstance().getInfoStoringIntoDBReportList().add(rep);
				rep.printReport();
			}
		} else {
			AttackReporter.getInstance().getInfoStoringIntoDBReportList().add(rep);
			rep.printReport();
		}
		return this;
	}

}
