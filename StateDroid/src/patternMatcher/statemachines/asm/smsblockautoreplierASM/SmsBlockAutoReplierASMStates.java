package patternMatcher.statemachines.asm.smsblockautoreplierASM;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.IncomingSmsAutoReplierASMEvent;
import patternMatcher.events.asm.IncomingSmsBlockerASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.GetExtrasBundleEvent;



public abstract class SmsBlockAutoReplierASMStates extends State{

	public State update(IncomingSmsBlockerASMEvent e){return this; }; 
	public State update(IncomingSmsAutoReplierASMEvent e){return this; }; 

}
