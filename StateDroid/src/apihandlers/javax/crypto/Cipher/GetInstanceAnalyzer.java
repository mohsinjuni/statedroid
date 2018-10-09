
package apihandlers.javax.crypto.Cipher;

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
import patternMatcher.events.csm.filereading.CipherGetInstanceEvent;
import patternMatcher.events.csm.filereading.FileInputStreamDefinedEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetInstanceAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetInstanceAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(GetInstanceAnalyzer.class);
	}

//		0x4a const-string v7, 'AES/CBC/PKCS5Padding'
//		0x4e invoke-static v7, Ljavax/crypto/Cipher;->getInstance(Ljava/lang/String;)Ljavax/crypto/Cipher;
//		0x54 move-result-object v7

	public Object analyzeInstruction(){
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v7
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg1.getName());
        
        SymbolTableEntry cipherEntry = new SymbolTableEntry();
        cipherEntry.getEntryDetails().setType("Ljavax/crypto/Cipher;");
        
        if(inputParamEntry != null){
        	
        	Hashtable recordFieldList = new Hashtable();
        	recordFieldList.put("encryption", inputParamEntry);
        	
        	cipherEntry.getEntryDetails().setRecord(true);
        	cipherEntry.getEntryDetails().setRecordFieldList(recordFieldList);
        	
    		EventFactory.getInstance().registerEvent("cipherGetInstanceEvent", new CipherGetInstanceEvent());
			Event event = EventFactory.getInstance().createEvent("cipherGetInstanceEvent");
			event.setName("cipherGetInstanceEvent");
			
			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			eventInfo.put("callerEntry", cipherEntry);
			event.setEventInfo(eventInfo);
			
			InstructionReturnValue irv = new InstructionReturnValue(cipherEntry, event);
			return irv;
        }
    	return null;
	}

}
