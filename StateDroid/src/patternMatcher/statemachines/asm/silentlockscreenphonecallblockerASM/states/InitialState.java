package patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM.states;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM.SilentLockScreenPhoneCallBlockerASMStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends SilentLockScreenPhoneCallBlockerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public InitialState(){}
	public InitialState(TaintAnalyzer ta){
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
