package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class FileOutputStreamWriteEvent extends Event{

	public Event createEvent(){
		return new FileOutputStreamWriteEvent();
	}
}
