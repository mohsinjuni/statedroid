package patternMatcher.events.csm.keyguardmanager;

import patternMatcher.events.Event;

public class KeyguardRestrictedInputModeEvent extends Event{

//	static
//	{
//		EventFactory.getInstance().registerEvent("abortBroadcastEvent", new AbortBroadcastEvent());
//		
//	}
	
	public Event createEvent()
	{
		return new KeyguardRestrictedInputModeEvent();
	}
}
