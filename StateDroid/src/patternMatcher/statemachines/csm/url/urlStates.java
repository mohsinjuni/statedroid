package patternMatcher.statemachines.csm.url;
import patternMatcher.events.csm.filereading.DataOutputStreamDefinedEvent;
import patternMatcher.events.csm.url.UrlGetOutputStreamEvent;
import patternMatcher.events.csm.url.UrlInitEvent;
import patternMatcher.events.csm.url.UrlOpenConnectionEvent;
import patternMatcher.statemachines.State;

public class urlStates extends State{

	public State update(UrlInitEvent e){ return this;};	
	public State update(UrlOpenConnectionEvent e){ return this;};	
	public State update(UrlGetOutputStreamEvent e){ return this;};	
	public State update(DataOutputStreamDefinedEvent e){ return this;};	
   	
}
