package patternMatcher.events.csm.mediaRecorder;

import patternMatcher.events.Event;

public class MediaSetAudioSourceEvent extends Event{

	public Event createEvent()
	{
		
		return new MediaSetAudioSourceEvent();
	}
}
