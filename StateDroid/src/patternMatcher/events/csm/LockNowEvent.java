package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class LockNowEvent extends Event{

//	static
//	{
//		EventFactory.getInstance().registerEvent("CreateFromPduEvent", new CreateFromPduEvent());
//		
//	}
//	
	public Event createEvent()
	{
		
		return new LockNowEvent();
	}
}
