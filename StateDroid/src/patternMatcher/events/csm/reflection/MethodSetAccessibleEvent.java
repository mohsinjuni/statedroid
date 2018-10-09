package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class MethodSetAccessibleEvent extends Event{

	public Event createEvent()
	{
		return new MethodSetAccessibleEvent();
	}
}
