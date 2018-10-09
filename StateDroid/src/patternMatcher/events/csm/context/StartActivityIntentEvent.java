package patternMatcher.events.csm.context;

import patternMatcher.events.Event;

public class StartActivityIntentEvent extends Event{

	public Event createEvent()
	{
		return new StartActivityIntentEvent();
	}
}
