package patternMatcher.events.csm.appremoval;

import patternMatcher.events.Event;

public class SetComponentEnabledEvent extends Event{

//	static
//	{
//		EventFactory.getInstance().registerEvent("abortBroadcastEvent", new AbortBroadcastEvent());
//		
//	}
	
	public Event createEvent()
	{
		return new SetComponentEnabledEvent();
	}
}
