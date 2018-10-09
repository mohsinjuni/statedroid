package patternMatcher.statemachines.asm.phonecallerASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.phonecallerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class PhoneCallerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	
	public PhoneCallerASMObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(this.taSubject);
	}
	public PhoneCallerASMObserver (){
		this.state = new InitialState();
	}
	
	@Override
	public void update(PhoneCallingASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);		
	}

	@Override
	public void update(PhoneCallBlockingASMEvent e) {
		state = state.update(e);		
	}
		
	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}
}
