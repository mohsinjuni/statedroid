package patternMatcher.statemachines.csm.uri;
import patternMatcher.events.csm.uri.UriParsedEvent;
import patternMatcher.statemachines.State;
import taintanalyzer.TaintAnalyzer;

public class uriStates extends State{

	public State update(UriParsedEvent e){ return this;};	
   	
}
