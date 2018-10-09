package patternMatcher.events.csm.intent;

import patternMatcher.events.Event;

public class IntentDefinedEvent extends Event{

	public Event createEvent()
	{
		return new IntentDefinedEvent();
	}
}
