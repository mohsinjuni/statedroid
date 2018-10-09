package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class AbortBroadcastEvent extends Event{

	public Event createEvent()
	{
		return new AbortBroadcastEvent();
	}
}
