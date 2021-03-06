package apihandlers.java.io.File;

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

//		0x18 invoke-direct v0, v1, Ljava/io/File;-><init>(Ljava/lang/String;)V
	
	public Object analyzeInstruction()
	{
		int regCount = ir.getInvolvedRegisters().size();
		if(regCount == 2)
		{
			Register reg1 = ir.getInvolvedRegisters().get(0);  //v0
			Register reg2 = ir.getInvolvedRegisters().get(1);  //v1
			
	        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
	        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
	        
	        
	        if(callerEntry != null)
	        {
	        	callerEntry.setLineNumber(ir.getLineNumber());
	        	
	        	if(inputParamEntry != null)
	        	{
	        		Hashtable recordFieldList = callerEntry.getEntryDetails().getRecordFieldList();
	        		if(recordFieldList == null){
	        			recordFieldList = new Hashtable();
	        		}
	        		recordFieldList.put("fileEntry", inputParamEntry);
	        		callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
	        		callerEntry.getEntryDetails().setRecord(true);

	        		EventFactory.getInstance().registerEvent("fileDefinedEvent", new FileDefinedEvent());
					Event event = EventFactory.getInstance().createEvent("fileDefinedEvent");
					event.setName("fileDefinedEvent");
					
					Hashtable eventInfo = event.getEventInfo();
					eventInfo.put(InstructionResponse.CLASS_NAME, ir);
					ta.setCurrCSMEvent(event);
	        	}
	        	callerEntry.getEntryDetails().setField(false);
	       }
	       else
	       {
	    	    callerEntry = new SymbolTableEntry();
	        	
	        	EntryDetails reg1EntryDetails = callerEntry.getEntryDetails();
	    	    callerEntry.setName(reg1.getName());
	        	reg1EntryDetails.setType("Ljava/io/File;"); // Just in case, type has not been set already.
	        	callerEntry.setLineNumber(ir.getLineNumber());
	        	
	        	reg1EntryDetails.setConstant(false);
	        	reg1EntryDetails.setTainted(false);
	    	    callerEntry.setInstrInfo(ir.getInstr().getText());
	    	    reg1EntryDetails.setField(false);
	    	    reg1EntryDetails.setRecord(false);
	    	   
	    	    reg1EntryDetails.setValue(" ");
	    	    callerEntry.setEntryDetails(reg1EntryDetails);
	    	    
	        	this.localSymSpace.addEntry(callerEntry);
	
	       }
        }
       logger.debug("\n <AppendAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
