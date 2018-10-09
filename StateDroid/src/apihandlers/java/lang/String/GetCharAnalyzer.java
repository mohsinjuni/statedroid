
package apihandlers.java.lang.String;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import apihandlers.android.app.Activity.InitAnalyzer;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;

public class GetCharAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetCharAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(GetCharAnalyzer.class);
	}

//	0x3e invoke-virtual v3, v6, v5, v1, v6, Ljava/lang/String;->getChars(I I [C I)V
	// string.getChars(0, len, char[], 0);  string is copied to chars.
	public Object analyzeInstruction()
	{
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v3
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v6
		Register reg3 = ir.getInvolvedRegisters().get(2);  //v5
		Register reg4 = ir.getInvolvedRegisters().get(3);  //v1
		Register reg5 = ir.getInvolvedRegisters().get(4);  //v6
		
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputEntry1 = localSymSpace.find(reg2.getName());
        SymbolTableEntry inputEntry2 = localSymSpace.find(reg3.getName());
        SymbolTableEntry inputEntry3 = localSymSpace.find(reg4.getName());
        SymbolTableEntry inputEntry4 = localSymSpace.find(reg5.getName());
        
        if(callerEntry != null)
        {
        	
        	callerEntry.getEntryDetails().setType(reg1.getType());
        	callerEntry.setLineNumber(ir.getLineNumber());
        	
        	logger.debug("isReg1 Tainted? " + callerEntry.getEntryDetails().isTainted());
    		boolean tainted = false;
    		String sourceAPI = "";
    		String instrInfo = "";
    	
    		if(inputEntry3 != null){

    			if(callerEntry.getEntryDetails().isTainted())
	        	{
        			tainted = true;

       			   	ArrayList<SourceInfo> existingSiList = callerEntry.getEntryDetails().getSourceInfoList();
    			   	ArrayList<SourceInfo> siList = inputEntry3.getEntryDetails().getSourceInfoList();
    			   	
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
    			   	inputEntry3.getEntryDetails().setSourceInfoList(siList);
        		}
        		
        		if(!inputEntry3.getEntryDetails().isTainted())
        			inputEntry3.getEntryDetails().setTainted(tainted);
        		inputEntry3.setInstrInfo(instrInfo);
	           	inputEntry3.getEntryDetails().setField(false);
	        	inputEntry3.getEntryDetails().setRecord(false);

          	}
    		else
          	{
          		inputEntry3 = new SymbolTableEntry();
          		if(callerEntry.getEntryDetails().isTainted())
	        	{
        			tainted = true;

       			   	ArrayList<SourceInfo> existingSiList = callerEntry.getEntryDetails().getSourceInfoList();
    			   	ArrayList<SourceInfo> siList = inputEntry3.getEntryDetails().getSourceInfoList();
    			   	
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
    			   	inputEntry3.getEntryDetails().setSourceInfoList(siList);
        		}
        		
        		if(!inputEntry3.getEntryDetails().isTainted())
        			inputEntry3.getEntryDetails().setTainted(tainted);
        		inputEntry3.setInstrInfo(instrInfo);
	           	inputEntry3.getEntryDetails().setField(false);
	        	inputEntry3.getEntryDetails().setRecord(false);

	        	this.localSymSpace.addEntry(inputEntry3);
          	}
        	return null;
       }
       logger.debug("\n <GetCharAnalyzer>");
//       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
