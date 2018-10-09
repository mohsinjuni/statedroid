package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class BufferedReaderReadDataEvent extends Event{

	public Event createEvent(){
		return new BufferedReaderReadDataEvent();
	}
}
