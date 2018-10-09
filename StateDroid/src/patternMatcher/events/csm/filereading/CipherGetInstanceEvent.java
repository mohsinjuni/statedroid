package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class CipherGetInstanceEvent extends Event{

	public Event createEvent(){
		return new CipherGetInstanceEvent();
	}
}
