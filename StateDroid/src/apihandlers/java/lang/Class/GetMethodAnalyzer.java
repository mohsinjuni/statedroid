package apihandlers.java.lang.Class;

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
import patternMatcher.events.csm.reflection.ClassGetDeclaredMethodEvent;
import patternMatcher.events.csm.reflection.ClassGetNameEvent;
import patternMatcher.events.csm.reflection.ObjectGetClassEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;


public class GetMethodAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetMethodAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetMethodAnalyzer.class);
	}

/*
		0xdc invoke-static v12, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
		0xe2 move-result-object v1
		0xe4 const-string v12, 'getITelephony'
		0xe8 const/4 v13, 0
		0xea new-array v13, v13, [Ljava/lang/Class;
		0xee invoke-virtual v1, v12, v13, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String; [Ljava/lang/Class;)Ljava/lang/reflect/Method;
		0xf4 move-result-object v5
 * 			
 * public java.lang.reflect.Method getDeclaredMethod(java.lang.String methodName, java.lang.Class[ ] parameterTypes)
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction(){
	
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		Register param1Reg = involvedRegisters.get(1);
		Register param2Reg = involvedRegisters.get(2);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());
		SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
		SymbolTableEntry param2Entry = this.localSymSpace.find(param2Reg.getName());
		
		SymbolTableEntry destEntry = null;
		
		if(callerApiEntry != null){
			destEntry = new SymbolTableEntry(callerApiEntry); // deep copy
		   
     	    destEntry.setLineNumber(ir.getLineNumber());
    	    destEntry.setName(callerApiReg.getName());
    	    
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
       	       		   
    	    
    	    //This one deserves a new event because after getting a method, it should be set to accessible=true, and then it can be invoked. There
    	    // is a temporal order between API-calls.
    	    
    	    //Here we use ClassGetDeclaredMethodEvent instead of generating a new ClassGetMethodEvent. Both serve the same purpose, anyway
			EventFactory.getInstance().registerEvent("classGetDeclaredMethodEvent", new ClassGetDeclaredMethodEvent());
			
			Event classGetDeclaredMethodEvent = EventFactory.getInstance().createEvent("classGetDeclaredMethodEvent");
			
			classGetDeclaredMethodEvent.setName("classGetDeclaredMethodEvent");
			classGetDeclaredMethodEvent.setCurrMethodName(instr.getCurrMethodName());
			classGetDeclaredMethodEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
			
			classGetDeclaredMethodEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
			classGetDeclaredMethodEvent.setCurrComponentName(ta.getCurrComponentName());
			classGetDeclaredMethodEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
						
			classGetDeclaredMethodEvent.getEventInfo().put("callerAPIEntry", callerApiEntry);
			if(param1Entry != null)
				classGetDeclaredMethodEvent.getEventInfo().put("methodNameEntry", param1Entry);
			classGetDeclaredMethodEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
			
			InstructionReturnValue retValue = new InstructionReturnValue(destEntry, classGetDeclaredMethodEvent);
			
			//This event is routed through move-object-result's analyzer for doing some homework.
//			ta.setCurrCSMEvent(classGetDeclaredMethodEvent);
			
			return retValue;
 
		}
 	   localSymSpace.logInfoSymbolSpace();
	   return null;
	}
}