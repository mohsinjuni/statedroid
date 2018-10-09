package patternMatcher.events.asm.contentprovider;

import patternMatcher.events.Event;

public class ContentProviderDeletionASMEvent extends Event{

	public Event createEvent()
	{
		
		return new ContentProviderDeletionASMEvent();
	}
}
