package patternMatcher.statemachines.asm.keyguardmanagerASM;
import java.util.Hashtable;

import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.events.asm.audiovideo.AudioRecorderASMEvent;
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



public abstract class KeyguardManagerASMStates extends State{

	   public State update(KeyguardLockCheckingASMEvent e){ return this;};	
	
}
