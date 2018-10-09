package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class ClassGetMethodEvent extends Event{

	public Event createEvent()
	{
		
		return new ClassGetMethodEvent();
	}
}
