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
import patternMatcher.events.csm.intent.IntentSetActionEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;


public class SetActionAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SetActionAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(SetActionAnalyzer.class);
	}

/*
     Intent shortcutIntent = new Intent(getApplicationContext(), MainActivity.class);
    shortcutIntent.setAction(Intent.ACTION_MAIN);
 * 			
 */
	public Object analyzeInstruction(){
		
		String pkgClsName = ir.getCallerAPIName();
		String methdObjectName = ir.getMethodOrObjectName();
		String qualifiedAPIName = pkgClsName.concat("->").concat(methdObjectName);
	
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());

		// caller + 1 input param
		if(involvedRegisters.size() == 2) {
			Register param1Reg = involvedRegisters.get(1);
			SymbolTableEntry actionEntry = this.localSymSpace.find(param1Reg.getName());

			if(callerApiEntry != null && actionEntry != null){
				Hashtable recordFieldList = callerApiEntry.getEntryDetails().getRecordFieldList();
				if(recordFieldList == null) 
					recordFieldList = new Hashtable();
				recordFieldList.put("actionEntry", actionEntry);
				callerApiEntry.getEntryDetails().setRecordFieldList(recordFieldList);
				
				EventFactory.getInstance().registerEvent("intentSetActionEvent", new IntentSetActionEvent());
				Event event = EventFactory.getInstance().createEvent("intentSetActionEvent");
				event.setName("intentSetActionEvent");
								
				event.getEventInfo().put("intentEntry", callerApiEntry);
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				ta.setCurrCSMEvent(event);

			}
		}
	   return null;
	}
}
