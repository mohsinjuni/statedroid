package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class ITelephonySilenceRingerEvent extends Event{

	public Event createEvent()
	{
		
		return new ITelephonySilenceRingerEvent();
	}
}
