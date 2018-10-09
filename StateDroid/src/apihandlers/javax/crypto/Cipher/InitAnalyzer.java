
package apihandlers.javax.crypto.Cipher;

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.filereading.CipherDefinedEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class InitAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(InitAnalyzer.class);
	}
	
//		0x60 invoke-virtual v7, v8, v9, v10, Ljavax/crypto/Cipher;->init(I Ljava/security/Key; Ljava/security/spec/AlgorithmParameterSpec;)V
//
	public Object analyzeInstruction(){
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v7
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg1.getName());
        
        if(inputParamEntry == null){
        	
        	inputParamEntry = new SymbolTableEntry();
        	inputParamEntry.getEntryDetails().setType("Ljavax/crypto/Cipher;");
        	this.localSymSpace.addEntry(inputParamEntry);
        }
        	
    	//Cipher.getInstance is defined first, and then Cipher.Init() is invoked. If v7 here is already defined, we are goood. 
    	// We just send cipherEvent to perform state transition. Otherwise, we create  a new entry here.
        	
    	//Create a new event.
		EventFactory.getInstance().registerEvent("cipherDefinedEvent", new CipherDefinedEvent());
		Event event = EventFactory.getInstance().createEvent("cipherDefinedEvent");
		event.setName("cipherDefinedEvent");
		
		Hashtable eventInfo = event.getEventInfo();
		eventInfo.put(InstructionResponse.CLASS_NAME, ir);
		eventInfo.put("callerEntry", inputParamEntry);
		event.setEventInfo(eventInfo);
			
		ta.setCurrCSMEvent(event);
			
       return null;
	}

}
