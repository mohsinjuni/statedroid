package patternMatcher.events.csm.filereading;

import patternMatcher.events.Event;

public class ScannerReadDataEvent extends Event{

	public Event createEvent()
	{
		
		return new ScannerReadDataEvent();
	}
}
