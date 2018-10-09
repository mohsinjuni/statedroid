package apihandlers.java.lang.reflect.Field;

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
import patternMatcher.events.csm.reflection.FieldSetAccessibleEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;


public class SetFieldAccessibleAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SetFieldAccessibleAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(SetFieldAccessibleAnalyzer.class);
	}
	
	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		Register param1Reg = involvedRegisters.get(1);

		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());
		SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
		
		SymbolTableEntry destEntry = null;
		
		if(callerApiEntry != null){
			
			if(param1Entry != null){
				SymbolTableEntry field = new SymbolTableEntry(param1Entry);
				field.setName(param1Reg.getName());
				
				Hashtable recordFieldList = callerApiEntry.getEntryDetails().getRecordFieldList();
				if(recordFieldList == null){
					recordFieldList = new Hashtable();
				}
				recordFieldList.put(field.getName(), field);
				callerApiEntry.getEntryDetails().setRecordFieldList(recordFieldList);
				
			}
			EventFactory.getInstance().registerEvent("fieldSetAccessibleEvent", new FieldSetAccessibleEvent());
			
			Event fieldSetAccessibleEvent = EventFactory.getInstance().createEvent("fieldSetAccessibleEvent");
			fieldSetAccessibleEvent.setName("fieldSetAccessibleEvent");
			fieldSetAccessibleEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
			
			ta.setCurrCSMEvent(fieldSetAccessibleEvent);
		}
 
	   return null;
	}
}
