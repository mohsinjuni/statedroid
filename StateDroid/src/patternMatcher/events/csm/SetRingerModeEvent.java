package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class SetRingerModeEvent extends Event{

	public Event createEvent()
	{
		
		return new SetRingerModeEvent();
	}
}
