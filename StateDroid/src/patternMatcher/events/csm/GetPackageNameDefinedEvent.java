package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class GetPackageNameDefinedEvent extends Event{

//	static
//	{
//		EventFactory.getInstance().registerEvent("abortBroadcastEvent", new AbortBroadcastEvent());
//		
//	}
	
	public Event createEvent()
	{
		return new GetPackageNameDefinedEvent();
	}
}
