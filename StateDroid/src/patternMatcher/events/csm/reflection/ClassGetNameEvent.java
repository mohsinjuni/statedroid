package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class ClassGetNameEvent extends Event{

	public Event createEvent()
	{
		
		return new ClassGetNameEvent();
	}
}
