package patternMatcher.events.csm.mediaRecorder;

import patternMatcher.events.Event;

public class MediaSetVideoSourceEvent extends Event{

	public Event createEvent()
	{
		
		return new MediaSetVideoSourceEvent();
	}
}
