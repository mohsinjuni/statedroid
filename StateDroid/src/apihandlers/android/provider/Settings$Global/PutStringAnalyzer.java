package apihandlers.android.provider.Settings$Global;

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
import patternMatcher.events.csm.settings.SettingsGlobalPutStringEvent;
import patternMatcher.events.csm.settings.SettingsSystemPutIntEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class PutStringAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public PutStringAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(PutStringAnalyzer.class);
		this.ta = ta;
	}

/*		0x8 const-string v1, 'airplane_mode_on'
		0xc invoke-static v4, Ljava/lang/String;->valueOf(I)Ljava/lang/String;
		0x12 move-result-object v2
		0x14 invoke-static v0, v1, v2, Landroid/provider/Settings$Global;->putString(Landroid/content/ContentResolver; Ljava/lang/String; Ljava/lang/String;)Z
*/		
	public Object analyzeInstruction(){

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register crReg = involvedRegisters.get(0);
		Register keyReg = involvedRegisters.get(1);
		Register valueReg = involvedRegisters.get(2);
		
		SymbolTableEntry crEntry = this.localSymSpace.find(crReg.getName());
		
		if(crEntry != null){
			SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
			SymbolTableEntry valueEntry = this.localSymSpace.find(valueReg.getName());
			
			if(keyEntry != null && valueEntry != null) {
				EventFactory.getInstance().registerEvent("settingsGlobalPutStringEvent", new SettingsGlobalPutStringEvent());
				Event event = EventFactory.getInstance().createEvent("settingsGlobalPutStringEvent");
				event.setName("settingsGlobalPutStringEvent");
				
				event.getEventInfo().put("keyEntry", keyEntry);
				event.getEventInfo().put("valueEntry", valueEntry);
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				ta.setCurrCSMEvent(event);
			
			}
		}
		else {
			crEntry = new SymbolTableEntry();
			crEntry.getEntryDetails().setType(ir.getReturnType());
			SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
			SymbolTableEntry valueEntry = this.localSymSpace.find(valueReg.getName());
			
			if(keyEntry != null && valueEntry != null) {
				EventFactory.getInstance().registerEvent("settingsGlobalPutStringEvent", new SettingsGlobalPutStringEvent());
				Event event = EventFactory.getInstance().createEvent("settingsGlobalPutStringEvent");
				event.setName("settingsGlobalPutStringEvent");
				
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
