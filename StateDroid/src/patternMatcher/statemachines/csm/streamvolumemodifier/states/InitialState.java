package patternMatcher.statemachines.csm.streamvolumemodifier.states;

import java.util.Hashtable;

import models.symboltable.SymbolTableEntry;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.streamvolumemodifier.StreamVolumeModifierStates;
import taintanalyzer.TaintAnalyzer;

public class InitialState extends StreamVolumeModifierStates {

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {

	}

	/*
	 * audiomanager.setStreamVolume( AudioManager.STREAM_VOICE_CALL,
	 * AudioManager.ADJUST_LOWER, 0); --decreasing/reduced to 0.
	 * 
	 * 0x166 const/4 v14, 0
	 * 0x168 const/4 v15, -1
	 * 0x16a const/16 v16, 8
	 * 0x16e move/from16 v0, v16
	 * // 0x172 invoke-virtual v3, v14, v15, v0,
	 * Landroid/media/AudioManager;->setStreamVolume(I I I)V
	 * 
	 * audiomanager.setStreamVolume( --increasing volume, infact it sets to the
	 * maximum
	 * AudioManager.STREAM_VOICE_CALL,
	 * audiomanager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
	 * 0);
	 * 
	 * 0x1d8 const/4 v14, 0
	 * 0x1da const/4 v15, 0
	 * 0x1dc invoke-virtual v3, v15,
	 * Landroid/media/AudioManager;->getStreamMaxVolume(I)I
	 * 0x1e2 move-result v15
	 * 0x1e4 const/16 v16, 0
	 * 0x1e8 move/from16 v0, v16
	 * 0x1ec invoke-virtual v3, v14, v15, v0,
	 * Landroid/media/AudioManager;->setStreamVolume(I I I)V
	 */
	@Override
	public State update(SetStreamVolumeEvent e) {

		//		System.out.println("SetStreamVolumeEvent Received");

		SymbolTableEntry param1Entry = (SymbolTableEntry) e.getEventInfo().get("param1Entry");
		SymbolTableEntry param2Entry = (SymbolTableEntry) e.getEventInfo().get("param2Entry");
		SymbolTableEntry param3Entry = (SymbolTableEntry) e.getEventInfo().get("param3Entry");

		if (param1Entry != null && param2Entry != null) {
			// AudioManager.STREAM_VOICE_CALL = 0
			// AudioManager.ADJUST_LOWER = -1
			// audiomanager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)   --> returns 5.
			// FLAG_REMOVE_SOUND_AND_VIBRATE	Removes any sounds/vibrate that may be in the queue, or are playing (related to changing volume).
			// ... value = 8

			//  ADJUST_RAISE = 1

			String param1Value = param1Entry.getEntryDetails().getValue().trim();
			String param2Value = param2Entry.getEntryDetails().getValue().trim();
			String param3Value = param3Entry.getEntryDetails().getValue().trim();

			// STREAM_ALARM = 4
			// STREAM_DTMF = 8 . This is dialpad tone, I guess.
			// STREAM_MUSIC = 3
			// STREAM_NOTIFICATION  = 5
			// STREAM_RING = 2
			// STREAM_SYSTEM = 1
			// STREAM_VOICE_CALL = 0
			if (param1Value.equalsIgnoreCase("0")) //Chosen stream
			{
				if (!param2Value.isEmpty() && (param2Value.equalsIgnoreCase("-1") || Integer.parseInt(param2Value) < 1)) {
					EventFactory.getInstance().registerEvent("VoiceCallStreamDecreasingEvent", new VoiceCallStreamDecreasingASMEvent());

					//First, crate event and set it in TaintAnalyzer. Then, do what we do for each source API i.e. return an entry marked as tainted.
					Event event = EventFactory.getInstance().createEvent("VoiceCallStreamDecreasingEvent");
					event.setName("VoiceCallStreamDecreasingEvent");

					Hashtable<String, Object> eventInfo = event.getEventInfo();

					event.setCurrMethodName(e.getCurrMethodName());
					event.setCurrPkgClsName(e.getCurrPkgClsName());
					event.setCurrCompCallbackMethodName(e.getCurrCompCallbackMethodName());
					event.setCurrComponentName(e.getCurrComponentName());
					event.setCurrComponentPkgName(e.getCurrComponentPkgName());

					eventInfo.put("additionalFlag", param3Value); // Flag in the API call
					event.setEventInfo(eventInfo);

					ta.setCurrASMEvent(event);
				} else if (param2Value.equalsIgnoreCase("1") || param2Value.equalsIgnoreCase("5")) //ADJUST_RAISE || max value
				{
					EventFactory.getInstance().registerEvent("VoiceCallStreamIncreasingEvent", new VoiceCallStreamIncreasingASMEvent());

					Event event = EventFactory.getInstance().createEvent("VoiceCallStreamIncreasingEvent");
					event.setName("VoiceCallStreamIncreasingEvent");

					Hashtable<String, Object> eventInfo = event.getEventInfo();

					eventInfo.put("additionalFlag", param3Value); // Flag in the API call

					event.setCurrMethodName(e.getCurrMethodName());
					event.setCurrPkgClsName(e.getCurrPkgClsName());
					event.setCurrCompCallbackMethodName(e.getCurrCompCallbackMethodName());
					event.setCurrComponentName(e.getCurrComponentName());
					event.setCurrComponentPkgName(e.getCurrComponentPkgName());

					eventInfo.put("additionalFlag", param3Value); // Flag in the API call

					event.setEventInfo(eventInfo);

					ta.setCurrASMEvent(event);
				}
			}
		}
		return this;
	}

}
