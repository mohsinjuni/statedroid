package patternMatcher.events.asm.contentprovider;

import patternMatcher.events.Event;

public class ContentProviderInsertASMEvent extends Event{

	public Event createEvent()
	{
		
		return new ContentProviderInsertASMEvent();
	}
}
