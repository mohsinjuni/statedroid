package patternMatcher.statemachines.asm.nickispycASM.states;

import patternMatcher.events.asm.DisplayHomeScreenASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOffASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.nickispycASM.NickiSpyCASMStates;
import taintanalyzer.TaintAnalyzer;

public class PhoneCallAnsweredState extends NickiSpyCASMStates {

	private TaintAnalyzer ta;

	public PhoneCallAnsweredState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public PhoneCallAnsweredState() {
	}
	
	@Override
	public State update(DisplayHomeScreenASMEvent e) {
		return (State) new DisplayHomeScreenState(this.ta);
	}


}
