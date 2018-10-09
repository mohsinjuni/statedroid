package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class FileDefinedEvent extends Event{

	public Event createEvent()
	{
		
		return new FileDefinedEvent();
	}
}
