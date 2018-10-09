package patternMatcher.events.csm.mediaRecorder;

import patternMatcher.events.Event;

public class MediaStartEvent extends Event{

	public Event createEvent()
	{
		
		return new MediaStartEvent();
	}
}
