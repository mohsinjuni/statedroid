package patternMatcher.statemachines.asm.silentphonecallerASM;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;

public abstract class SilentPhoneCallerASMStates extends State{

	public State update(VoiceCallStreamDecreasingASMEvent e){ return this;};	
	public State update(PhoneCallingASMEvent e){ return this;};	
	
}
