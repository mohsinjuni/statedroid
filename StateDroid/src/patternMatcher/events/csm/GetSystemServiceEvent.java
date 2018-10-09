package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class GetSystemServiceEvent extends Event{

	public Event createEvent()
	{
		
		return new GetSystemServiceEvent();
	}
}
