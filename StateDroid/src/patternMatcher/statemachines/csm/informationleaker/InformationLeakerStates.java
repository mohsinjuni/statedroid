package patternMatcher.statemachines.csm.informationleaker;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.InformationLeakerASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.GetExtrasBundleEvent;



public abstract class InformationLeakerStates extends State{

	  public State update(InformationLeakerASMEvent e){ return this;};	
	  
}
