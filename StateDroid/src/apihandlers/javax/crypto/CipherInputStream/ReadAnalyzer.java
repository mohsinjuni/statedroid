package apihandlers.javax.crypto.CipherInputStream;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.filereading.CipherInputStreamReadEvent;
import patternMatcher.events.csm.filereading.FileInputStreamDefinedEvent;
import patternMatcher.events.csm.filereading.FileInputStreamReadEvent;
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class ReadAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ReadAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(ReadAnalyzer.class);
	}
	
//		0x166 invoke-virtual/range v20 ... v21, Ljavax/crypto/CipherInputStream;->read([B)I  
	
	public Object analyzeInstruction(){
		int regCount = ir.getInvolvedRegisters().size();
		Register reg1 = ir.getInvolvedRegisters().get(0);
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
	        
        if(regCount >=2){
			Register reg2 = ir.getInvolvedRegisters().get(1);
	        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
	        if(callerEntry != null){
	        	callerEntry.setLineNumber(ir.getLineNumber());
	        	if(inputParamEntry == null){
	        		inputParamEntry = new SymbolTableEntry();
	        		inputParamEntry.getEntryDetails().setType("[B");
	        		inputParamEntry.setName(reg2.getName());
	        		this.localSymSpace.addEntry(inputParamEntry);
	        	}
	        	
	    		EventFactory.getInstance().registerEvent("cipherInputStreamReadEvent", new CipherInputStreamReadEvent());
				Event event = EventFactory.getInstance().createEvent("cipherInputStreamReadEvent");
				event.setName("cipherInputStreamReadEvent");
					
				Hashtable eventInfo = event.getEventInfo();
				eventInfo.put(InstructionResponse.CLASS_NAME, ir);
				eventInfo.put("callerEntry", callerEntry);
				eventInfo.put("inputParamEntry", inputParamEntry);
				
				ta.setCurrCSMEvent(event);
	        }
        }
	    logger.debug("\n <AppendAnalyzer>");
	   return null;
	}
}
