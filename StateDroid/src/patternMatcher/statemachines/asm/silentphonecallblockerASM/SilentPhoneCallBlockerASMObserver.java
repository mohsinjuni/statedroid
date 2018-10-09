package patternMatcher.statemachines.asm.silentphonecallblockerASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOnASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentphonecallblockerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class SilentPhoneCallBlockerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	
	/*   Attack definition
	 * 
	(1) RingerModeSilentState ==> PhoneCallBlockerState ==> RingerModeNormalState (ATTACK reported here) --------------> silentPhoneCallBlockerASM
	(2) Lock phone screen ==> Start a phone call ==> End phone call  -------------> lockscreenphonecallerASM
	(3) Intercept phone call ==> Set phone ringer to silent mode ==> Lock phone screen ==> block phone call ==>  (delete call logs)
	(4) Intercept phone call ==> Lock phone screen ==> Set phone ringer to silent mode ==> block phone call ==>  (delete call logs)
	
	*/
	public SilentPhoneCallBlockerASMObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(this.taSubject);
	}
	
	public SilentPhoneCallBlockerASMObserver (){
		this.state = new InitialState();
	}

	@Override
	public void update(AudioManagerRingerModeOnASMEvent e) {
		state = state.update(e);
	}
	
	@Override
	public void update(AudioManagerRingerModeOffASMEvent e) {
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
