package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class DataOutputStreamWriteEvent extends Event{

	public Event createEvent(){
		return new DataOutputStreamWriteEvent();
	}
}
