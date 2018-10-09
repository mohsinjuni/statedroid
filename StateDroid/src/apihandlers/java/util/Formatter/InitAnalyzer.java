package apihandlers.java.util.Formatter;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class InitAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(InitAnalyzer.class);
	}
//		0x28 new-instance v0, Ljava/lang/StringBuffer;
//		0x2c invoke-direct v0, Ljava/lang/StringBuffer;-><init>()V
//		0x32 new-instance v1, Ljava/util/Formatter;
//		0x36 invoke-direct v1, v0, Ljava/util/Formatter;-><init>(Ljava/lang/Appendable;)V
	
	public Object analyzeInstruction()
	{

		int regCount = ir.getInvolvedRegisters().size();
		if(regCount == 2)
		{
			Register reg1 = ir.getInvolvedRegisters().get(0);  //v1
			Register reg2 = ir.getInvolvedRegisters().get(1);  //v0
				
	        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
	        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
		        
		        
	        if(callerEntry != null)
	        {
	        	callerEntry.setLineNumber(ir.getLineNumber());
	        	
	        	if(inputParamEntry != null)
	        	{
	        		SymbolTableEntry shallowCopiedInput = (SymbolTableEntry) inputParamEntry.clone();
	        		shallowCopiedInput.setName("input");
	        		Hashtable recordFieldList = (Hashtable) callerEntry.getEntryDetails().getRecordFieldList();
	        		if(recordFieldList == null){
	        			recordFieldList = new Hashtable();
	        		}
	        		recordFieldList.put(shallowCopiedInput.getName(), shallowCopiedInput);
	        		callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
	        	}
            	if(inputParamEntry.getEntryDetails().isTainted() )
            	{
            		callerEntry.getEntryDetails().setTainted(true);
       			   	ArrayList<SourceInfo> existingSiList = inputParamEntry.getEntryDetails().getSourceInfoList();
    			   	ArrayList<SourceInfo> siList = callerEntry.getEntryDetails().getSourceInfoList();
    			   	
    			   	if(existingSiList != null && existingSiList.size()> 0)
    			   	{
    			   		if(siList == null)
    			   			siList = new ArrayList<SourceInfo>();
        				for(SourceInfo si : existingSiList)
        				{
	        	    		if(!siList.contains(si))
	        	    			siList.add(si);
        				}
    			   	}
    			   	callerEntry.getEntryDetails().setSourceInfoList(siList);
            	}

	       }
	       else
	       {
	    	   callerEntry = new SymbolTableEntry();
	        	callerEntry.setLineNumber(ir.getLineNumber());
	        	
	        	if(inputParamEntry != null)
	        	{
	        		SymbolTableEntry shallowCopiedInput = (SymbolTableEntry) inputParamEntry.clone();
	        		shallowCopiedInput.setName("input");
	        		Hashtable recordFieldList = (Hashtable) callerEntry.getEntryDetails().getRecordFieldList();
	        		if(recordFieldList == null){
	        			recordFieldList = new Hashtable();
	        		}
	        		recordFieldList.put(shallowCopiedInput.getName(), shallowCopiedInput);
	        		callerEntry.getEntryDetails().setRecordFieldList(recordFieldList);
	        	}
            	if(inputParamEntry.getEntryDetails().isTainted() )
            	{
            		callerEntry.getEntryDetails().setTainted(true);
       			   	ArrayList<SourceInfo> existingSiList = inputParamEntry.getEntryDetails().getSourceInfoList();
    			   	ArrayList<SourceInfo> siList = callerEntry.getEntryDetails().getSourceInfoList();
    			   	
    			   	if(existingSiList != null && existingSiList.size()> 0)
    			   	{
    			   		if(siList == null)
    			   			siList = new ArrayList<SourceInfo>();
        				for(SourceInfo si : existingSiList)
        				{
	        	    		if(!siList.contains(si))
	        	    			siList.add(si);
        				}
    			   	}
    			   	callerEntry.getEntryDetails().setSourceInfoList(siList);
            	}
            	
            	this.localSymSpace.addEntry(callerEntry);
	
	       }
		}
   
       logger.debug("\n <InitAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
