package patternMatcher.events.csm.cursor;

import patternMatcher.events.Event;

public class CursorGetStringEvent extends Event{

//	static
//	{
//		EventFactory.getInstance().registerEvent("abortBroadcastEvent", new AbortBroadcastEvent());
//		
//	}
	
	public Event createEvent()
	{
		return new CursorGetStringEvent();
	}
}
