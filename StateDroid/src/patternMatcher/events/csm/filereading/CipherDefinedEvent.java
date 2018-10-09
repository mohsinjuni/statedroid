package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class CipherDefinedEvent extends Event{

	public Event createEvent(){
		return new CipherDefinedEvent();
	}
}
