package patternMatcher.statemachines.asm.smsblockautoreplierASM;

import patternMatcher.AttackObserver;
import patternMatcher.events.asm.IncomingSmsAutoReplierASMEvent;
import patternMatcher.events.asm.IncomingSmsBlockerASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smsblockautoreplierASM.states.InitialState;

import taintanalyzer.TaintAnalyzer;

public class SmsBlockAutoReplierASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public SmsBlockAutoReplierASMObserver() {
		this.state = new InitialState();
	}

	public SmsBlockAutoReplierASMObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

	@Override
	public void update(IncomingSmsBlockerASMEvent e) {
		state = state.update(e);
	}

	@Override
	public void update(IncomingSmsAutoReplierASMEvent e) {
		state = state.update(e);
	}
	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
