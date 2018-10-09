package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class FileInputStreamDefinedEvent extends Event{

	public Event createEvent(){
		return new FileInputStreamDefinedEvent();
	}
}
