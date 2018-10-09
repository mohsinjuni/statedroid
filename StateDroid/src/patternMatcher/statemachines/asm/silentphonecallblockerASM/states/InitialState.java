package patternMatcher.statemachines.asm.silentphonecallblockerASM.states;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentphonecallblockerASM.SilentPhoneCallBlockerASMStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends SilentPhoneCallBlockerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public InitialState(){}
	public InitialState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(AudioManagerRingerModeOffASMEvent e) {
		return (State) new RingerModeSilentState(this.ta);
	}

}
