package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class FileReaderDefinedEvent extends Event{

	public Event createEvent()
	{
		
		return new FileReaderDefinedEvent();
	}
}
