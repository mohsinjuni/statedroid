package patternMatcher.statemachines.asm.filereaderASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.FileReaderASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.filereaderASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class FileReaderASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	

	public FileReaderASMObserver (){
		this.state = new InitialState();
	}

	public FileReaderASMObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}
 
  	@Override
	public void update(FileReaderASMEvent e) {
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
