package patternMatcher.statemachines.asm.smsblockautoreplierASM.states;

import patternMatcher.events.asm.IncomingSmsBlockerASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smsblockautoreplierASM.SmsBlockAutoReplierASMStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends SmsBlockAutoReplierASMStates {

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(IncomingSmsBlockerASMEvent e) {
		return (State) new SmsBlockedState(this.ta);
	}

}
