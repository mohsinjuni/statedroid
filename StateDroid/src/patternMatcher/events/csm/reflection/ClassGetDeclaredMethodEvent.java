package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class ClassGetDeclaredMethodEvent extends Event{

	public Event createEvent()
	{
		
		return new ClassGetDeclaredMethodEvent();
	}
}
