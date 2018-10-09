package patternMatcher.events.csm.audiomanager;

import patternMatcher.events.Event;

public class SetStreamVolumeEvent extends Event{

	public Event createEvent()
	{
		
		return new SetStreamVolumeEvent();
	}
}
