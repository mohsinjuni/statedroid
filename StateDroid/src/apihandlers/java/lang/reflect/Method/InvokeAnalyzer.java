package apihandlers.java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.GetExtrasBundleEvent;
import patternMatcher.events.csm.intent.IntentDefinedEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;


public class InvokeAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InvokeAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(InvokeAnalyzer.class);
	}

/*
 * 
 * 		0xfe const/4 v12, 0
		0x100 new-array v12, v12, [Ljava/lang/Object;
		0x104 invoke-virtual v5, v11, v12, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object; [Ljava/lang/Object;)Ljava/lang/Object;

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
		
		if(callerApiEntry != null){
			
			destEntry = new SymbolTableEntry(callerApiEntry); // deep copy
			   
     	    destEntry.setLineNumber(ir.getLineNumber());
    	    destEntry.setName(callerApiReg.getName());
    	    
    	    destEntry.getEntryDetails().setType(ir.getReturnType()); //Type is Method
 	    	destEntry.getEntryDetails().setValue(callerApiEntry.getEntryDetails().getValue()); // 'getITelephony'
    	        	    
    	    destEntry.setInstrInfo(ir.getInstr().getText());
    	    
			EventFactory.getInstance().registerEvent("methodInvokeEvent", new MethodInvokeEvent());
			Event methodInvokeEvent = EventFactory.getInstance().createEvent("methodInvokeEvent");
			methodInvokeEvent.setName("methodInvokeEvent");
			
			methodInvokeEvent.getEventInfo().put("callerAPIEntry", callerApiEntry);
			if(param1Entry != null)
				methodInvokeEvent.getEventInfo().put("methodNameEntry", param1Entry);
			methodInvokeEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
			
			Instruction currInstr = ir.getInstr();
			Instruction nextInstr = currInstr.getNextInstr();
			
			if(nextInstr != null){
				String nextInstrText = nextInstr.getText();
				
				//There is no return value, send events from here now.
				if(!nextInstrText.contains("move-result")){ 
				   ta.setCurrCSMEvent(methodInvokeEvent);
				   return null;
				}
			}
			InstructionReturnValue instrRetValue = new InstructionReturnValue(destEntry, methodInvokeEvent);
			return instrRetValue;
		}
 
	   return null;
	}
}
