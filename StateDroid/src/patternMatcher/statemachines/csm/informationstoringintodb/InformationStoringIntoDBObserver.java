package patternMatcher.statemachines.csm.informationstoringintodb;
import patternMatcher.AttackObserver;
import patternMatcher.events.csm.SqlLiteDatabaseInsertEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.informationstoringintodb.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class InformationStoringIntoDBObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	

	public InformationStoringIntoDBObserver (){
		this.state = new InitialState();
	}

	public InformationStoringIntoDBObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

	public void registerObserver (TaintAnalyzer taParam){
	}

	
	@Override
	public void update(SqlLiteDatabaseInsertEvent e){ 
		this.state = state.update(e);
	}
		public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}


}
