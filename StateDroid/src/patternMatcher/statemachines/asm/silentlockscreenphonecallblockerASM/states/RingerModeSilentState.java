package patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM.states;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM.SilentLockScreenPhoneCallBlockerASMStates;
import taintanalyzer.TaintAnalyzer;

public class RingerModeSilentState extends SilentLockScreenPhoneCallBlockerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public RingerModeSilentState(){}
	public RingerModeSilentState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(KeyguardLockCheckingASMEvent e) {
		return (State) new KeyguardLockCheckedState(this.ta);
	}

}
