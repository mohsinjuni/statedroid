package patternMatcher.statemachines.asm.nickispycASM;
import patternMatcher.events.asm.DisplayHomeScreenASMEvent;
import patternMatcher.events.asm.ShowCallScreenWithDialpadASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallAnsweringASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOnASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationRestoreASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.statemachines.State;

public abstract class NickiSpyCASMStates extends State{

   public State update(AudioManagerRingerModeOffASMEvent e){return this;}
   public State update(AudioManagerVibrationOffASMEvent e){ return this;}
   public State update(ShowCallScreenWithDialpadASMEvent e){return this;}
   public State update(PhoneCallAnsweringASMEvent e){return this;}
   public State update(DisplayHomeScreenASMEvent e){return this;}
   public State update(PhoneCallBlockingASMEvent e){return this;}
   public State update(AudioManagerVibrationRestoreASMEvent e){ return this;}
   public State update(AudioManagerRingerModeOnASMEvent e){return this;}
   public State update(ContentProviderDeletionASMEvent e){return this;}
   	
}
