package patternMatcher.statemachines.csm.contentresolver;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.InformationLeakerASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.GetExtrasBundleEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverApplyBatchEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverDeleteEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverInsertEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverQueryEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverUpdateEvent;
import patternMatcher.events.csm.cursor.CursorGetStringEvent;



public abstract class ContentResolverStates extends State{

	  public State update(ContentResolverDeleteEvent e){ return this;};	
	  public State update(ContentResolverApplyBatchEvent e){ return this;};	
	  public State update(ContentResolverUpdateEvent e){ return this;};	
	  public State update(ContentResolverQueryEvent e){ return this;};	
     public State update(ContentResolverInsertEvent e){ return this;}	

	  public State update(CursorGetStringEvent e){ return this;};	

}
