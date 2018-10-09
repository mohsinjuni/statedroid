package patternMatcher.statemachines.csm.context;

import models.symboltable.SymbolSpace;
import patternMatcher.AttackObserver;
import patternMatcher.events.csm.GetManagerEvent;
import patternMatcher.events.csm.GetPackageManagerDefinedEvent;
import patternMatcher.events.csm.GetPackageNameDefinedEvent;
import patternMatcher.events.csm.GetSystemServiceEvent;
import patternMatcher.events.csm.context.StartActivityIntentEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.context.states.InitialState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class ContextObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private SymbolSpace localSymbolSpace;

	public ContextObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public ContextObserver() {
		this.state = new InitialState(taSubject);
	}

	@Override
	public void update(GetManagerEvent e) {
		this.state = new InitialState(taSubject);
		state = state.update(e);
	}

	@Override
	public void update(GetPackageManagerDefinedEvent e) {
		this.state = new InitialState(taSubject);
		state = state.update(e);
	}

	@Override
	public void update(GetPackageNameDefinedEvent e) {
		this.state = new InitialState(taSubject);
		state = state.update(e);
	}

	@Override
	public void update(StartActivityIntentEvent e) {
		this.state = new InitialState(taSubject);
		state = state.update(e);
	}

	@Override
	public void update(GetSystemServiceEvent e) {
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
