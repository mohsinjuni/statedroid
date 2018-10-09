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
import patternMatcher.events.csm.intent.IntentPutExtraEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;


public class PutExtraAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public PutExtraAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(PutExtraAnalyzer.class);
	}

//	addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());

		Register param1Reg = involvedRegisters.get(1);
		Register param2Reg = involvedRegisters.get(2);
		SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
		SymbolTableEntry param2Entry = this.localSymSpace.find(param2Reg.getName());

		if(callerApiEntry == null){
			callerApiEntry = new SymbolTableEntry();
			callerApiEntry.setName(callerApiReg.getName());
			this.localSymSpace.addEntry(callerApiEntry);
		}
		
		EventFactory.getInstance().registerEvent("intentPutExtraEvent", new IntentPutExtraEvent());
		Event event = EventFactory.getInstance().createEvent("intentPutExtraEvent");
		event.setName("intentPutExtraEvent");
					
		event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
		if(callerApiEntry != null){
			event.getEventInfo().put("intentEntry", callerApiEntry);
		}
		if(param1Entry != null){
			event.getEventInfo().put("param1Entry", param1Entry);
		}
		if(param2Entry != null){
			event.getEventInfo().put("param2Entry", param2Entry);
		}	
		ta.setCurrCSMEvent(event);
	   return null;
	}
}
