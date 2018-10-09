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
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;

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
//		0x8 new-instance v9, Ljava/io/FileInputStream;
//		0xc invoke-direct v9, v8, Ljava/io/FileInputStream;-><init>(Ljava/lang/String;)V
//		0x12 new-instance v10, Ljava/io/InputStreamReader;
//		0x16 invoke-direct v10, v9, Ljava/io/InputStreamReader;-><init>(Ljava/io/InputStream;)V
//		0x1c new-instance v6, Ljava/io/BufferedReader;
//		0x20 invoke-direct v6, v10, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
//
	
	public Object analyzeInstruction()
	{
		int regCount = ir.getInvolvedRegisters().size();
		if(regCount == 2)
		{
			Register reg1 = ir.getInvolvedRegisters().get(0);  //v9
			Register reg2 = ir.getInvolvedRegisters().get(1);  //v8
			
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

	        		EventFactory.getInstance().registerEvent("fileInputStreamDefinedEvent", new FileInputStreamDefinedEvent());
					Event event = EventFactory.getInstance().createEvent("fileInputStreamDefinedEvent");
					event.setName("fileInputStreamDefinedEvent");
					
					Hashtable eventInfo = event.getEventInfo();
					eventInfo.put(InstructionResponse.CLASS_NAME, ir);
					ta.setCurrCSMEvent(event);
	        	}
	        	callerEntry.getEntryDetails().setField(false);
	       }
		}
        else if (regCount == 1)
        {
//        	0x28 invoke-direct v6, Ljava/lang/StringBuilder;-><init>()V

        	Register reg1 = ir.getInvolvedRegisters().get(0);  //v6
			
	        SymbolTableEntry reg1Entry = localSymSpace.find(reg1.getName());
	        
	        if(reg1Entry != null)
	        {
	        	EntryDetails reg1EntryDetails = reg1Entry.getEntryDetails();
	        	reg1EntryDetails.setType("Ljava/io/FileInputStream;"); // Just in case, type has not been set already.
	        	reg1Entry.setLineNumber(ir.getLineNumber());
	        	
	        	reg1EntryDetails.setConstant(false);
	        	reg1EntryDetails.setTainted(false);
	    	    reg1Entry.setInstrInfo(ir.getInstr().getText());
	    	    reg1EntryDetails.setField(false);
	    	    reg1EntryDetails.setRecord(false);
	    	   
	    	    reg1EntryDetails.setValue(" ");
	    	    reg1Entry.setEntryDetails(reg1EntryDetails);
	       }
	       else
	       {
	    	    reg1Entry = new SymbolTableEntry();
	        	EntryDetails reg1EntryDetails = reg1Entry.getEntryDetails();
	        	reg1EntryDetails.setType("Ljava/io/FileInputStream;"); 
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
