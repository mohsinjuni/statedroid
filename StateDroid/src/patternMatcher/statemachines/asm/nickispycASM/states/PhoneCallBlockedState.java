package patternMatcher.statemachines.asm.nickispycASM.states;

import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOnASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOnASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.nickispycASM.NickiSpyCASMStates;
import taintanalyzer.TaintAnalyzer;

public class PhoneCallBlockedState extends NickiSpyCASMStates {

	private TaintAnalyzer ta;

	public PhoneCallBlockedState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public PhoneCallBlockedState() {
	}
	
	@Override
	public State update(AudioManagerRingerModeOnASMEvent e) {
		return (State) new AudioManagerRingerModeOnState(this.ta);
	}


}
