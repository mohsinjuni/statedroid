package patternMatcher.statemachines.asm.audiomanagerASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerStreamVolumeChangedASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.audiomanagerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class AudioManagerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	

	public AudioManagerASMObserver (){
		this.state = new InitialState();
	}

	public AudioManagerASMObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

  	@Override
	public void update(AudioManagerStreamVolumeChangedASMEvent e) {
  		this.state = new InitialState(taSubject);
		state = state.update(e);		
	}

  	@Override
	public void update(AudioManagerRingerModeOffASMEvent e) {
  		this.state = new InitialState(taSubject);
		state = state.update(e);		
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}


}
