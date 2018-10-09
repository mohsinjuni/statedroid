package patternMatcher.statemachines.asm.silentlockscreenphonecallerASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeNormalASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentlockscreenphonecallerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class SilentLockScreenPhoneCallerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	

	public SilentLockScreenPhoneCallerASMObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(this.taSubject);
	}
	
	public SilentLockScreenPhoneCallerASMObserver (){
		this.state = new InitialState();
	}

	@Override
	public void update(KeyguardLockCheckingASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);		
	}

	@Override
	public void update(PhoneCallBlockingASMEvent e) {
		state = state.update(e);		
	}
	
	@Override
	public void update(PhoneCallingASMEvent e) {
		state = state.update(e);		
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
