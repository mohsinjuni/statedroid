package patternMatcher.statemachines.csm.streamvolumemodifier;

import patternMatcher.AttackObserver;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.streamvolumemodifier.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class StreamVolumeModifierObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public StreamVolumeModifierObserver() {
		this.state = new InitialState();
	}

	public StreamVolumeModifierObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

	public void registerObserver(TaintAnalyzer taParam) {

	}

	@Override
	public void update(SetStreamVolumeEvent e) {
		state = state.update(e);
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
