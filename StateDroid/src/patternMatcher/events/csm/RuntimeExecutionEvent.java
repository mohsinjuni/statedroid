package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class RuntimeExecutionEvent extends Event{

	public Event createEvent(){
		return new RuntimeExecutionEvent();
	}
}
