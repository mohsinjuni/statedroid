package patternMatcher.events.csm.reflection;

import patternMatcher.events.Event;

public class ITelephonyAnswerRingingCallEvent extends Event{

	public Event createEvent()
	{
		
		return new ITelephonyAnswerRingingCallEvent();
	}
}
