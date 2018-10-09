package patternMatcher.statemachines.asm.keyguardmanagerASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.keyguardmanagerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class KeyguardManagerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	

	public KeyguardManagerASMObserver (){
		this.state = new InitialState();
	}

	public KeyguardManagerASMObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

  	@Override
	public void update(KeyguardLockCheckingASMEvent e) {
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
