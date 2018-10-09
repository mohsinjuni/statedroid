package patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeNormalASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class SilentLockScreenPhoneCallBlockerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	
	/*   Attack definition
	 * 
	(1) RingerModeSilentState ==> PhoneCallBlockerState ==> RingerModeNormalState (ATTACK reported here) --------------> silentPhoneCallBlockerASM
	(2) Lock phone screen ==> Start a phone call ==> End phone call  -------------> lockscreenphonecallerASM
	(3) Intercept phone call ==> Set phone ringer to silent mode ==> Lock phone screen ==> block phone call ==> restore phone volume ----------> Current
	(4) Intercept phone call ==> Lock phone screen ==> Set phone ringer to silent mode ==> block phone call ==> restore phone volume 
	 
	*/	

	public SilentLockScreenPhoneCallBlockerASMObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(this.taSubject);
	}
	
	public SilentLockScreenPhoneCallBlockerASMObserver (){
		this.state = new InitialState();
	}

	@Override
	public void update(RingerModeSilentASMEvent e) {
		state = state.update(e);		
	}

	@Override
	public void update(VoiceCallStreamDecreasingASMEvent e) {
		state = state.update(e);		
	}

	@Override
	public void update(KeyguardLockCheckingASMEvent e) {
		state = state.update(e);		
	}

	@Override
	public void update(PhoneCallBlockingASMEvent e) {
		state = state.update(e);		
	}
	
	@Override
	public void update(VoiceCallStreamIncreasingASMEvent e) {
		state = state.update(e);		
	}

	@Override
	public void update(RingerModeNormalASMEvent e) {
		state = state.update(e);
	}
	
	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
