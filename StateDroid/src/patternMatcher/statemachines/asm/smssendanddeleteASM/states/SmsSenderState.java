package patternMatcher.statemachines.asm.smssendanddeleteASM.states;

import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.Event;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smssendanddeleteASM.SmsSendAndDeleteASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class SmsSenderState extends SmsSendAndDeleteASMStates {

	private TaintAnalyzer ta;

	public SmsSenderState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public SmsSenderState() {
	}

	@Override
	public State update(ContentProviderDeletionASMEvent e) {
		generateReport(e);
		return (State) new InitialState(ta);
	}
	
	public void generateReport(Event e){
		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());
		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		String permStr = Config.getInstance().getCurrCFGPermutationString();
		rep.setPermutationStr(permStr);
		rep.setMessage(" [ATK] ##### This app can send SMS AND delete them from SMS database::");

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfSmsSendAndDeleteReportExists(rep)) {
			AttackReporter.getInstance().getSmsSendAndDeleteReportList().add(rep);
			rep.printReport();
		}
	}
}
