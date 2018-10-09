package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class SendBroadcastEvent extends Event{

	public Event createEvent()
	{
		return new SendBroadcastEvent();
	}
}
