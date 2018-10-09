package patternMatcher.statemachines.asm.phonecallblockerASM.states;

import java.util.ArrayList;

import configuration.Config;

import models.cfg.InstructionResponse;
import patternMatcher.statemachines.State;

import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.PhoneCallBlockerReport;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.statemachines.asm.phonecallblockerASM.PhoneCallBlockerASMStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends PhoneCallBlockerASMStates {

	private String currInstr = "";
	private TaintAnalyzer ta;

	public InitialState() {
	}

	public InitialState(TaintAnalyzer ta) {
		this.ta = ta;
	}

	@Override
	public State update(PhoneCallBlockingASMEvent e) {

		//		System.out.println("PhoneBlockingEvent Received");

		PhoneCallBlockerReport rep = new PhoneCallBlockerReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		String permStr = Config.getInstance().getCurrCFGPermutationString();
		rep.setPermutationStr(permStr);
		rep.setMessage(" ##### This app can block incoming phone calls.::");

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfPhoneCallBlockerExists(rep)) {
			AttackReporter.getInstance().getPhoneCallBlockerReportList().add(rep);
			rep.printReport();
		}

		return this;
	}

}
