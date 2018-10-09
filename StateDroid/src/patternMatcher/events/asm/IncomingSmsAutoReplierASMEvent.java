package patternMatcher.events.asm;

import patternMatcher.events.Event;

public class IncomingSmsAutoReplierASMEvent extends Event{

	public Event createEvent()
	{
		
		return new IncomingSmsAutoReplierASMEvent();
	}
}
