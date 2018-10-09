package apihandlers.java.util.HashTable;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetAnalyzer.class);
	}

/*
 0x72 invoke-virtual v5, v2, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;
		0x78 move-result-object v2
		
				 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register mapReg = involvedRegisters.get(0);
		Register keyReg = involvedRegisters.get(1);
		
		SymbolTableEntry mapEntry = this.localSymSpace.find(mapReg.getName());
		
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		
		if(mapEntry != null && mapEntry.getEntryDetails().isTainted())
		{
			ArrayList<SourceInfo> siList = mapEntry.getEntryDetails().getSourceInfoList();
			
			ArrayList<SourceInfo> returnSiList = returnEntry.getEntryDetails().getSourceInfoList();
			
			if(returnSiList == null)
				returnSiList = new ArrayList<SourceInfo>();
			
			for(SourceInfo si: siList)
			{
				if(!returnSiList.contains(si))
					returnSiList.add(si);
			}
			returnEntry.getEntryDetails().setSourceInfoList(siList);
			returnEntry.getEntryDetails().setTainted(true);
			
			return returnEntry;
			//we dont check for Key thingy.
		}
			
     	logger.debug("\n Bundle.getString()");
//	       localSymSpace.printSymbolSpace();
	    return null;
	}
}
