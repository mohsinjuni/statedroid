package patternMatcher.events.csm.settings;

import patternMatcher.events.Event;

public class SettingsGlobalPutStringEvent extends Event{

	public Event createEvent(){
		return new SettingsGlobalPutStringEvent();
	}
}
