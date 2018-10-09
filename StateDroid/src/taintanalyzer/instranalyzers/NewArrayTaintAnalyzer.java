package taintanalyzer.instranalyzers;


import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class NewArrayTaintAnalyzer  extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
    boolean tainted=false;
    String[] used ;
    String changed;


	public NewArrayTaintAnalyzer(TaintAnalyzer ta )
	{
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		 logger = Logger.getLogger(NewArrayTaintAnalyzer.class);			
	}
	
//new-array vx,vy,type_id
	
//	Generates a new array of type_id type and vy element size and puts the reference to the array into vx.	

// 0x7e new-array v9, v4, [Ljava/lang/String;  , v9 = array, v4=size	
	
	public Object analyzeInstruction()
	{
	   Register arrayReg;
	   Register sizeReg;
	    
	   arrayReg = ir.getInvolvedRegisters().get(0); // v9
	   sizeReg = ir.getInvolvedRegisters().get(1); // v4
		   
	   SymbolTableEntry sizeEntry= localSymSpace.find(sizeReg.getName());
	       
	   //Creates a new array, so even if an entry exists, it will be replaced.
	   
	   SymbolTableEntry arrayEntry = new SymbolTableEntry();
	   EntryDetails arrayEntryDetails = arrayEntry.getEntryDetails();
	   
	   arrayEntry.setInstrInfo(ir.getInstr().getText());
	   arrayEntry.setLineNumber(ir.getLineNumber());
	   arrayEntry.setName(arrayReg.getName());
	   
	   arrayEntryDetails.setType(ir.getReturnType());
	   
	   arrayEntry.setEntryDetails(arrayEntryDetails);
	   
	   this.localSymSpace.addEntry(arrayEntry);
	   
       logger.debug("\n AputTaintAnalyzer");
       localSymSpace.logInfoSymbolSpace();
	       
	   return null;
	}

}
