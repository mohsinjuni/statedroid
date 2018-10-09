package apihandlers.java.io.DataOutputStream;

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
import patternMatcher.events.csm.filereading.DataOutputStreamDefinedEvent;
import patternMatcher.events.csm.filereading.DataOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.FileInputStreamDefinedEvent;
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;
import patternMatcher.events.csm.url.UrlInitEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class WriteAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public WriteAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(WriteAnalyzer.class);
	}

//		0x28a invoke-virtual v0, v8, v1, v2, Ljava/io/DataOutputStream;->write([B I I)V
	// We are only interested in the first input parameter.
	
	public Object analyzeInstruction(){
		int regCount = ir.getInvolvedRegisters().size();
		
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v0
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v8
		
		boolean isNewEntry = false;
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
        
        if(callerEntry != null){
	    	callerEntry.setLineNumber(ir.getLineNumber());
	    	if(inputParamEntry != null){
	    		Hashtable recordFieldList = callerEntry.getEntryDetails().getRecordFieldList();
	    		if(recordFieldList == null){
	    			recordFieldList = new Hashtable();
	    		}
	    		recordFieldList.put("inputParamEntry", inputParamEntry);
	    		callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
	    		callerEntry.getEntryDetails().setRecord(true);
	        	callerEntry.getEntryDetails().setField(false);
	
	    		EventFactory.getInstance().registerEvent("dataOutputStreamWriteEvent", new DataOutputStreamWriteEvent());
				Event event = EventFactory.getInstance().createEvent("dataOutputStreamWriteEvent");
				event.setName("dataOutputStreamWriteEvent");
			
				Hashtable eventInfo = event.getEventInfo();
				eventInfo.put(InstructionResponse.CLASS_NAME, ir);
				eventInfo.put("callerEntry", callerEntry);
			
				ta.setCurrCSMEvent(event);
	    	}
    	}
       logger.debug("\n <AppendAnalyzer>");
       return null;
	}
}
