package patternMatcher.statemachines.asm.smsblockautoreplierASM.states;

import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.Event;
import patternMatcher.events.asm.IncomingSmsAutoReplierASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smsblockautoreplierASM.SmsBlockAutoReplierASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class SmsBlockedState extends SmsBlockAutoReplierASMStates {

	private TaintAnalyzer ta;

	public SmsBlockedState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public SmsBlockedState() {
	}

	@Override
	public State update(IncomingSmsAutoReplierASMEvent e) {
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
		rep.setMessage(" [ATK] ##### This app can block incoming sms messages AND send auto-reply to incoming SMS messages.::");

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfSmsBlockAutoReplierReportExists(rep)) {
			AttackReporter.getInstance().getSmsBlockAutoReplierReportList().add(rep);
			rep.printReport();
		}
	}
}
