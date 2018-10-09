package patternMatcher.events.csm.url;

import patternMatcher.events.Event;

public class UrlInitEvent extends Event{

	public Event createEvent()
	{
		return new UrlInitEvent();
	}
}
