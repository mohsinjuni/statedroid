package patternMatcher.statemachines.asm.phonecallforwardingASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.phonecall.PhoneCallForwardingASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.phonecallforwardingASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class PhoneCallForwardingASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	
	public PhoneCallForwardingASMObserver (TaintAnalyzer taParam)
	{
		this.taSubject = taParam;
		this.state = new InitialState(this.taSubject);
	}
	public PhoneCallForwardingASMObserver (){
		this.state = new InitialState();
	}
	
	@Override
	public void update(PhoneCallForwardingASMEvent e) {
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
