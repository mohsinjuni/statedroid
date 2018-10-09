package patternMatcher.statemachines.csm.settingstoggler.states;
import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.manifest.AndroidManifest;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.appremoval.AppRemovalASMEvent;
import patternMatcher.events.asm.settings.DeviceAirPlaneModeModificationASMEvent;
import patternMatcher.events.asm.settings.DeviceBrightnessModificationASMEvent;
import patternMatcher.events.asm.settings.WifiTogglerASMEvent;
import patternMatcher.events.csm.appremoval.SetApplicationEnabledEvent;
import patternMatcher.events.csm.appremoval.SetComponentEnabledEvent;
import patternMatcher.events.csm.settings.SettingsGlobalPutStringEvent;
import patternMatcher.events.csm.settings.SettingsSystemPutIntEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.appremoval.AppRemovalStates;
import patternMatcher.statemachines.csm.context.states.PackageManagerDefinedState;
import patternMatcher.statemachines.csm.context.states.PackageNameDefinedState;
import patternMatcher.statemachines.csm.settingstoggler.SettingsTogglerStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class InitialState extends SettingsTogglerStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public InitialState(TaintAnalyzer taParam){
		this.ta = taParam;
	}
	
	public InitialState(){}
	
	@Override
	public State update(SettingsSystemPutIntEvent e) {
		
//		0x1c const-string v1, 'screen_brightness'
//		0x20 invoke-static v0, v1, v4, Landroid/provider/Settings$System;->putInt(Landroid/content/ContentResolver; Ljava/lang/String; I)Z
		
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		SymbolTableEntry keyEntry = (SymbolTableEntry) e.getEventInfo().get("keyEntry");
		SymbolTableEntry valueEntry = (SymbolTableEntry) e.getEventInfo().get("valueEntry");
		
		if(keyEntry != null && valueEntry != null){
			String key = keyEntry.getEntryDetails().getValue();
			if(key != null && !key.isEmpty() && 
					(key.equalsIgnoreCase("screen_brightness") || key.equalsIgnoreCase("'screen_brightness'"))
					){
				EventFactory.getInstance().registerEvent("deviceBrightnessModificationASMEvent", new DeviceBrightnessModificationASMEvent());
				Event asmEvent = EventFactory.getInstance().createEvent("deviceBrightnessModificationASMEvent");
				asmEvent.setName("deviceBrightnessModificationASMEvent");
				
				asmEvent.setCurrMethodName(ir.getInstr().getCurrMethodName());
				asmEvent.setCurrPkgClsName(ir.getInstr().getCurrPkgClassName());
				
				asmEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
				asmEvent.setCurrComponentName(ta.getCurrComponentName());
				asmEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				asmEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				
				ta.setCurrASMEvent(asmEvent);
			}
		}
		return this; 
	}
	
		@Override
	public State update(SettingsGlobalPutStringEvent e) {
		
/*		0x8 const-string v1, 'airplane_mode_on'
		0xc invoke-static v4, Ljava/lang/String;->valueOf(I)Ljava/lang/String;
		0x12 move-result-object v2
		0x14 invoke-static v0, v1, v2, Landroid/provider/Settings$Global;->putString(Landroid/content/ContentResolver; Ljava/lang/String; Ljava/lang/String;)Z
*/		
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		SymbolTableEntry keyEntry = (SymbolTableEntry) e.getEventInfo().get("keyEntry");
		SymbolTableEntry valueEntry = (SymbolTableEntry) e.getEventInfo().get("valueEntry");
		
		if(keyEntry != null && valueEntry != null){
			String key = keyEntry.getEntryDetails().getValue();
			if(key != null && !key.isEmpty() && 
					(key.equalsIgnoreCase("airplane_mode_on") || key.equalsIgnoreCase("'airplane_mode_on'"))
					){
				EventFactory.getInstance().registerEvent("deviceAirPlaneModeModificationASMEvent", new DeviceAirPlaneModeModificationASMEvent());
				Event asmEvent = EventFactory.getInstance().createEvent("deviceAirPlaneModeModificationASMEvent");
				asmEvent.setName("deviceAirPlaneModeModificationASMEvent");
				
				asmEvent.setCurrMethodName(ir.getInstr().getCurrMethodName());
				asmEvent.setCurrPkgClsName(ir.getInstr().getCurrPkgClassName());
				
				asmEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
				asmEvent.setCurrComponentName(ta.getCurrComponentName());
				asmEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				asmEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				
				ta.setCurrASMEvent(asmEvent);
			}
		}
		return this; 
	}
}
