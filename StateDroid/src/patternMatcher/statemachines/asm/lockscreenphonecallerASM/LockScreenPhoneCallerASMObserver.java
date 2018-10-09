package patternMatcher.statemachines.asm.lockscreenphonecallerASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.lockscreenphonecallerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class LockScreenPhoneCallerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	
	/*   Attack definition
	 * 
	(1) RingerModeSilentState ==> PhoneCallBlockerState ==> RingerModeNormalState (ATTACK reported here) --------------> silentphonecallblockerASM
	(2) Lock phone screen ==> Start a phone call ==> End phone call  -------------> lockscreenphonecallerASM
	(3) Intercept phone call ==> Set phone ringer to silent mode ==> Lock phone screen ==> block phone call ==>  (delete call logs)
	(4) Intercept phone call ==> Lock phone screen ==> Set phone ringer to silent mode ==> block phone call ==>  (delete call logs)
	
	*/
	public LockScreenPhoneCallerASMObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(this.taSubject);
	}
	
	public LockScreenPhoneCallerASMObserver (){
		this.state = new InitialState();
	}

	@Override
	public void update(KeyguardLockCheckingASMEvent e) {
		state = state.update(e);
	}
	
	@Override
	public void update(PhoneCallingASMEvent e) {
		state = state.update(e);		
	}
	
	@Override
	public void update(PhoneCallBlockingASMEvent e) {
		state = state.update(e);		
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}


}
