package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class BufferedOutputStreamWriteEvent extends Event{

	public Event createEvent()
	{
		
		return new BufferedOutputStreamWriteEvent();
	}
}
