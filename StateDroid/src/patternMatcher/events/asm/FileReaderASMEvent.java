package patternMatcher.events.asm;

import patternMatcher.events.Event;

public class FileReaderASMEvent extends Event{

	public Event createEvent()
	{
		
		return new FileReaderASMEvent();
	}
}
