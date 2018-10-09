package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class InputStreamReaderDefinedEvent extends Event{

	public Event createEvent()
	{
		
		return new InputStreamReaderDefinedEvent();
	}
}
