package apihandlers.java.io.FileOutputStream;

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
import patternMatcher.events.csm.filereading.FileInputStreamDefinedEvent;
import patternMatcher.events.csm.filereading.FileInputStreamReadEvent;
import patternMatcher.events.csm.filereading.FileOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class WriteAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public WriteAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(WriteAnalyzer.class);
	}
	
//		0x1c6 invoke-virtual/range v20 ... v23, Ljava/io/FileOutputStream;->write([B I I)V
	
	public Object analyzeInstruction(){
		
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v14
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v8
			
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
	        
        if(callerEntry != null){
    		EventFactory.getInstance().registerEvent("fileOutputStreamWriteEvent", new FileOutputStreamWriteEvent());
			Event event = EventFactory.getInstance().createEvent("fileOutputStreamWriteEvent");
			event.setName("fileOutputStreamWriteEvent");
				
			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			eventInfo.put("callerEntry", callerEntry);
			eventInfo.put("inputParamEntry", inputParamEntry);
			
			ta.setCurrCSMEvent(event);
		}
        logger.debug("\n <AppendAnalyzer>");
       return null;
	}
}
