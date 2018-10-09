package patternMatcher.statemachines.asm.silentphonecallerASM;

import patternMatcher.AttackObserver;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.silentphonecallerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class SilentPhoneCallerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public SilentPhoneCallerASMObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		//		this.taSubject.attach(this);
		this.state = new InitialState();
	}

	public SilentPhoneCallerASMObserver() {
		//		this.taSubject = taParam;
		//		this.taSubject.attach(this);
		this.state = new InitialState();
	}

	public void registerObserver(TaintAnalyzer taParam) {

	}

	@Override
	public void update(VoiceCallStreamDecreasingASMEvent e) {
		state = state.update(e); // I don't see any need of passing current statemachineobject. 
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
