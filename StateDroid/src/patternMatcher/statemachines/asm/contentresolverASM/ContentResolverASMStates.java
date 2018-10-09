package patternMatcher.statemachines.asm.contentresolverASM;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderInsertASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderUpdateASMEvent;
import patternMatcher.statemachines.State;


public abstract class ContentResolverASMStates extends State{

	public State update(ContentProviderDeletionASMEvent e){ return this;};	
	public State update(ContentProviderUpdateASMEvent e){ return this;};	
	public State update(ContentProviderInsertASMEvent e){ return this;};	
	
}
