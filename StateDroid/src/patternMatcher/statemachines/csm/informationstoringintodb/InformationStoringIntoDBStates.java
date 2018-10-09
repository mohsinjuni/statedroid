package patternMatcher.statemachines.csm.informationstoringintodb;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.InformationLeakerASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.GetExtrasBundleEvent;
import patternMatcher.events.csm.SqlLiteDatabaseInsertEvent;



public abstract class InformationStoringIntoDBStates extends State{

	  public State update(SqlLiteDatabaseInsertEvent e){ return this;};	
	  
}
