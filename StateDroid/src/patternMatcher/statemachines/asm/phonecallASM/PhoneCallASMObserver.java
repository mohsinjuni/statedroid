package patternMatcher.statemachines.asm.phonecallASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.phonecall.PhoneCallAnsweringASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.phonecallASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class PhoneCallASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	
	public PhoneCallASMObserver (TaintAnalyzer taParam)
	{
		this.taSubject = taParam;
		this.state = new InitialState(this.taSubject);
	}
	public PhoneCallASMObserver (){
		this.state = new InitialState();
	}
	
	@Override
	public void update(PhoneCallBlockingASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);		
	}
	
	@Override
	public void update(PhoneCallingASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);		
	}
	
	@Override
	public void update(PhoneCallAnsweringASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);		
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}


}
