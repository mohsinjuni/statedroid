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
import patternMatcher.events.csm.filereading.FileInputStreamDefinedEvent;
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;
import patternMatcher.events.csm.url.UrlInitEvent;

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
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(InitAnalyzer.class);
	}

//	0x208 invoke-direct v0, v1, Ljava/io/DataOutputStream;-><init>(Ljava/io/OutputStream;)V
	
	public Object analyzeInstruction(){
		int regCount = ir.getInvolvedRegisters().size();
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v0
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v1
		
		boolean isNewEntry = false;
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
        
        if(callerEntry == null){
        	callerEntry = new SymbolTableEntry();
        	callerEntry.setName(reg1.getName());
        	callerEntry.getEntryDetails().setType("Ljava/io/DataOutputStream;");
        	isNewEntry = true;
        }
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

    		EventFactory.getInstance().registerEvent("dataOutputStreamDefinedEvent", new DataOutputStreamDefinedEvent());
			Event event = EventFactory.getInstance().createEvent("dataOutputStreamDefinedEvent");
			event.setName("dataOutputStreamDefinedEvent");
			
			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			eventInfo.put("callerEntry", callerEntry);
			
			if(isNewEntry){
				this.localSymSpace.addEntry(callerEntry);
			}
			ta.setCurrCSMEvent(event);
    	}
       logger.debug("\n <AppendAnalyzer>");
       return null;
		
	}
}
