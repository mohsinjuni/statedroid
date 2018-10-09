package patternMatcher.statemachines.asm.audiovideorecorderASM;
import patternMatcher.events.asm.audiovideo.AudioRecorderASMEvent;
import patternMatcher.events.asm.audiovideo.VideoRecorderASMEvent;
import patternMatcher.statemachines.State;



public abstract class AudioVideoRecorderASMStates extends State{

   public State update(AudioRecorderASMEvent e){ return this;};	
   public State update(VideoRecorderASMEvent e){ return this;};	
	
}
