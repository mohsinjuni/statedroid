package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class FieldSetAccessibleEvent extends Event{

	public Event createEvent(){
		return new FieldSetAccessibleEvent();
	}
}
