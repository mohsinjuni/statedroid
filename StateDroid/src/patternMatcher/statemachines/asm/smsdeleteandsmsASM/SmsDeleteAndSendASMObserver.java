package patternMatcher.statemachines.asm.smsdeleteandsmsASM;

import patternMatcher.AttackObserver;
import patternMatcher.events.asm.IncomingSmsAutoReplierASMEvent;
import patternMatcher.events.asm.IncomingSmsBlockerASMEvent;
import patternMatcher.events.asm.SmsSenderASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smsdeleteandsmsASM.states.InitialState;

import taintanalyzer.TaintAnalyzer;

public class SmsDeleteAndSendASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public SmsDeleteAndSendASMObserver() {
		this.state = new InitialState();
	}

	public SmsDeleteAndSendASMObserver(TaintAnalyzer taParam) {
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
