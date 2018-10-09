package patternMatcher.events.asm.phonevolume;

import patternMatcher.events.Event;

public class AudioManagerStreamVolumeChangedASMEvent extends Event{

	public Event createEvent()
	{
		
		return new AudioManagerStreamVolumeChangedASMEvent();
	}
}
