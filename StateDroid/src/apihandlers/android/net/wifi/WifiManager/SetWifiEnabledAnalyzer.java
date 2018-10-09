
package apihandlers.android.net.wifi.WifiManager;


import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.SetRingerModeEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetAudioSourceEvent;
import patternMatcher.events.csm.settings.SetWifiEnabledEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants;
import enums.Constants.MediaRecorderFields;

public class SetWifiEnabledAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SetWifiEnabledAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(SetWifiEnabledAnalyzer.class);
	}

//	0x10 invoke-virtual v0, v3, Landroid/net/wifi/WifiManager;->setWifiEnabled(Z)Z
	public Object analyzeInstruction(){

		Register reg1 = ir.getInvolvedRegisters().get(0);  //v0
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v3
		
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());

        if(callerEntry != null){
        	
        	if(inputParamEntry != null){
       			
       			String value = inputParamEntry.getEntryDetails().getValue();
       			if(value!= null && !value.isEmpty()){
       				int val = Integer.parseInt(value);
       				if(val== 0 || val == 1){
		    	  		EventFactory.getInstance().registerEvent("setWifiEnabledEvent", new SetWifiEnabledEvent());
		    			Event setWifiEnabledEvent = EventFactory.getInstance().createEvent("setWifiEnabledEvent");
		    			setWifiEnabledEvent.setName("setWifiEnabledEvent");
		    		
		    			Hashtable<String, Object> eventInfo = setWifiEnabledEvent.getEventInfo(); 
		    			eventInfo.put(InstructionResponse.CLASS_NAME, ir); 
		    			eventInfo.put("booleanValue", value);
		    			setWifiEnabledEvent.setEventInfo(eventInfo);
		    			
		    			ta.setCurrCSMEvent(setWifiEnabledEvent);
       				}
       			}
        	}
        	return callerEntry;
       }
       return null;
		
	}
}
