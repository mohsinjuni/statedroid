package patternMatcher.statemachines.asm.settingstogglerASM;
import patternMatcher.statemachines.State;

import patternMatcher.events.Event;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeNormalASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.events.asm.settings.DeviceAirPlaneModeModificationASMEvent;
import patternMatcher.events.asm.settings.DeviceBrightnessModificationASMEvent;
import patternMatcher.events.asm.settings.MobileDataTogglerASMEvent;
import patternMatcher.events.asm.settings.WifiTogglerASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;

public abstract class SettingsTogglerASMStates extends State{

	public State update(WifiTogglerASMEvent e){ return this;};	
	public State update(DeviceBrightnessModificationASMEvent e){ return this;};	
	public State update(DeviceAirPlaneModeModificationASMEvent e){ return this;};	
	public State update(MobileDataTogglerASMEvent e){ return this;};	
	
}
