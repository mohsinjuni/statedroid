package apihandlers.java.io.FileInputStream;

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
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class ReadAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ReadAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(ReadAnalyzer.class);
	}
	
//		0x262 invoke-virtual v14, v8, Ljava/io/FileInputStream;->read([B)I
//		0x268 move-result v17
	
	public Object analyzeInstruction()
	{
		int regCount = ir.getInvolvedRegisters().size();
		if(regCount == 2){
			Register reg1 = ir.getInvolvedRegisters().get(0);  //v14
			Register reg2 = ir.getInvolvedRegisters().get(1);  //v8
			
	        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
	        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
	        
	        if(callerEntry != null){
	        	callerEntry.setLineNumber(ir.getLineNumber());
	        	if(inputParamEntry != null)
	        	{
	        		Hashtable recordFieldList = callerEntry.getEntryDetails().getRecordFieldList();
	        		if(recordFieldList == null){
	        			recordFieldList = new Hashtable();
	        		}
	        		recordFieldList.put("inputParamEntry", inputParamEntry);
	        		callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
	        		callerEntry.getEntryDetails().setRecord(true);
	        	}
	        	callerEntry.getEntryDetails().setField(false);

        		EventFactory.getInstance().registerEvent("fileInputStreamReadEvent", new FileInputStreamReadEvent());
				Event event = EventFactory.getInstance().createEvent("fileInputStreamReadEvent");
				event.setName("fileInputStreamReadEvent");
					
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
