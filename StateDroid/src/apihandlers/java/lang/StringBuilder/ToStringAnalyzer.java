package apihandlers.java.lang.StringBuilder;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;

public class ToStringAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ToStringAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ToStringAnalyzer.class);
	}

	// 		0xac invoke-virtual v8, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
	public Object analyzeInstruction()
	{

		Register destReg = ir.getInvolvedRegisters().get(0);
     	   
        SymbolTableEntry entry = localSymSpace.find(destReg.getName());

        SymbolTableEntry returnEntry = new SymbolTableEntry();
        if(entry != null)
        {
        	
        	// Since this method call returns String, which does not get changed after being assigned a value.
        	// So we need deep copy.
        	returnEntry = new SymbolTableEntry(entry);
        	
        	returnEntry.getEntryDetails().setType(ir.getReturnType());
        	returnEntry.setLineNumber(ir.getLineNumber());

        	returnEntry.getEntryDetails().setField(false);
        	returnEntry.getEntryDetails().setRecord(false);
    	       	   
    	   return returnEntry;
       }
       logger.debug("\n ToStringHandler");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
