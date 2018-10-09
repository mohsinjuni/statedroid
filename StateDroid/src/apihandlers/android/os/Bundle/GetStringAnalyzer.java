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

public class GetStringAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetStringAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetStringAnalyzer.class);
	}

/*
 * 		0x12 const-string v3, 'android.support.PARENT_ACTIVITY'
		0x16 invoke-virtual v2, v3, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;
		0x1c move-result-object v0
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register bundleReg = involvedRegisters.get(0);
		Register keyReg = involvedRegisters.get(1);
		
		SymbolTableEntry bundleEntry = this.localSymSpace.find(bundleReg.getName());
		
		if(bundleEntry != null)
		{
			SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
			
			if(keyEntry != null)
			{
				String keyValue = keyEntry.getEntryDetails().getValue();
				
				Hashtable recordFieldList = bundleEntry.getEntryDetails().getRecordFieldList();
				
				if(recordFieldList!= null && recordFieldList.containsKey(keyValue))
				{
					SymbolTableEntry valueEntry = (SymbolTableEntry) recordFieldList.get(keyValue);
					
					if(valueEntry != null)
					{
						SymbolTableEntry returnEntry = new SymbolTableEntry(valueEntry); //deeeep copy, since it is a string.
						
						return returnEntry;
					}
				}
			}
		}
		
     	logger.debug("\n Bundle.getString()");
//	       localSymSpace.printSymbolSpace();
	    return null;
	}
}
