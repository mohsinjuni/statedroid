package patternMatcher.events.csm.appremoval;

import patternMatcher.events.Event;

public class SetApplicationEnabledEvent extends Event{

//	static
//	{
//		EventFactory.getInstance().registerEvent("abortBroadcastEvent", new AbortBroadcastEvent());
//		
//	}
	
	public Event createEvent()
	{
		return new SetApplicationEnabledEvent();
	}
}
