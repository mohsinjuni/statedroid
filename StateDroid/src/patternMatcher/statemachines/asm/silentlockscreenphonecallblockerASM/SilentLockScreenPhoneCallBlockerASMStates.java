package patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeNormalASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;

public abstract class SilentLockScreenPhoneCallBlockerASMStates extends State{

	public State update(RingerModeSilentASMEvent e){ return this;};	
//OR
	public State update(VoiceCallStreamDecreasingASMEvent e){ return this;};	
	public State update(KeyguardLockCheckingASMEvent e){ return this;};	
	public State update(PhoneCallBlockingASMEvent e){ return this;};	
	public State update(RingerModeNormalASMEvent e){ return this;};	
//OR	
	public State update(VoiceCallStreamIncreasingASMEvent e){ return this;};	
	
}
