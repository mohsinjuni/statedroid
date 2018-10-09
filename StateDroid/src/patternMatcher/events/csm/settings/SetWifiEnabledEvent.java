package patternMatcher.events.csm.settings;

import patternMatcher.events.Event;

public class SetWifiEnabledEvent extends Event{

	public Event createEvent(){
		return new SetWifiEnabledEvent();
	}
}
