package taintanalyzer.instranalyzers;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class NewInstanceTaintAnalyzer  extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
    boolean tainted=false;
    String[] used ;
    String changed;

	public NewInstanceTaintAnalyzer(TaintAnalyzer ta )
	{
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		 logger = Logger.getLogger(NewInstanceTaintAnalyzer.class);			
	}
    // 0x62 new-instance v0, Lorg/json/JSONObject;
	
	public Object analyzeInstruction()
	{
		Register destReg = ir.getInvolvedRegisters().get(0);
    	   
    	// entry is set Record: assumption is that there is always an <init> call after it which is handled by Default handler.
    	   // If this is one of the APIs (StringBuilder, JSON etc.), new-instace creates an object and mark it as record.
    	   // Default handler handles <init> call. It marks object as tainted, if any of the input variable is tainted.

   		SymbolTableEntry entry = new SymbolTableEntry();
   		EntryDetails entryDetails = entry.getEntryDetails();

	    entry.setName(destReg.getName());
	    entry.setLineNumber(ir.getLineNumber());
	    entry.setInstrInfo(ir.getInstr().getText());
	    
	    entryDetails.setType(destReg.getType());
	    entryDetails.setTainted(false);
	    entryDetails.setConstant(false);
	    entryDetails.setField(false);

//	    entryDetails.setRecord(true); Whenever iput sets data, it should set it as record. 
	 
	    entryDetails.setValue("");

	    //Rest of the elements are set to the default.
 	    entry.setEntryDetails(entryDetails);
 	   
	   localSymSpace.addEntry(entry);
    
       logger.debug("\n NewInstanceTaintAnalyzer");
       localSymSpace.logInfoSymbolSpace();
       
       return null;
	}
}
