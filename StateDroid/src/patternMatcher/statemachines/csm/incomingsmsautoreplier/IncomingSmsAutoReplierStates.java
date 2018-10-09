package patternMatcher.statemachines.csm.incomingsmsautoreplier;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.GetExtrasBundleEvent;
import patternMatcher.events.csm.SmsSenderEvent;



public abstract class IncomingSmsAutoReplierStates extends State{

	 public State update(SmsSenderEvent e){ return this;}	

}
