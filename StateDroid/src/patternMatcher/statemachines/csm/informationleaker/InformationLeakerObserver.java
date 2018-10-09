package patternMatcher.statemachines.csm.informationleaker;

import patternMatcher.AttackObserver;
import patternMatcher.events.asm.InformationLeakerASMEvent;
import patternMatcher.events.csm.SmsSenderEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.informationleaker.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class InformationLeakerObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public InformationLeakerObserver() {
		this.state = new InitialState();
	}

	public InformationLeakerObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

	public void registerObserver(TaintAnalyzer taParam) {

	}

	@Override
	public void update(InformationLeakerASMEvent e) {
		this.state = state.update(e);
	}

	@Override
	public void update(SmsSenderEvent e) {

	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
