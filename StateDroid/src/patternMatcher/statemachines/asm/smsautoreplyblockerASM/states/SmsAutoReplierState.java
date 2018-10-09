package patternMatcher.statemachines.asm.smsautoreplyblockerASM.states;

import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.Event;
import patternMatcher.events.asm.IncomingSmsBlockerASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smsautoreplyblockerASM.SmsAutoReplyBlockerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class SmsAutoReplierState extends SmsAutoReplyBlockerASMStates {

	private TaintAnalyzer ta;

	public SmsAutoReplierState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public SmsAutoReplierState() {
	}

	@Override
	public State update(IncomingSmsBlockerASMEvent e) {
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
		rep.setMessage(" [ATK] ##### This app can send auto-reply to incoming SMS messages AND block incoming sms messages::");

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfSmsAutoReplyBlockerReportExists(rep)) {
			AttackReporter.getInstance().getSmsAutoReplyBlockerReportList().add(rep);
			rep.printReport();
		}
	}
}
