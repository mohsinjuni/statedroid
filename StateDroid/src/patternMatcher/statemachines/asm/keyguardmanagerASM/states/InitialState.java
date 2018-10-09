package patternMatcher.statemachines.asm.keyguardmanagerASM.states;

import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.keyguardmanagerASM.KeyguardManagerASMStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends KeyguardManagerASMStates {

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(KeyguardLockCheckingASMEvent e) {

		return this;
	}

}
