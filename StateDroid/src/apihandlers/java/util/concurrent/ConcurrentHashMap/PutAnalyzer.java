package apihandlers.java.util.concurrent.ConcurrentHashMap;

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
    0x14 iget-object v0, v1, Lcom/mobileapptracker/MobileAppTracker;->k Ljava/util/concurrent/ConcurrentHashMap;
    0x18 invoke-virtual v0, v2, v3, Ljava/util/concurrent/ConcurrentHashMap;->put(Ljava/lang/Object; Ljava/lang/Object;)Ljava/lang/Object;
		
	V	put(K key, V value)
		Maps the specified key to the specified value in this table.
	
		//We also need to handle HashMap, JSon, Hashtable etc.
 * 
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
			
			if(keyEntry != null && valueEntry != null)
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
