package taintanalyzer.instranalyzers;

import models.cfg.CFG;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class ThrowTaintAnalyzer  extends BaseTaintAnalyzer{

	private TaintAnalyzer ta;
	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
    boolean tainted=false;
    String[] used ;
    String changed;

 
//	0x2a new-instance v1, Ljava/lang/RuntimeException;
//	0x2e invoke-direct v1, v7, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V
//	0x34 throw v1
//	onCreate-BB@0x36 0x36 0x58[ NEXT =  ] [ PREV = ] 
//		0x36 move-exception v6		

    public ThrowTaintAnalyzer(TaintAnalyzer ta )
	{
		this.ta = ta;
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		 logger = Logger.getLogger(ThrowTaintAnalyzer.class);			
	}
	
	public Object analyzeInstruction()
	{
 	   Register reg = ir.getInvolvedRegisters().get(0);
	   Instruction instr = ir.getInstr();
       SymbolTableEntry entry=   localSymSpace.find(reg.getName());

       if(entry != null){
    	   SymbolTableEntry deepCopy = new SymbolTableEntry(entry);
    	   CFG currCFG = ta.getCurrCFG();
    	   if(currCFG != null){
    		   currCFG.setExceptionObject(deepCopy);
    	   }
       }
       return null;
	}
}
