package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class FieldGetByKeyEvent extends Event{

	public Event createEvent(){
		return new FieldGetByKeyEvent();
	}
}
