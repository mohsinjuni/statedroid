package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class FilesToStringEvent extends Event{

	public Event createEvent()
	{
		
		return new FilesToStringEvent();
	}
}
