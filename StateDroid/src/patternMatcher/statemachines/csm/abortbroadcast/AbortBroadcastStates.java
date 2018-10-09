package patternMatcher.statemachines.csm.abortbroadcast;
import java.util.Hashtable;

import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.GetSystemServiceEvent;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;
import patternMatcher.events.csm.keyguardmanager.KeyguardRestrictedInputModeEvent;
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



public abstract class AbortBroadcastStates extends State{

   public State update(AbortBroadcastEvent e){ return this;};	
	
}
