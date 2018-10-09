package patternMatcher.events.asm;

import patternMatcher.events.Event;

public class SmsSenderASMEvent extends Event{

	public Event createEvent()
	{
		
		return new SmsSenderASMEvent();
	}
}
