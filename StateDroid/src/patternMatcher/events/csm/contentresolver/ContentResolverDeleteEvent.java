package patternMatcher.events.csm.contentresolver;

import patternMatcher.events.Event;

public class ContentResolverDeleteEvent extends Event{

//	static
//	{
//		EventFactory.getInstance().registerEvent("CreateFromPduEvent", new CreateFromPduEvent());
//		
//	}
//	
	public Event createEvent()
	{
		
		return new ContentResolverDeleteEvent();
	}
}
