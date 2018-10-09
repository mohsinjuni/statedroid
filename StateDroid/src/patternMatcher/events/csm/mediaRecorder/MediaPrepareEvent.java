package patternMatcher.events.csm.mediaRecorder;

import patternMatcher.events.Event;

public class MediaPrepareEvent extends Event{

	public Event createEvent()
	{
		
		return new MediaPrepareEvent();
	}
}
