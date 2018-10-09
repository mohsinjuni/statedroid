package patternMatcher.events.csm.settings;

import patternMatcher.events.Event;

public class SettingsSystemPutIntEvent extends Event{

	public Event createEvent(){
		return new SettingsSystemPutIntEvent();
	}
}
