
package apihandlers.javax.crypto.CipherInputStream;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.filereading.CipherGetInstanceEvent;
import patternMatcher.events.csm.filereading.CipherInputStreamInitEvent;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;

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
	
//		0x138 invoke-direct/range v21 ... v23, Ljavax/crypto/CipherInputStream;-><init>(Ljava/io/InputStream; Ljavax/crypto/Cipher;)V
//		0x13e move-object/from16 v15, v20
	
//		v6 should have a specific state, and cipher should have a specific state as well.	

	public Object analyzeInstruction(){
		Register reg0 = ir.getInvolvedRegisters().get(0);  //v21
		Register reg1 = ir.getInvolvedRegisters().get(1);  //v22
		Register reg2 = ir.getInvolvedRegisters().get(2);  //v23

        SymbolTableEntry callerEntry = localSymSpace.find(reg0.getName());
		SymbolTableEntry param1Entry = localSymSpace.find(reg1.getName());
        SymbolTableEntry param2Entry = localSymSpace.find(reg2.getName());
        
        if(callerEntry != null){
        	//Create a new event.
        	
    		EventFactory.getInstance().registerEvent("cipherInputStreamInitEvent", new CipherInputStreamInitEvent());
			Event event = EventFactory.getInstance().createEvent("cipherInputStreamInitEvent");
			event.setName("cipherInputStreamInitEvent");
			
			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			eventInfo.put("fileInputStreamEntry", param1Entry);
			eventInfo.put("cipherEntry", param2Entry);
			eventInfo.put("callerEntry", callerEntry);
			event.setEventInfo(eventInfo);
			
			ta.setCurrCSMEvent(event);
        }
       return null;
	}

}
