package patternMatcher.statemachines.csm.streamvolumemodifier;
import patternMatcher.events.csm.GetSystemServiceEvent;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;
import patternMatcher.statemachines.State;
import taintanalyzer.TaintAnalyzer;



public abstract class StreamVolumeModifierStates extends State{

	public State update(SetStreamVolumeEvent e){ return this;};	
	
}
