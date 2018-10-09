package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class ScannerDefinedEvent extends Event{

	public Event createEvent()
	{
		
		return new ScannerDefinedEvent();
	}
}
