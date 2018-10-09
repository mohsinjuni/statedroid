package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class GetManagerEvent extends Event{

	public Event createEvent()
	{
		
		return new GetManagerEvent();
	}
}
