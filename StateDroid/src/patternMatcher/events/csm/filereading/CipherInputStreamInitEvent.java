package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class CipherInputStreamInitEvent extends Event{

	public Event createEvent(){
		return new CipherInputStreamInitEvent();
	}
}
