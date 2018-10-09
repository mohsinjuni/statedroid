package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class CipherInputStreamReadEvent extends Event{

	public Event createEvent(){
		return new CipherInputStreamReadEvent();
	}
}
