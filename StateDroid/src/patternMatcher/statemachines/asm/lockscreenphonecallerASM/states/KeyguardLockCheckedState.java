package patternMatcher.statemachines.asm.lockscreenphonecallerASM.states;

import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.PhoneCallerReport;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.lockscreenphonecallerASM.LockScreenPhoneCallerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class KeyguardLockCheckedState extends LockScreenPhoneCallerASMStates {

	private String currInstr = "";
	private TaintAnalyzer ta;

	public KeyguardLockCheckedState() {
	}

	public KeyguardLockCheckedState(TaintAnalyzer ta) {
		this.ta = ta;
	}

	@Override
	public State update(PhoneCallingASMEvent e) {

		PhoneCallerReport rep = new PhoneCallerReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		String permStr = Config.getInstance().getCurrCFGPermutationString();
		rep.setPermutationStr(permStr);
		rep.setMessage(" [ATK] ##### This app checks if screen is locked or not, and then starts a phone call (in the background)");

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfPhoneCallerReportExists(rep)) {
			AttackReporter.getInstance().getPhoneCallerReportList().add(rep);
			rep.printReport();
		}

		return (State) new PhoneCallBlockedState(this.ta);
	}

}
