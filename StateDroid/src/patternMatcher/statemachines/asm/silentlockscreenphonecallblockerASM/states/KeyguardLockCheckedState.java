package patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.PhoneCallerReport;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.lockscreenphonecallerASM.states.PhoneCallBlockedState;
import patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM.SilentLockScreenPhoneCallBlockerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class KeyguardLockCheckedState extends SilentLockScreenPhoneCallBlockerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public KeyguardLockCheckedState(){}
	public KeyguardLockCheckedState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(PhoneCallBlockingASMEvent e) {

 		return (State) new PhoneCallBlockedState(this.ta);
	}

}
