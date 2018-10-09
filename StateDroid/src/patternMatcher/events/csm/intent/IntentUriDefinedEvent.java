package patternMatcher.events.csm.intent;

import patternMatcher.events.Event;

public class IntentUriDefinedEvent extends Event{

	public Event createEvent()
	{
		
		return new IntentUriDefinedEvent();
	}
}
