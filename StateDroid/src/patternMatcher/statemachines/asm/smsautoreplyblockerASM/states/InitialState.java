package patternMatcher.statemachines.asm.smsautoreplyblockerASM.states;

import patternMatcher.events.asm.IncomingSmsAutoReplierASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smsautoreplyblockerASM.SmsAutoReplyBlockerASMStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends SmsAutoReplyBlockerASMStates {

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(IncomingSmsAutoReplierASMEvent e) {
		return (State) new SmsAutoReplierState(this.ta);
	}

}
