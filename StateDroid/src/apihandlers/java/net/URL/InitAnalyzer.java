package apihandlers.java.net.URL;

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
import patternMatcher.events.csm.filereading.FileDefinedEvent;
import patternMatcher.events.csm.filereading.FileReaderDefinedEvent;
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
	
//		0xfa new-instance v34, Ljava/net/URL;
//		0xfe const-string v36, 'https://132.72.23.126:443/archiveArtifact/rest/test'
//		0x102 move-object/from16 v0, v34
//		0x106 move-object/from16 v1, v36
//		0x10a invoke-direct v0, v1, Ljava/net/URL;-><init>(Ljava/lang/String;)V

		
	public Object analyzeInstruction()
	{
		int regCount = ir.getInvolvedRegisters().size();
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v0
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v1
		
		boolean isNewEntry = false;
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
        
        if(callerEntry == null){
        	callerEntry = new SymbolTableEntry();
        	callerEntry.setName(reg1.getName());
        	callerEntry.getEntryDetails().setType("Ljava/net/URL;");
        	isNewEntry = true;
        }
    	callerEntry.setLineNumber(ir.getLineNumber());
    	if(inputParamEntry != null){
    		Hashtable recordFieldList = callerEntry.getEntryDetails().getRecordFieldList();
    		if(recordFieldList == null){
    			recordFieldList = new Hashtable();
    		}
    		recordFieldList.put("urlStringEntry", inputParamEntry);
    		callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
    		callerEntry.getEntryDetails().setRecord(true);
        	callerEntry.getEntryDetails().setField(false);

    		EventFactory.getInstance().registerEvent("urlInitEvent", new UrlInitEvent());
			Event event = EventFactory.getInstance().createEvent("urlInitEvent");
			event.setName("urlInitEvent");
			
			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			eventInfo.put("urlEntry", callerEntry);
			
			if(isNewEntry){
				this.localSymSpace.addEntry(callerEntry);
			}
			ta.setCurrCSMEvent(event);
    	}
	       
       return null;
		
	}
}
