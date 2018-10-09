package patternMatcher.statemachines.asm.smsautoreplyblockerASM;

import patternMatcher.AttackObserver;
import patternMatcher.events.asm.IncomingSmsAutoReplierASMEvent;
import patternMatcher.events.asm.IncomingSmsBlockerASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smsautoreplyblockerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class SmsAutoReplyBlockerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public SmsAutoReplyBlockerASMObserver() {
		this.state = new InitialState();
	}

	public SmsAutoReplyBlockerASMObserver(TaintAnalyzer taParam) {
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
