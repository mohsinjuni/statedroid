package patternMatcher.statemachines.asm.nickispycASM.states;

import patternMatcher.events.asm.ShowCallScreenWithDialpadASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOffASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.nickispycASM.NickiSpyCASMStates;
import patternMatcher.statemachines.asm.smsdeleteandsmsASM.states.ContentProviderDeletionState;
import taintanalyzer.TaintAnalyzer;

public class AudioManagerVibrationOffState extends NickiSpyCASMStates {

	private TaintAnalyzer ta;

	public AudioManagerVibrationOffState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public AudioManagerVibrationOffState() {
	}
	
	@Override
	public State update(ShowCallScreenWithDialpadASMEvent e) {
		return (State) new ShowCallScreenWithDialpadState(this.ta);
	}


}
