package patternMatcher.statemachines.csm.mediarecorder;
import patternMatcher.events.csm.mediaRecorder.MediaPrepareEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetAudioSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFileEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFormatEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetVideoSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaStartEvent;
import patternMatcher.statemachines.State;



public abstract class MediaRecorderStates extends State{

   public State update(MediaSetAudioSourceEvent e){ return this;};	
   public State update(MediaSetVideoSourceEvent e){ return this;};	
   public State update(MediaSetOutputFormatEvent e){ return this;};	
   public State update(MediaSetOutputFileEvent e){ return this;};	
   public State update(MediaPrepareEvent e){ return this;};	
   public State update(MediaStartEvent e){ return this;};	
	
}
