package patternMatcher.statemachines.asm.appremovalASM;

import patternMatcher.AttackObserver;
import patternMatcher.events.asm.appremoval.AppRemovalASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerStreamVolumeChangedASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.appremovalASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class AppRemovalASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public AppRemovalASMObserver() {
		this.state = new InitialState();
	}

	public AppRemovalASMObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

	@Override
	public void update(AppRemovalASMEvent e) {
		this.state = new InitialState(taSubject);
		state = state.update(e);
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
