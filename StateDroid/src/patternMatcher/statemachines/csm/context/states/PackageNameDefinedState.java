package patternMatcher.statemachines.csm.context.states;
import patternMatcher.statemachines.csm.context.ContextStates;
import taintanalyzer.TaintAnalyzer;


public class PackageNameDefinedState extends ContextStates{
	
	TaintAnalyzer ta;
	public PackageNameDefinedState(){}
	public PackageNameDefinedState(TaintAnalyzer taParam){
		this.ta = taParam;
	}
	

}
