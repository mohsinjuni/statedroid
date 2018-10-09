package patternMatcher.events.csm.intent;

import patternMatcher.events.Event;

public class IntentContextClsDefinedEvent extends Event{

	public Event createEvent()
	{
		return new IntentContextClsDefinedEvent();
	}
}
