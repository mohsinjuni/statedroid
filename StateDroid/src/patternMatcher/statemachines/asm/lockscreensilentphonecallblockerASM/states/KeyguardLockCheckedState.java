package patternMatcher.statemachines.asm.lockscreensilentphonecallblockerASM.states;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.lockscreensilentphonecallblockerASM.LockScreenSilentPhoneCallBlockerASMStates;
import taintanalyzer.TaintAnalyzer;

public class KeyguardLockCheckedState extends LockScreenSilentPhoneCallBlockerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public KeyguardLockCheckedState(){}
	public KeyguardLockCheckedState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(RingerModeSilentASMEvent e) {
		return (State) new RingerModeSilentState(this.ta);
	}

	//Both kind-of achieve the same thing.
	@Override
	public State update(VoiceCallStreamDecreasingASMEvent e) {
		return (State) new RingerModeSilentState(this.ta);
	}
}
