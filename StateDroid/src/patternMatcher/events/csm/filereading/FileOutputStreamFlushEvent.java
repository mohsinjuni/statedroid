package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class FileOutputStreamFlushEvent extends Event{

	public Event createEvent(){
		return new FileOutputStreamFlushEvent();
	}
}
