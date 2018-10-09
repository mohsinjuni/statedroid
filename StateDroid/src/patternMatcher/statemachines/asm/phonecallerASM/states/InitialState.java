package patternMatcher.statemachines.asm.phonecallerASM.states;
import models.cfg.InstructionResponse;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.PhoneCallerReport;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.phonecallerASM.PhoneCallerASMStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class InitialState extends PhoneCallerASMStates{

	private String currInstr="";
	
	private TaintAnalyzer ta;
	public InitialState(){}
	public InitialState(TaintAnalyzer ta){
		this.ta = ta;
	}
	
	@Override
	public State update(PhoneCallingASMEvent e) {
		return new PhoneCallStartedState(ta);
	}

}
