package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class FileInputStreamReadEvent extends Event{

	public Event createEvent(){
		return new FileInputStreamReadEvent();
	}
}
