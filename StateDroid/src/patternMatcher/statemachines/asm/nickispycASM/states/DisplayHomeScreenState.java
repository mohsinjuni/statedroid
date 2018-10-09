package patternMatcher.statemachines.asm.nickispycASM.states;

import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOffASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.nickispycASM.NickiSpyCASMStates;
import patternMatcher.statemachines.asm.smsdeleteandsmsASM.states.ContentProviderDeletionState;
import taintanalyzer.TaintAnalyzer;

public class DisplayHomeScreenState extends NickiSpyCASMStates {

	private TaintAnalyzer ta;

	public DisplayHomeScreenState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public DisplayHomeScreenState() {
	}
	
	@Override
	public State update(PhoneCallBlockingASMEvent e) {
		return (State) new PhoneCallBlockedState(this.ta);
	}


}
