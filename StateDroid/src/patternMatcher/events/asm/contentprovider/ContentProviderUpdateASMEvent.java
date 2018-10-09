package patternMatcher.events.asm.contentprovider;

import patternMatcher.events.Event;

public class ContentProviderUpdateASMEvent extends Event{

	public Event createEvent()
	{
		
		return new ContentProviderUpdateASMEvent();
	}
}
