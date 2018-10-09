package apihandlers.android.content.Intent;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.intent.IntentCategoryDefinedEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;


public class AddCategoryAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public AddCategoryAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(AddCategoryAnalyzer.class);
	}

	// myIntent.addCategory("android.intent.category.HOME");
	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());

		// caller + 1 input param
		if(involvedRegisters.size() == 2) {
			Register param1Reg = involvedRegisters.get(1);
			SymbolTableEntry categoryEntry = this.localSymSpace.find(param1Reg.getName());

			if(param1Reg.getType().equalsIgnoreCase("Ljava/lang/String;")){
				if(callerApiEntry != null){
					Hashtable recordFieldList = callerApiEntry.getEntryDetails().getRecordFieldList();
					if(recordFieldList == null) 
						recordFieldList = new Hashtable();
					recordFieldList.put("categoryEntry", categoryEntry);
					callerApiEntry.getEntryDetails().setRecordFieldList(recordFieldList);
				}
				EventFactory.getInstance().registerEvent("intentCategoryDefinedEvent", new IntentCategoryDefinedEvent());
				Event event = EventFactory.getInstance().createEvent("intentCategoryDefinedEvent");
				event.setName("intentCategoryDefinedEvent");
							
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				ta.setCurrCSMEvent(event);
			}
		}
 	   localSymSpace.logInfoSymbolSpace();
	   return null;
	}
}
