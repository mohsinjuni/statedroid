package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class ITelephonyEndCallEvent extends Event{

	public Event createEvent()
	{
		
		return new ITelephonyEndCallEvent();
	}
}
