package patternMatcher.statemachines.asm.nickispycASM.states;

import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationRestoreASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.nickispycASM.NickiSpyCASMStates;
import taintanalyzer.TaintAnalyzer;

public class AudioManagerRingerModeOnState extends NickiSpyCASMStates {

	private TaintAnalyzer ta;

	public AudioManagerRingerModeOnState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public AudioManagerRingerModeOnState() {
	}
	
	@Override
	public State update(AudioManagerVibrationRestoreASMEvent e) {
		return (State) new AudioManagerVibrationRestoreState(this.ta);
	}

}
