package patternMatcher.events.csm.url;

import patternMatcher.events.Event;

public class UrlOpenConnectionEvent extends Event{

	public Event createEvent()
	{
		return new UrlOpenConnectionEvent();
	}
}
