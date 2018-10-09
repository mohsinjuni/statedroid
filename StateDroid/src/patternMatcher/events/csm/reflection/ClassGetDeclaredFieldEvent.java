package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class ClassGetDeclaredFieldEvent extends Event{

	public Event createEvent()
	{
		
		return new ClassGetDeclaredFieldEvent();
	}
}
