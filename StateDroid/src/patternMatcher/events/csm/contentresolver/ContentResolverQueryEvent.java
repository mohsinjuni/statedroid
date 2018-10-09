package patternMatcher.events.csm.contentresolver;

import patternMatcher.events.Event;

public class ContentResolverQueryEvent extends Event{

	public Event createEvent()
	{
		
		return new ContentResolverQueryEvent();
	}
}
