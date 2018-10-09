package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class FileUtilsReadFileToStringEvent extends Event{

	public Event createEvent()
	{
		
		return new FileUtilsReadFileToStringEvent();
	}
}
