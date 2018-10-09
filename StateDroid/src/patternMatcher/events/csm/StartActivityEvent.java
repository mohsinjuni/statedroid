package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class StartActivityEvent extends Event{

	public Event createEvent()
	{
		
		return new StartActivityEvent();
	}
}
