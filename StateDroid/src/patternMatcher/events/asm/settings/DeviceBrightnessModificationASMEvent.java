package patternMatcher.events.asm.settings;

import patternMatcher.events.Event;

public class DeviceBrightnessModificationASMEvent extends Event{

	public Event createEvent()
	{
		
		return new DeviceBrightnessModificationASMEvent();
	}
}
