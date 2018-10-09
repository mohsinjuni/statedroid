package patternMatcher.events.csm.mediaRecorder;

import patternMatcher.events.Event;

public class MediaSetMaxDurationEvent extends Event{

	public Event createEvent()
	{
		
		return new MediaSetMaxDurationEvent();
	}
}
