package patternMatcher.statemachines.asm.ringermodesilencerASM;

import patternMatcher.AttackObserver;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.ringermodesilencerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class RingerModeSilencerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public RingerModeSilencerASMObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.state = new InitialState(this.taSubject);
	}

	public RingerModeSilencerASMObserver() {
		this.state = new InitialState();
	}

	@Override
	public void update(RingerModeSilentASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
