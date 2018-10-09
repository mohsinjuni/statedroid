package apihandlers.java.io.FileReader;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.cursor.CursorGetStringEvent;
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

//		0x4 new-instance v9, Ljava/io/FileReader;
//		0x8 invoke-direct v9, v8, Ljava/io/FileReader;-><init>(Ljava/lang/String;)V
//		0xe new-instance v6, Ljava/io/BufferedReader;
//		0x12 invoke-direct v6, v9, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
	
	public Object analyzeInstruction()
	{
		int regCount = ir.getInvolvedRegisters().size();

		if(regCount == 2){
			
			Register reg1 = ir.getInvolvedRegisters().get(0);  //v0
			Register reg2 = ir.getInvolvedRegisters().get(1);  //v1
			
	        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
	        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
	        
	        if(callerEntry != null){
	        	
	        	callerEntry.setLineNumber(ir.getLineNumber());
	        	if(inputParamEntry != null){
	        		Hashtable recordFieldList = callerEntry.getEntryDetails().getRecordFieldList();
	        		if(recordFieldList == null){
	        			recordFieldList = new Hashtable();
	        		}
	        		recordFieldList.put("fileEntry", inputParamEntry);
	        		callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
	        		callerEntry.getEntryDetails().setRecord(true);
	        		
					EventFactory.getInstance().registerEvent("fileReaderDefinedEvent", new FileReaderDefinedEvent());
					Event event = EventFactory.getInstance().createEvent("fileReaderDefinedEvent");
					event.setName("fileReaderDefinedEvent");
					
					Hashtable eventInfo = event.getEventInfo();
					eventInfo.put(InstructionResponse.CLASS_NAME, ir);
					ta.setCurrCSMEvent(event);
	        	}
	        	callerEntry.getEntryDetails().setField(false);
	       }
		}
        else if (regCount == 1)
        {
        	Register reg1 = ir.getInvolvedRegisters().get(0);  //v6
			
	        SymbolTableEntry reg1Entry = localSymSpace.find(reg1.getName());
	        
	        if(reg1Entry != null)
	        {
	        	EntryDetails reg1EntryDetails = reg1Entry.getEntryDetails();
	//    	    entry.setName(destReg.getName());
	        	reg1EntryDetails.setType("Ljava/io/FileReader;"); // Just in case, type has not been set already.
	        	reg1Entry.setLineNumber(ir.getLineNumber());
	        	
	        	reg1EntryDetails.setConstant(false);
	        	reg1EntryDetails.setTainted(false);
	    	    reg1Entry.setInstrInfo(ir.getInstr().getText());
	    	    reg1EntryDetails.setField(false);
	    	    reg1EntryDetails.setRecord(false);
	    	   
	    	    reg1EntryDetails.setValue(" ");
	    	    reg1Entry.setEntryDetails(reg1EntryDetails);
//	        	return null;
	       }
	       else
	       {
	    	    reg1Entry = new SymbolTableEntry();
	        	
	        	EntryDetails reg1EntryDetails = reg1Entry.getEntryDetails();
	        	reg1EntryDetails.setType("Ljava/io/FileReader;"); // Just in case, type has not been set already.
	        	reg1Entry.setLineNumber(ir.getLineNumber());
	        	
	        	reg1EntryDetails.setConstant(false);
	        	reg1EntryDetails.setTainted(false);
	    	    reg1Entry.setInstrInfo(ir.getInstr().getText());
	    	    reg1EntryDetails.setField(false);
	    	    reg1EntryDetails.setRecord(false);
	    	   
	    	    reg1EntryDetails.setValue(" ");
	    	    reg1Entry.setEntryDetails(reg1EntryDetails);
	    	    
	        	this.localSymSpace.addEntry(reg1Entry);
	
	       }
        }
       logger.debug("\n <AppendAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
