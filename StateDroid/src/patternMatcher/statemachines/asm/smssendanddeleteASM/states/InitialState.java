package patternMatcher.statemachines.asm.smssendanddeleteASM.states;

import patternMatcher.events.asm.SmsSenderASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smssendanddeleteASM.SmsSendAndDeleteASMStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends SmsSendAndDeleteASMStates {

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(SmsSenderASMEvent e) {
		return (State) new SmsSenderState(this.ta);
	}

}
