package patternMatcher.statemachines.asm.lockscreensilentphonecallblockerASM.states;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.lockscreensilentphonecallblockerASM.LockScreenSilentPhoneCallBlockerASMStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends LockScreenSilentPhoneCallBlockerASMStates{

	private String currInstr="";
	private TaintAnalyzer ta;
	public InitialState(){}
	public InitialState(TaintAnalyzer ta){
		this.ta = ta;
	}
	
	/*   Attack definition
	 * 
	(1) RingerModeSilentState ==> PhoneCallBlockerState ==> RingerModeNormalState (ATTACK reported here) --------------> silentPhoneCallBlockerASM
	(2) Lock phone screen ==> Start a phone call ==> End phone call  -------------> lockscreenphonecallerASM
	(3) Intercept phone call ==> Set phone ringer to silent mode ==> Lock phone screen ==> block phone call ==> restore phone volume 
	(4) Intercept phone call ==> Lock phone screen ==> Set phone ringer to silent mode ==> block phone call ==> restore phone volume ----------> Current
		 
	*/	

	@Override
	public State update(KeyguardLockCheckingASMEvent e) {
		return (State) new KeyguardLockCheckedState(this.ta);
	}

}
