package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class ObjectGetClassEvent extends Event{

	public Event createEvent()
	{
		
		return new ObjectGetClassEvent();
	}
}
