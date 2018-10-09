package patternMatcher.events.csm.intent;

import patternMatcher.events.Event;

public class IntentSetDataEvent extends Event{

	public Event createEvent()
	{
		
		return new IntentSetDataEvent();
	}
}
