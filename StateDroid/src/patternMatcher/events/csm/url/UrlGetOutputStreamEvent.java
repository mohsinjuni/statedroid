package patternMatcher.events.csm.url;

import patternMatcher.events.Event;

public class UrlGetOutputStreamEvent extends Event{

	public Event createEvent()
	{
		return new UrlGetOutputStreamEvent();
	}
}
