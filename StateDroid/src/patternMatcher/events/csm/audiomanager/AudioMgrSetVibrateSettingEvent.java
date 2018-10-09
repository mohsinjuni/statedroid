package patternMatcher.events.csm.audiomanager;

import patternMatcher.events.Event;

public class AudioMgrSetVibrateSettingEvent extends Event{

	public Event createEvent()
	{
		
		return new AudioMgrSetVibrateSettingEvent();
	}
}
