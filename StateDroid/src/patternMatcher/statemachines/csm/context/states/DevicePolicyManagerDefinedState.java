package patternMatcher.statemachines.csm.context.states;
import patternMatcher.statemachines.csm.context.ContextStates;
import taintanalyzer.TaintAnalyzer;


public class DevicePolicyManagerDefinedState extends ContextStates{
	
	TaintAnalyzer ta;
	public DevicePolicyManagerDefinedState(){}
	public DevicePolicyManagerDefinedState(TaintAnalyzer taParam){
		this.ta = taParam;
	}
	
}
