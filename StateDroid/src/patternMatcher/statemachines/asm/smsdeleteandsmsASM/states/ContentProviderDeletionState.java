package patternMatcher.statemachines.asm.smsdeleteandsmsASM.states;

import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.Event;
import patternMatcher.events.asm.SmsSenderASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smsdeleteandsmsASM.SmsDeleteAndSendASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class ContentProviderDeletionState extends SmsDeleteAndSendASMStates {

	private TaintAnalyzer ta;

	public ContentProviderDeletionState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public ContentProviderDeletionState() {
	}

	@Override
	public State update(SmsSenderASMEvent e) {
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
		rep.setMessage(" [ATK] ##### This app can delete SMS from SMS database and send SMS messages::");

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfSmsDeleteAndSendReportExists(rep)) {
			AttackReporter.getInstance().getSmsDeleteAndSendReportList().add(rep);
			rep.printReport();
		}
	}
}
