package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class CipherDoFinalEvent extends Event{

	public Event createEvent(){
		return new CipherDoFinalEvent();
	}
}
