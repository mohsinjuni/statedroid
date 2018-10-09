package patternMatcher.statemachines.csm.incomingsmsautoreplier;
import patternMatcher.AttackObserver;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.SmsSenderEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.incomingsmsautoreplier.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class IncomingSmsAutoReplierObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public IncomingSmsAutoReplierObserver (){
		this.state = new InitialState();
	}

	public IncomingSmsAutoReplierObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
	}
	public void registerObserver (TaintAnalyzer taParam){}

	@Override
	public void update(SmsSenderEvent e) {
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
