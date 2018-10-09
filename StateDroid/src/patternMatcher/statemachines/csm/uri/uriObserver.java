package patternMatcher.statemachines.csm.uri;

import patternMatcher.AttackObserver;
import patternMatcher.events.csm.uri.UriParsedEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.uri.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class uriObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public uriObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		//		this.taSubject.attach(this);
		this.state = new InitialState(taSubject);
	}

	public uriObserver() {
		this.state = new InitialState(taSubject);
	}

	@Override
	public void update(UriParsedEvent e) {
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
