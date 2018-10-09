package patternMatcher.statemachines.asm.phonecallblockerASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.phonecallblockerASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class PhoneCallBlockerASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	
	public PhoneCallBlockerASMObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(this.taSubject);
	}
	public PhoneCallBlockerASMObserver (){
		this.state = new InitialState();
	}

	@Override
	public void update(PhoneCallBlockingASMEvent e) {
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
