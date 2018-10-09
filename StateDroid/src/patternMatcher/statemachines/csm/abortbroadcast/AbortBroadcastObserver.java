package patternMatcher.statemachines.csm.abortbroadcast;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.AttackObserver;
import patternMatcher.events.Event;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.abortbroadcast.states.InitialState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class AbortBroadcastObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private Hashtable attackParameters;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public AbortBroadcastObserver (){}
	
	public AbortBroadcastObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.attackParameters = new Hashtable();
	}

	@Override
	public void update(AbortBroadcastEvent e) {
//	0xae invoke-virtual v7, Lcmp/netsentry/list/f;->abortBroadcast()V
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
