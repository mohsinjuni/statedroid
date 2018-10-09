package patternMatcher.events.csm.mediaRecorder;

import patternMatcher.events.Event;

public class MediaSetOutputFileEvent extends Event{

	public Event createEvent()
	{
		
		return new MediaSetOutputFileEvent();
	}
}
