
package apihandlers.java.util.Formatter;

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

public class FormatAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public FormatAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(FormatAnalyzer.class);
	}

//		0x48 aput-object v2, v5, v6
//		0x4c invoke-virtual v1, v4, v5, Ljava/util/Formatter;->format(Ljava/lang/String; [Ljava/lang/Object;)Ljava/util/Formatter;
	public Object analyzeInstruction()
	{

		Register reg1 = ir.getInvolvedRegisters().get(0);  //v1
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v4
		Register reg3 = ir.getInvolvedRegisters().get(2);  //v5

        SymbolTableEntry returnEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg3.getName());
        
        logger.debug("start AppendAnalyzer");
        
        if(returnEntry != null)
        {
//    	    entry.setName(destReg.getName());
        	returnEntry.getEntryDetails().setType(reg1.getType());
        	returnEntry.setLineNumber(ir.getLineNumber());
        	
        	logger.debug("isReg1 Tainted? " + returnEntry.getEntryDetails().isTainted());
        	
        	if(inputParamEntry != null)
        	{
        		
        		boolean tainted = false;
        		String sourceAPI = "";
        		String instrInfo = "";
        		
        		SymbolTableEntry inputStoredEntry = this.localSymSpace.find(returnEntry.getName(), "input");  
        		if(inputParamEntry.getEntryDetails().isTainted())
        		{
        			tainted = true;
        			
       			   	ArrayList<SourceInfo> existingSiList = inputParamEntry.getEntryDetails().getSourceInfoList();
    			   	ArrayList<SourceInfo> siList = returnEntry.getEntryDetails().getSourceInfoList();
    			   	
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
    			   	returnEntry.getEntryDetails().setSourceInfoList(siList);
    			   	
    			   	if(inputStoredEntry == null){
    			   		inputStoredEntry = new SymbolTableEntry();
    			   		inputStoredEntry.setName("input");
    			   		Hashtable recordFieldList = returnEntry.getEntryDetails().getRecordFieldList();
    			   		if(recordFieldList == null){
    			   			recordFieldList = new Hashtable();
    			   		}
    			   		recordFieldList.put(inputStoredEntry.getName(), inputStoredEntry);
    			   		returnEntry.getEntryDetails().setRecordFieldList(recordFieldList);
    			   		returnEntry.getEntryDetails().setRecord(true);
    			   	}
    			   	ArrayList<SourceInfo> inputStoredSIist = inputStoredEntry.getEntryDetails().getSourceInfoList();
    			   	
    			   	
    			   	if(existingSiList != null && existingSiList.size()> 0)
    			   	{
    			   		if(inputStoredSIist == null)
    			   			inputStoredSIist = new ArrayList<SourceInfo>();
        				for(SourceInfo si : existingSiList)
        				{
	        	    		if(!inputStoredSIist.contains(si))
	        	    			inputStoredSIist.add(si);
        				}
    			   	}    			   	
    			   	inputStoredEntry.getEntryDetails().setSourceInfoList(inputStoredSIist);
        		}
        		
           		if(!returnEntry.getEntryDetails().isTainted()){
        			returnEntry.getEntryDetails().setTainted(tainted);
        			inputStoredEntry.getEntryDetails().setTainted(tainted);
           		}
        		returnEntry.setInstrInfo(instrInfo);

          	}
        	returnEntry.getEntryDetails().setField(false);
        	returnEntry.getEntryDetails().setRecord(false);
    	   
        	return returnEntry;
       }
       logger.debug("\n <AppendAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
