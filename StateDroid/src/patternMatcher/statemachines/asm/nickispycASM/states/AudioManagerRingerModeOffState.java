package patternMatcher.statemachines.asm.nickispycASM.states;

import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOffASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.nickispycASM.NickiSpyCASMStates;
import taintanalyzer.TaintAnalyzer;

public class AudioManagerRingerModeOffState extends NickiSpyCASMStates {

	private TaintAnalyzer ta;

	public AudioManagerRingerModeOffState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public AudioManagerRingerModeOffState() {
	}
	
	@Override
	public State update(AudioManagerVibrationOffASMEvent e) {
		return (State) new AudioManagerVibrationOffState(this.ta);
	}


}
