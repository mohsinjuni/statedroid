package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class DataOutputStreamDefinedEvent extends Event{

	public Event createEvent(){
		return new DataOutputStreamDefinedEvent();
	}
}
