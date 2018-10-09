package patternMatcher.events.csm.mediaRecorder;

import patternMatcher.events.Event;

public class MediaSetOutputFormatEvent extends Event{

	public Event createEvent()
	{
		
		return new MediaSetOutputFormatEvent();
	}
}
