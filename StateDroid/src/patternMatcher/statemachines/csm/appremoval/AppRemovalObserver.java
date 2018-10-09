package patternMatcher.statemachines.csm.appremoval;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.AttackObserver;
import patternMatcher.events.Event;
import patternMatcher.events.csm.appremoval.SetApplicationEnabledEvent;
import patternMatcher.events.csm.appremoval.SetComponentEnabledEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.appremoval.states.InitialState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class AppRemovalObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private Hashtable attackParameters;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public AppRemovalObserver() {
	}

	public AppRemovalObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.attackParameters = new Hashtable();
	}

	@Override
	public void update(SetApplicationEnabledEvent e) {
		state = new InitialState(taSubject);
		state = state.update(e);
	}

	@Override
	public void update(SetComponentEnabledEvent e) {
		state = new InitialState(taSubject);
		state = state.update(e);
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
