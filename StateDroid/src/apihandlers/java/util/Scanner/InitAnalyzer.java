package apihandlers.java.util.Scanner;

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
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;
import patternMatcher.events.csm.filereading.ScannerDefinedEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class InitAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta){
		
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(InitAnalyzer.class);
	}

//		0xc new-instance v7, Ljava/util/Scanner;
//		0x10 new-instance v0, Ljava/io/File;
//		0x14 const-string v1, 'c:\\file.txt'
//		0x18 invoke-direct v0, v1, Ljava/io/File;-><init>(Ljava/lang/String;)V
//		0x1e invoke-direct v7, v0, Ljava/util/Scanner;-><init>(Ljava/io/File;)V
//		0x50 invoke-virtual v7, Ljava/util/Scanner;->nextInt()I
//		0x56 move-result v0
	
	//Scanner(File source)
	//Scanner(File source, String charsetName)
	//Scanner(InputStream source)
	//Scanner(InputStream source, String charsetName)
	//Scanner(Path source)
	//Scanner(Path source, String charsetName)
	//Scanner(Readable source)
	//Scanner(ReadableByteChannel source)
	//Scanner(ReadableByteChannel source, String charsetName)
	//Scanner(String source)
	
	
	public Object analyzeInstruction(){
		
		int regCount = ir.getInvolvedRegisters().size();
		
		if(regCount >= 2)
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
	        		
					EventFactory.getInstance().registerEvent("scannerDefinedEvent", new ScannerDefinedEvent());
					Event event = EventFactory.getInstance().createEvent("scannerDefinedEvent");
					event.setName("scannerDefinedEvent");
					
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
	        	reg1EntryDetails.setType("Ljava/util/Scanner;"); 
	        	callerEntry.setLineNumber(ir.getLineNumber());
	        	
	        	reg1EntryDetails.setConstant(false);
	        	reg1EntryDetails.setTainted(false);
	    	    callerEntry.setInstrInfo(ir.getInstr().getText());
	    	    reg1EntryDetails.setField(false);
	    	    reg1EntryDetails.setRecord(false);
	    	   
	        	if(inputParamEntry != null)
	        	{
	        		Hashtable recordFieldList = callerEntry.getEntryDetails().getRecordFieldList();
	        		if(recordFieldList == null){
	        			recordFieldList = new Hashtable();
	        		}
	        		recordFieldList.put("fileEntry", inputParamEntry);
	        		callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
	        		callerEntry.getEntryDetails().setRecord(true);
	        	}
	    	    reg1EntryDetails.setValue(" ");
	    	    callerEntry.setEntryDetails(reg1EntryDetails);
	    	    
	        	this.localSymSpace.addEntry(callerEntry);
	
	       }
        }
       return null;
		
	}
}
