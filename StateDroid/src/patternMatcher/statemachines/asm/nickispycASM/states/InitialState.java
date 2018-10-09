package patternMatcher.statemachines.asm.nickispycASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.asm.appremoval.AppRemovalASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.nickispycASM.NickiSpyCASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class InitialState extends NickiSpyCASMStates{

	private TaintAnalyzer ta;
	public InitialState(TaintAnalyzer taParam){
		this.ta = taParam;
	}
	public InitialState(){}
	
	@Override
	public State update(AudioManagerRingerModeOffASMEvent e) {
		return (State) new AudioManagerRingerModeOffState(this.ta);
	}
	
}
