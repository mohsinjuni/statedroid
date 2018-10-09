package patternMatcher.events.csm.intent;

import patternMatcher.events.Event;

public class IntentActionUriDefinedEvent extends Event{

	public Event createEvent()
	{
		return new IntentActionUriDefinedEvent();
	}
}
