package patternMatcher.statemachines.csm.resetpassword;
import java.util.Hashtable;

import models.symboltable.SymbolSpace;
import patternMatcher.AttackObserver;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.LockNowEvent;
import patternMatcher.events.csm.ResetPasswordEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.resetpassword.states.InitialState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class ResetPasswordObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private Hashtable attackParameters;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public ResetPasswordObserver (){}
	
	public ResetPasswordObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.attackParameters = new Hashtable();
	}

	@Override
	public void update(ResetPasswordEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);
	}

	@Override
	public void update(LockNowEvent e) {
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
