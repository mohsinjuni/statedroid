package patternMatcher.events.csm;

import patternMatcher.events.Event;

public class ResetPasswordEvent extends Event{

//	static
//	{
//		EventFactory.getInstance().registerEvent("CreateFromPduEvent", new CreateFromPduEvent());
//		
//	}
//	
	public Event createEvent()
	{
		
		return new ResetPasswordEvent();
	}
}
