package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class BufferedReaderDefinedEvent extends Event{

	public Event createEvent()
	{
		
		return new BufferedReaderDefinedEvent();
	}
}
