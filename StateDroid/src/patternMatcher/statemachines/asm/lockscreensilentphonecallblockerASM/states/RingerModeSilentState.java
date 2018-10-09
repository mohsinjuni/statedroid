package patternMatcher.statemachines.asm.lockscreensilentphonecallblockerASM.states;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.lockscreenphonecallerASM.states.PhoneCallBlockedState;
import patternMatcher.statemachines.asm.lockscreensilentphonecallblockerASM.LockScreenSilentPhoneCallBlockerASMStates;
import taintanalyzer.TaintAnalyzer;

public class RingerModeSilentState extends LockScreenSilentPhoneCallBlockerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public RingerModeSilentState(){}
	public RingerModeSilentState(TaintAnalyzer ta){
		this.ta = ta;
	}

	@Override
	public State update(PhoneCallBlockingASMEvent e) {
 		return (State) new PhoneCallBlockedState(this.ta);
	}
	
}
