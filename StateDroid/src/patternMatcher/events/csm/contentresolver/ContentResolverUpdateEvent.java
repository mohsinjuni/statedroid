package patternMatcher.events.csm.contentresolver;

import patternMatcher.events.Event;

public class ContentResolverUpdateEvent extends Event{

	public Event createEvent()
	{
		
		return new ContentResolverUpdateEvent();
	}
}
