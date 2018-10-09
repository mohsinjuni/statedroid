package patternMatcher.statemachines.asm.smsdeleteandsmsASM.states;

import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.smsdeleteandsmsASM.SmsDeleteAndSendASMStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends SmsDeleteAndSendASMStates {

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(ContentProviderDeletionASMEvent e) {
		return (State) new ContentProviderDeletionState(this.ta);
	}

}
