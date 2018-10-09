package patternMatcher.statemachines.asm.nickispycASM.states;

import patternMatcher.events.asm.phonecall.PhoneCallAnsweringASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOffASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.nickispycASM.NickiSpyCASMStates;
import taintanalyzer.TaintAnalyzer;

public class ShowCallScreenWithDialpadState extends NickiSpyCASMStates {

	private TaintAnalyzer ta;

	public ShowCallScreenWithDialpadState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public ShowCallScreenWithDialpadState() {
	}
	
	@Override
	public State update(PhoneCallAnsweringASMEvent e) {
		return (State) new PhoneCallAnsweredState(this.ta);
	}


}
