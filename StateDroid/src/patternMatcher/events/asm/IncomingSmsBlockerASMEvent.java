package patternMatcher.events.asm;

import patternMatcher.events.Event;

public class IncomingSmsBlockerASMEvent extends Event{

	public Event createEvent()
	{
		
		return new IncomingSmsBlockerASMEvent();
	}
}
