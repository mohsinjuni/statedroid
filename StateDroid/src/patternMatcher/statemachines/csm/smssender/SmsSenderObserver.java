package patternMatcher.statemachines.csm.smssender;

import patternMatcher.AttackObserver;
import patternMatcher.events.csm.SmsSenderEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.smssender.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class SmsSenderObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public SmsSenderObserver() {
		this.state = new InitialState();
	}

	public SmsSenderObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

	@Override
	public void update(SmsSenderEvent e) {
		state = state.update(e);
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
