package apihandlers.android.os.Bundle;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class PutStringAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public PutStringAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(PutStringAnalyzer.class);
	}

/*
 * 		0xec const-string v8, 'description'
		0xf0 const-string v7, 'description'
		0xf4 invoke-virtual v2, v7, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;
		0xfa move-result-object v7
		0xfc check-cast v7, Ljava/lang/String;
		
		
		0x100 invoke-virtual v4, v8, v7, Landroid/os/Bundle;->putString(Ljava/lang/String; Ljava/lang/String;)V
		
		//We also need to handle HashMap, JSon, Hashtable etc.
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register bundleReg = involvedRegisters.get(0);
		Register keyReg = involvedRegisters.get(1);
		Register valueReg = involvedRegisters.get(2);
		
		SymbolTableEntry bundleEntry = this.localSymSpace.find(bundleReg.getName());
		
		if(bundleEntry != null)
		{
			SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
			SymbolTableEntry valueEntry = this.localSymSpace.find(valueReg.getName());
			
			
			if(keyEntry != null && valueEntry != null)
			{
				SymbolTableEntry fieldEntry = new SymbolTableEntry(valueEntry); //deep copy
				
				Hashtable recordFieldList = bundleEntry.getEntryDetails().getRecordFieldList();
    			
	        	if(recordFieldList == null)
	        		recordFieldList = new Hashtable();
	        	
				recordFieldList.put(keyEntry.getEntryDetails().getValue().trim(), fieldEntry);
				
				bundleEntry.getEntryDetails().setRecordFieldList(recordFieldList);
			}
		}
		
     	logger.debug("\n Bundle.putString()");
//	       localSymSpace.printSymbolSpace();
	    return null;
	}
}
