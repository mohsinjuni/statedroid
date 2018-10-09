package patternMatcher.statemachines.asm.silentlockscreenphonecallerASM.states;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentlockscreenphonecallerASM.SilentLockScreenPhoneCallerASMStates;
import taintanalyzer.TaintAnalyzer;

public class KeyguardLockCheckedState extends SilentLockScreenPhoneCallerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public KeyguardLockCheckedState(){}
	public KeyguardLockCheckedState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(PhoneCallingASMEvent e) {

 		return (State) new PhoneCallStartedState(this.ta);
	}

}
