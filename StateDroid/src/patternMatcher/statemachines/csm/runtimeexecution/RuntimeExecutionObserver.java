package patternMatcher.statemachines.csm.runtimeexecution;
import java.util.Hashtable;

import models.symboltable.SymbolSpace;
import patternMatcher.AttackObserver;
import patternMatcher.events.csm.RuntimeExecutionEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.runtimeexecution.states.InitialState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class RuntimeExecutionObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private Hashtable attackParameters;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public RuntimeExecutionObserver (){}
	
	public RuntimeExecutionObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.attackParameters = new Hashtable();
	}

	@Override
	public void update(RuntimeExecutionEvent e) {
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
