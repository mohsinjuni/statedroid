package patternMatcher.events.asm.phonecall;

import patternMatcher.events.Event;

public class PhoneCallBlockingASMEvent extends Event{

	public Event createEvent()
	{
		
		return new PhoneCallBlockingASMEvent();
	}
}
