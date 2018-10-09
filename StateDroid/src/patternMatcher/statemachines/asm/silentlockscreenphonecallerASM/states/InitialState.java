package patternMatcher.statemachines.asm.silentlockscreenphonecallerASM.states;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentlockscreenphonecallerASM.SilentLockScreenPhoneCallerASMStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends SilentLockScreenPhoneCallerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public InitialState(){}
	public InitialState(TaintAnalyzer ta){
		this.ta = ta;
	}
	
	@Override
	public State update(RingerModeSilentASMEvent e) {
		return (State) new PhoneCallStartedState(this.ta);
	}

	//Both kind-of achieve the same thing.
	@Override
	public State update(KeyguardLockCheckingASMEvent e) {
		return (State) new KeyguardLockCheckedState(this.ta);
	}
}
