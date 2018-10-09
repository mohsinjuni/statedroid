package patternMatcher.statemachines.csm.audiomanager;
import java.util.Hashtable;

import patternMatcher.events.csm.GetSystemServiceEvent;
import patternMatcher.events.csm.SetRingerModeEvent;
import patternMatcher.events.csm.audiomanager.AudioMgrSetVibrateSettingEvent;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;
import patternMatcher.events.csm.mediaRecorder.MediaPrepareEvent;
import patternMatcher.events.csm.mediaRecorder.MediaPreviewDisplayEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetAudioSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetCameraEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetMaxDurationEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFileEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFormatEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetPreviewDisplayEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetVideoSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaStartEvent;
import patternMatcher.statemachines.State;



public abstract class AudioManagerStates extends State{

   public State update(SetStreamVolumeEvent e){ return this;};	
   public State update(GetSystemServiceEvent e){ return this;};	
   public State update(SetRingerModeEvent e){ return this;};	
   public State update(AudioMgrSetVibrateSettingEvent e){ return this;};	
	
}
