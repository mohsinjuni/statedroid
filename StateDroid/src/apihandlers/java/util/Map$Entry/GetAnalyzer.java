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
//   		0x5c invoke-interface v2, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;
		
		Register mapReg = involvedRegisters.get(0);
		
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
