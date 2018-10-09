package patternMatcher.statemachines.asm.audiovideorecorderASM;

import patternMatcher.AttackObserver;
import patternMatcher.events.asm.audiovideo.AudioRecorderASMEvent;
import patternMatcher.events.asm.audiovideo.VideoRecorderASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.audiovideorecorderASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class AudioVideoRecorderASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public AudioVideoRecorderASMObserver() {
		this.state = new InitialState();
	}

	public AudioVideoRecorderASMObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

	public void registerObserver(TaintAnalyzer taParam) {
	}

	@Override
	public void update(AudioRecorderASMEvent e) {
		state = state.update(e);
	}

	@Override
	public void update(VideoRecorderASMEvent e) {
		state = state.update(e);
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
