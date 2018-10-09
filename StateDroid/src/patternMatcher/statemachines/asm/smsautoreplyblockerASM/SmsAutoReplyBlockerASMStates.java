package patternMatcher.statemachines.asm.smsautoreplyblockerASM;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.IncomingSmsAutoReplierASMEvent;
import patternMatcher.events.asm.IncomingSmsBlockerASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.GetExtrasBundleEvent;



public abstract class SmsAutoReplyBlockerASMStates extends State{

	public State update(IncomingSmsBlockerASMEvent e){return this; }; 
	public State update(IncomingSmsAutoReplierASMEvent e){return this; }; 

}
