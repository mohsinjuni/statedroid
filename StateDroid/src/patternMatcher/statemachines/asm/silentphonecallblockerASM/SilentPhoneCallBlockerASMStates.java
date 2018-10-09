package patternMatcher.statemachines.asm.silentphonecallblockerASM;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOnASMEvent;
import patternMatcher.statemachines.State;

public abstract class SilentPhoneCallBlockerASMStates extends State{

	public State update(PhoneCallBlockingASMEvent e){ return this;};	
	public State update(AudioManagerRingerModeOnASMEvent e){ return this;};	
	public State update(AudioManagerRingerModeOffASMEvent e){ return this;};	
	
}
