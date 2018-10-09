package patternMatcher.events.csm.intent;

import patternMatcher.events.Event;

public class IntentSetActionEvent extends Event{

	public Event createEvent()
	{
		
		return new IntentSetActionEvent();
	}
}
