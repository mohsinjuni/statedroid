package patternMatcher.statemachines.csm.context;
import patternMatcher.events.csm.GetManagerEvent;
import patternMatcher.events.csm.GetPackageManagerDefinedEvent;
import patternMatcher.events.csm.GetPackageNameDefinedEvent;
import patternMatcher.events.csm.GetSystemServiceEvent;
import patternMatcher.events.csm.context.StartActivityIntentEvent;
import patternMatcher.statemachines.State;

public class ContextStates extends State{

	public State update(StartActivityIntentEvent e){ return this;};	
	public State update(GetSystemServiceEvent e){ return this;};	
    public State update(GetPackageManagerDefinedEvent e){ return this;};	
    public State update(GetPackageNameDefinedEvent e){ return this;};	
    public State update(GetManagerEvent e){ return this;};	
}
