package apihandlers.android.provider.Settings$System;

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
import patternMatcher.events.csm.contentresolver.ContentResolverInsertEvent;
import patternMatcher.events.csm.settings.SettingsSystemPutIntEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class PutIntAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public PutIntAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(PutIntAnalyzer.class);
		this.ta = ta;
	}

/*	0x1c const-string v1, 'screen_brightness'
	0x20 invoke-static v0, v1, v4, Landroid/provider/Settings$System;->putInt(Landroid/content/ContentResolver; Ljava/lang/String; I)Z
*/	
	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register crReg = involvedRegisters.get(0);
		Register keyReg = involvedRegisters.get(1);
		Register valueReg = involvedRegisters.get(2);
		
		SymbolTableEntry crEntry = this.localSymSpace.find(crReg.getName());
		
		if(crEntry != null){
			SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
			SymbolTableEntry valueEntry = this.localSymSpace.find(valueReg.getName());
			
			if(keyEntry != null && valueEntry != null) {
				EventFactory.getInstance().registerEvent("settingsSystemPutIntEvent", new SettingsSystemPutIntEvent());
				Event event = EventFactory.getInstance().createEvent("settingsSystemPutIntEvent");
				event.setName("settingsSystemPutIntEvent");
				
				event.getEventInfo().put("keyEntry", keyEntry);
				event.getEventInfo().put("valueEntry", valueEntry);
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				ta.setCurrCSMEvent(event);
			
			}
		}
		else 
		{
			crEntry = new SymbolTableEntry();
			crEntry.getEntryDetails().setType(ir.getReturnType());
			SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
			SymbolTableEntry valueEntry = this.localSymSpace.find(valueReg.getName());
			
			if(keyEntry != null && valueEntry != null) {
				EventFactory.getInstance().registerEvent("settingsSystemPutIntEvent", new SettingsSystemPutIntEvent());
				Event event = EventFactory.getInstance().createEvent("settingsSystemPutIntEvent");
				event.setName("settingsSystemPutIntEvent");
				
				event.getEventInfo().put("keyEntry", keyEntry);
				event.getEventInfo().put("valueEntry", valueEntry);
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				ta.setCurrCSMEvent(event);
			}
			localSymSpace.addEntry(crEntry);
		}
     	logger.debug("\n Bundle.putString()");
//	       localSymSpace.printSymbolSpace();
	    return null;
	}
}
