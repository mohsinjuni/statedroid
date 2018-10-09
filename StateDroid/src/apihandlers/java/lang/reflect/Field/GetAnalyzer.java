package apihandlers.java.lang.reflect.Field;

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
import patternMatcher.events.csm.reflection.FieldGetByKeyEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;


public class GetAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetAnalyzer.class);
	}

/*		0x40 invoke-virtual v6, v5, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
		0x46 move-result-object v13
*/	

	public Object analyzeInstruction(){
	
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		Register param1Reg = involvedRegisters.get(1);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());
		SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
		
		SymbolTableEntry destEntry = null;
		
		if(callerApiEntry != null){
			destEntry = new SymbolTableEntry(callerApiEntry); // deep copy
		   
     	    destEntry.setLineNumber(ir.getLineNumber());
    	    destEntry.setName(callerApiReg.getName());
    	    // type = Ljava/lang/Class; , value = Landroid/telephony/TelephonyManager;
    	    
    	    destEntry.getEntryDetails().setType(ir.getReturnType());
    	    Hashtable recordFieldList = destEntry.getEntryDetails().getRecordFieldList();
    	    if(recordFieldList == null)
    	    	recordFieldList = new Hashtable();
    	    recordFieldList.put("callerAPIEntry", callerApiEntry);
    	    
    	    if(param1Entry != null){
    	    	destEntry.getEntryDetails().setValue(param1Entry.getEntryDetails().getValue());
    	    	recordFieldList.put("param1Entry", param1Entry);
    	    }
    	    destEntry.getEntryDetails().setRecordFieldList(recordFieldList);
    	    destEntry.setInstrInfo(ir.getInstr().getText());
       	       		   
    	    
    	    //This one deserves a new event because after getting a method, it should be set to accessible=true, and then it can be invoked. There
    	    // is a temporal order between API-calls.
			EventFactory.getInstance().registerEvent("fieldGetByKeyEvent", new FieldGetByKeyEvent());
			Event event = EventFactory.getInstance().createEvent("fieldGetByKeyEvent");
			event.setName("fieldGetByKeyEvent");
			event.setCurrMethodName(instr.getCurrMethodName());
			event.setCurrPkgClsName(instr.getCurrPkgClassName());
			
			event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
			event.setCurrComponentName(ta.getCurrComponentName());
			event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
						
			event.getEventInfo().put("callerAPIEntry", callerApiEntry);
			if(param1Entry != null)
				event.getEventInfo().put("param1Entry", param1Entry);
			event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
			
			InstructionReturnValue retValue = new InstructionReturnValue(destEntry, event);
			
			return retValue;
 
		}
 	   localSymSpace.logInfoSymbolSpace();
	   return null;
	}
}
