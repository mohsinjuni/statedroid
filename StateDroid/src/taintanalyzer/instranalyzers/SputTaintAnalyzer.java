package taintanalyzer.instranalyzers;


import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class SputTaintAnalyzer  extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
    boolean tainted=false;
    String[] used ;
    String changed;
    SymbolTableEntry destEntry;
    SymbolTableEntry srcEntry;
    Register srcReg;


	public SputTaintAnalyzer(TaintAnalyzer ta )
	{
		this.ir = ta.getIr();
		 logger = Logger.getLogger(SputTaintAnalyzer.class);			
	}
//	0xa sput-object v0, Lcom/geinimi/a/d;->a Ljava/lang/String;
	
	// we want to put value of v0=source into Lcom/geinimi/a/d;->a object
	public Object analyzeInstruction()
	{
		SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
		SymbolSpace globalSymSpace = Config.getInstance().getGlobalSymbolSpace();
		
		
		   srcReg = ir.getInvolvedRegisters().get(0);

		   String objectName = ir.getCallerAPIName();
		   String fieldName = ir.getMethodOrObjectName();
	       String qualifiedAPIName = ir.getCallerAPIName().trim().concat("->").concat(ir.getMethodOrObjectName().trim());
		   
	       srcEntry = localSymSpace.find(srcReg.getName());
	       
	       if(srcEntry != null)  //v0 value to be stored 
	       {
	    	    destEntry = new SymbolTableEntry(srcEntry); //deep copy
	       }
	       else
	       {
	    	    destEntry = new SymbolTableEntry(); 
	       }	   
			destEntry.setInstrInfo(ir.getInstr().getText());
			destEntry.setLineNumber(ir.getLineNumber());
			destEntry.setName(qualifiedAPIName);
			
			destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.
			
			globalSymSpace.addEntry(destEntry);
	       
	       logger.debug("\n SputTaintAnalyzer");
	       logger.debug("\n Printing Global SymSpace");
	       globalSymSpace.logInfoSymbolSpace();
	       
	       return null;
	}
	
}
