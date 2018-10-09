package patternMatcher.events.csm.contentresolver;

import patternMatcher.events.Event;

public class ContentResolverInsertEvent extends Event{

	public Event createEvent()
	{
		
		return new ContentResolverInsertEvent();
	}
}
