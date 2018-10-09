package patternMatcher.statemachines.asm.contentresolverASM.states;

import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.ContentProviderDeletionReport;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderInsertASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderUpdateASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.contentresolverASM.ContentResolverASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends ContentResolverASMStates {

	private String currInstr = "";
	private TaintAnalyzer ta;

	public InitialState() {
	}

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	@Override
	public State update(ContentProviderDeletionASMEvent e) {

		//		System.out.println("PhoneCallingEvent Received");

		String permStr = Config.getInstance().getCurrCFGPermutationString();

		String uri = (String) e.getEventInfo().get("dbInfo");

		ContentProviderDeletionReport rep = new ContentProviderDeletionReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		rep.setUri(uri);
		rep.setPermutationStr(permStr);
		rep.setMessage(" ##### This app can delete data from a database pointed by :: [uri] = " + uri);

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfContentProviderDeletionReportExists(rep)) {
			AttackReporter.getInstance().getContentProviderDeletionReportList().add(rep);
			rep.printReport();
		}
		return this;
	}

	@Override
	public State update(ContentProviderUpdateASMEvent e) {

		//		System.out.println("PhoneCallingEvent Received");

		String permStr = Config.getInstance().getCurrCFGPermutationString();

		String uri = (String) e.getEventInfo().get("dbInfo");

		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());
		rep.setCompPkgName(e.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(e.getCurrCompCallbackMethodName());

		rep.setCurrComponentClsName(e.getCurrComponentName());
		// 		rep.setUri(uri);
		rep.setPermutationStr(permStr);
		rep.setMessage(" ##### This app can update database pointed by [uri] = " + uri + "     #########");

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfContentProviderUpdateReportExists(rep)) {
			AttackReporter.getInstance().getContentProviderUpdateReportList().add(rep);
			rep.printReport();
		}
		return this;
	}

	@Override
	public State update(ContentProviderInsertASMEvent e) {

		String permStr = Config.getInstance().getCurrCFGPermutationString();
		String uri = (String) e.getEventInfo().get("dbInfo");

		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());
		rep.setCompPkgName(e.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(e.getCurrCompCallbackMethodName());

		rep.setCurrComponentClsName(e.getCurrComponentName());
		rep.setPermutationStr(permStr);
		rep.setMessage(" ##### This app can insert data into a sensitive database pointed by [uri] =  " + uri + "     #########");

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfContentProviderInsertReportExists(rep)) {
			AttackReporter.getInstance().getContentProviderInsertReportList().add(rep);
			rep.printReport();
		}
		return this;
	}

}
