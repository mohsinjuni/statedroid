package patternMatcher.events.asm.phonecall;

import patternMatcher.events.Event;

public class PhoneCallAnsweringASMEvent extends Event{

	public Event createEvent()
	{
		
		return new PhoneCallAnsweringASMEvent();
	}
}
