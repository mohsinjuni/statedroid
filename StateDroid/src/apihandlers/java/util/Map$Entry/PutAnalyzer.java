package apihandlers.java.util.Map$Entry;

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
 *   Map<Object,String> mp=new HashMap<Object, String>();

        mp.put(new Integer(2), "Two");
        mp.put(new Integer(1), "One");

       Set s=mp.entrySet();

        //Move next key and value of Map by iterator
        Iterator it=s.iterator();

        while(it.hasNext())
        {
            // key=value separator this by Map.Entry to get key and value
            Map.Entry m =(Map.Entry)it.next();

            // getKey is used to get key of Map
            int key=(Integer)m.getKey();

            // getValue is used to get value of key in Map
            String value=(String)m.getValue();

            System.out.println("Key :"+key+"  Value :"+value);
        }
    }
    
    
   		0x5c invoke-interface v2, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;
		0x62 move-result-object v1

		0x80 invoke-interface v2, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;
		0x86 move-result-object v1


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
