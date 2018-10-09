package patternMatcher.statemachines.asm.phonecallASM;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.phonecall.PhoneCallAnsweringASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;


public abstract class PhoneCallASMStates extends State{

	public State update(PhoneCallingASMEvent e){ return this;};	
	public State update(PhoneCallBlockingASMEvent e){ return this;};
	public State update(PhoneCallAnsweringASMEvent e){ return this;};
	
}
