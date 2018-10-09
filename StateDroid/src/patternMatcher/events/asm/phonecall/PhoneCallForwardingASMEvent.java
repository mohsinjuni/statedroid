package patternMatcher.events.asm.phonecall;

import patternMatcher.events.Event;

public class PhoneCallForwardingASMEvent extends Event{

	public Event createEvent()
	{
		
		return new PhoneCallForwardingASMEvent();
	}
}
