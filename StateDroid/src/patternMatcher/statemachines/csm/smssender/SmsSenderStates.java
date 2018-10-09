package patternMatcher.statemachines.csm.smssender;
import patternMatcher.statemachines.State;

import patternMatcher.events.csm.SmsSenderEvent;



public abstract class SmsSenderStates extends State{

	 public State update(SmsSenderEvent e){ return this;}	

}
