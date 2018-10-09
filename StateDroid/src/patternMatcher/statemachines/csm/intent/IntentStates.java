package patternMatcher.statemachines.csm.intent;
import patternMatcher.events.csm.SendBroadcastEvent;
import patternMatcher.events.csm.intent.IntentActionDefinedEvent;
import patternMatcher.events.csm.intent.IntentActionUriDefinedEvent;
import patternMatcher.events.csm.intent.IntentCategoryDefinedEvent;
import patternMatcher.events.csm.intent.IntentContextClsDefinedEvent;
import patternMatcher.events.csm.intent.IntentDefinedEvent;
import patternMatcher.events.csm.intent.IntentPutExtraEvent;
import patternMatcher.events.csm.intent.IntentSetActionEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import patternMatcher.events.csm.intent.IntentUriDefinedEvent;
import patternMatcher.statemachines.State;

public class IntentStates extends State{

	public State update(IntentActionDefinedEvent e){ return this;};	
	public State update(IntentActionUriDefinedEvent e){ return this;};	
	public State update(IntentUriDefinedEvent e){ return this;};	
	public State update(IntentSetDataEvent e){ return this;};	

	public State update(IntentContextClsDefinedEvent e){ return this;};	
	public State update(IntentSetActionEvent e){ return this;};	
	public State update(IntentPutExtraEvent e){ return this;};	
	public State update(IntentDefinedEvent e){ return this;};	
	public State update(IntentCategoryDefinedEvent e){ return this;};	

	public State update(SendBroadcastEvent e){ return this;};	

}
