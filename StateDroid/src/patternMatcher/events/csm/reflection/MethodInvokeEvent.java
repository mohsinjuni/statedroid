package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class MethodInvokeEvent extends Event{

	public Event createEvent()
	{
		
		return new MethodInvokeEvent();
	}
}
