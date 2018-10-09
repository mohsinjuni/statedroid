package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class SmsSenderEvent extends Event{

	public Event createEvent()
	{
		
		return new SmsSenderEvent();
	}
}
