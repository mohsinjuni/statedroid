package patternMatcher.events.csm.contentresolver;

import patternMatcher.events.Event;

public class ContentResolverApplyBatchEvent extends Event{

	public Event createEvent()
	{
		
		return new ContentResolverApplyBatchEvent();
	}
}
