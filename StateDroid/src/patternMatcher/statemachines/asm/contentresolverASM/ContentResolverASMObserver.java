package patternMatcher.statemachines.asm.contentresolverASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderInsertASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderUpdateASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.contentresolverASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;


public class ContentResolverASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	
	public ContentResolverASMObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState();
	}
	public ContentResolverASMObserver (){
		this.state = new InitialState();
	}
	
	@Override
	public void update(ContentProviderDeletionASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);		
	}
	
	@Override
	public void update(ContentProviderUpdateASMEvent e) {
		this.state = new InitialState(this.taSubject);
		state = state.update(e);		
	}

	@Override
	public void update(ContentProviderInsertASMEvent e) {
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
