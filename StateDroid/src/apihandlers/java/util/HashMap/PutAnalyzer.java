package apihandlers.java.util.HashMap;

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

public class PutAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public PutAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(PutAnalyzer.class);
	}

/*
		0x5a invoke-virtual v1, v2, v3, Ljava/util/HashMap;->put(Ljava/lang/Object; Ljava/lang/Object;)Ljava/lang/Object;
		0x60 add-int/lit8 v0, v0, 1 * 

 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register mapReg = involvedRegisters.get(0);
		Register keyReg = involvedRegisters.get(1);
		Register valueReg = involvedRegisters.get(2);
		
		SymbolTableEntry mapEntry = this.localSymSpace.find(mapReg.getName());
		
		if(mapEntry != null)
		{
			SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
			SymbolTableEntry valueEntry = this.localSymSpace.find(valueReg.getName());
			
			// If any of the value entry is tainted, we marks the whole Map as tainted, mark it as NOT-record. 
			//Effectively, it means, if one value is tainted, whole Map is tainted... Untainting is difficult. 
			//It will do create False Positive
			
			if(valueEntry != null) // keyEntry != null && 
			{
				
				if(valueEntry.getEntryDetails().isTainted())
				{
					ArrayList<SourceInfo> siList = mapEntry.getEntryDetails().getSourceInfoList();
					
					ArrayList<SourceInfo> valueSiList = valueEntry.getEntryDetails().getSourceInfoList();
					
					if(siList == null)
						siList = new ArrayList<SourceInfo>();
					
					for(SourceInfo si: valueSiList)
					{
						if(!siList.contains(si))
							siList.add(si);
					}
					mapEntry.getEntryDetails().setTainted(true);
					mapEntry.getEntryDetails().setSourceInfoList(siList);
					
				}
				mapEntry.getEntryDetails().setRecord(false);
			}
		}
		else 
		{
			mapEntry = new SymbolTableEntry();
			SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
			SymbolTableEntry valueEntry = this.localSymSpace.find(valueReg.getName());
			
			if(valueEntry != null)
			{
				if(valueEntry.getEntryDetails().isTainted())
				{
					ArrayList<SourceInfo> siList = mapEntry.getEntryDetails().getSourceInfoList();
					
					ArrayList<SourceInfo> valueSiList = valueEntry.getEntryDetails().getSourceInfoList();
					
					if(siList == null)
						siList = new ArrayList<SourceInfo>();
					
					for(SourceInfo si: valueSiList)
					{
						if(!siList.contains(si))
							siList.add(si);
					}
					mapEntry.getEntryDetails().setTainted(true);
					
					mapEntry.getEntryDetails().setSourceInfoList(siList);
					
				}
				mapEntry.getEntryDetails().setRecord(false);

			}
		}
		
     	logger.debug("\n Bundle.putString()");
//	       localSymSpace.printSymbolSpace();
	    return null;
	}
}
