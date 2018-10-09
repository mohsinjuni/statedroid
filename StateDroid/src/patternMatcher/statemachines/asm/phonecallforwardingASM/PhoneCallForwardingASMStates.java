package patternMatcher.statemachines.asm.phonecallforwardingASM;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.phonecall.PhoneCallAnsweringASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallForwardingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;


public abstract class PhoneCallForwardingASMStates extends State{

	public State update(PhoneCallForwardingASMEvent e){ return this;};	
	
}
