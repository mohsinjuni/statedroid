package apihandlers.android.media.AudioManager;

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.audiomanager.AudioMgrSetVibrateSettingEvent;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class SetVibrateSettingAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private final String fieldName = "output_format";

/*
 *  void setVibrateSetting (int vibrateType, int vibrateSetting)
 *  
 *  vibrateType 	int: The type of vibrate. One of VIBRATE_TYPE_NOTIFICATION or VIBRATE_TYPE_RINGER.
	vibrateSetting 	int: The vibrate setting, one of VIBRATE_SETTING_ON (1), VIBRATE_SETTING_OFF (0), or VIBRATE_SETTING_ONLY_SILENT (2).
 *  
 *  So, vibration is ON only when value of second parameter is 1, otherwise, it's off when its value is 0 or 2.
 *  
 */
	
//	0x22 invoke-virtual v0, v1, v2, Landroid/media/AudioManager;->setVibrateSetting(I I)V
	public SetVibrateSettingAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(SetVibrateSettingAnalyzer.class);
		this.ta = ta;
	}

	public Object analyzeInstruction() {
		//Based on input, create the relevant event.

		Register reg1 = ir.getInvolvedRegisters().get(0); //v0
		Register reg2 = ir.getInvolvedRegisters().get(1); //v1
		Register reg3 = ir.getInvolvedRegisters().get(2); //v2

		SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
		SymbolTableEntry param1Entry = localSymSpace.find(reg2.getName());
		SymbolTableEntry param2Entry = localSymSpace.find(reg3.getName()); 

		EventFactory.getInstance().registerEvent("audioMgrSetVibrateSettingEvent", new AudioMgrSetVibrateSettingEvent());
		Event event = EventFactory.getInstance().createEvent("audioMgrSetVibrateSettingEvent");
		event.setName("audioMgrSetVibrateSettingEvent");

		Hashtable<String, Object> eventInfo = event.getEventInfo();
		if (callerEntry != null) {
			Hashtable recordFieldList = callerEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList == null)
				recordFieldList = new Hashtable();

			if (param2Entry != null) {
				SymbolTableEntry streamOperationEntry = new SymbolTableEntry(param2Entry); //deep copy
				streamOperationEntry.setName("vibrateSettingEntry");
				recordFieldList.put("vibrateSettingEntry", streamOperationEntry);
			}
			callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			event.setEventInfo(eventInfo);
			ta.setCurrCSMEvent(event);

			return callerEntry;
		}
		return null;

	}
}
