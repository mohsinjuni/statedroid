package patternMatcher.events.csm.uri;

import patternMatcher.events.Event;

public class UriParsedEvent extends Event{

	public Event createEvent()
	{
		return new UriParsedEvent();
	}
}
