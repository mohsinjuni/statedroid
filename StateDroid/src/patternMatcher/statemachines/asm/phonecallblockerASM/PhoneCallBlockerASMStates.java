package patternMatcher.statemachines.asm.phonecallblockerASM;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;

public abstract class PhoneCallBlockerASMStates extends State{

	public State update(PhoneCallBlockingASMEvent e){ return this;};	
	
}
