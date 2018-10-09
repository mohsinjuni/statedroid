package taintanalyzer.instranalyzers;


import models.cfg.CFG;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class MoveExceptionTaintAnalyzer  extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
    boolean tainted=false;
    String[] used ;
    String changed;
    TaintAnalyzer ta;

	
	public MoveExceptionTaintAnalyzer(TaintAnalyzer taParam )
	{
		ta = taParam;
		this.ir = taParam.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		 logger = Logger.getLogger(MoveExceptionTaintAnalyzer.class);			
	}
	
//		0x34 throw v1
//	onCreate-BB@0x36 0x36 0x58[ NEXT =  ] [ PREV = ] 
//		0x36 move-exception v6
		
	public Object analyzeInstruction()
	{
 	   Register destReg = ir.getInvolvedRegisters().get(0);
     	   
       SymbolTableEntry entry= null;
       
       CFG cfg = ta.getCurrCFG();
       if(cfg != null){
		   entry = cfg.getExceptionObject();
		   
		   if(entry != null){
			   
			   entry.setName(destReg.getName());  
			   entry.setLineNumber(ir.getLineNumber());
			   this.localSymSpace.addEntry(entry);
		   }
       }
       logger.debug("\n MoveExceptionTaintAnalyzer");
       localSymSpace.logInfoSymbolSpace();
       
       return null;
	}
}
