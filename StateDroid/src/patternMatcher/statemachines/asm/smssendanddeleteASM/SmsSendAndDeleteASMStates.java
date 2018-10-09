package patternMatcher.statemachines.asm.smssendanddeleteASM;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.IncomingSmsAutoReplierASMEvent;
import patternMatcher.events.asm.IncomingSmsBlockerASMEvent;
import patternMatcher.events.asm.SmsSenderASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.GetExtrasBundleEvent;



public abstract class SmsSendAndDeleteASMStates extends State{
	public State update(ContentProviderDeletionASMEvent e){return this; }; 
	public State update(SmsSenderASMEvent e){return this; }; 

}
