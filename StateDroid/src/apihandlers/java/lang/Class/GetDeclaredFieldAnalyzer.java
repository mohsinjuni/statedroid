package apihandlers.java.lang.Class;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.reflection.ClassGetDeclaredFieldEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;


public class GetDeclaredFieldAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetDeclaredFieldAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetDeclaredFieldAnalyzer.class);
	}

/*
		0x24 invoke-static v15, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
		0x2a move-result-object v15  ======> v15 has ConnectivityManagerDefinedState
		0x2c const-string v16, 'mService'
		0x30 invoke-virtual/range v15 ... v16, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
		0x36 move-result-object v6
 * 			
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction(){
	
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		Register param1Reg = involvedRegisters.get(1);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());
		SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
		
		SymbolTableEntry destEntry = null;
		
		if(callerApiEntry != null)
		{
			destEntry = new SymbolTableEntry(callerApiEntry); // deep copy
		   
     	    destEntry.setLineNumber(ir.getLineNumber()); //and name is decided by next instruction.
    	    destEntry.getEntryDetails().setType(ir.getReturnType());
    	    Hashtable recordFieldList = destEntry.getEntryDetails().getRecordFieldList();
    	    if(recordFieldList == null)
    	    	recordFieldList = new Hashtable();
    	    recordFieldList.put("class", callerApiEntry);
    	    
    	    if(param1Entry != null)
    	    {
    	    	destEntry.getEntryDetails().setValue(param1Entry.getEntryDetails().getValue());
    	    	recordFieldList.put("method", param1Entry);
    	    }
    	    destEntry.getEntryDetails().setRecordFieldList(recordFieldList);
    	    destEntry.setInstrInfo(ir.getInstr().getText());
       	       		   
    	    
			EventFactory.getInstance().registerEvent("classGetDeclaredFieldEvent", new ClassGetDeclaredFieldEvent());
			Event classGetDeclaredFieldEvent = EventFactory.getInstance().createEvent("classGetDeclaredFieldEvent");
			
			classGetDeclaredFieldEvent.setName("classGetDeclaredFieldEvent");
			classGetDeclaredFieldEvent.setCurrMethodName(instr.getCurrMethodName());
			classGetDeclaredFieldEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
			
			classGetDeclaredFieldEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
			classGetDeclaredFieldEvent.setCurrComponentName(ta.getCurrComponentName());
			classGetDeclaredFieldEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
						
			classGetDeclaredFieldEvent.getEventInfo().put("callerAPIEntry", callerApiEntry);
			if(param1Entry != null)
				classGetDeclaredFieldEvent.getEventInfo().put("fieldNameEntry", param1Entry);
			classGetDeclaredFieldEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
			
			InstructionReturnValue retValue = new InstructionReturnValue(destEntry, classGetDeclaredFieldEvent);
			
			//Event is routed through move-result-object analyzer because we need to do some homework before sending this event out.
//			ta.setCurrCSMEvent(classGetDeclaredMethodEvent);
			
			return retValue;
 
		}
 	   localSymSpace.logInfoSymbolSpace();
	   return null;
	}
}
