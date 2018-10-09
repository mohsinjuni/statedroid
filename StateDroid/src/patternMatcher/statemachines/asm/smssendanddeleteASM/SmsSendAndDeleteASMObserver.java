package patternMatcher.statemachines.asm.smssendanddeleteASM;

import patternMatcher.AttackObserver;
import patternMatcher.events.asm.SmsSenderASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smssendanddeleteASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class SmsSendAndDeleteASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public SmsSendAndDeleteASMObserver() {
		this.state = new InitialState();
	}

	public SmsSendAndDeleteASMObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

	@Override
	public void update(ContentProviderDeletionASMEvent e) {
		state = state.update(e);
	}

	@Override
	public void update(SmsSenderASMEvent e) {
		state = state.update(e);
	}
	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
