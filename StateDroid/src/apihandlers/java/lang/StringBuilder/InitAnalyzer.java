package apihandlers.java.lang.StringBuilder;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;


import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;

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

//	0x28 invoke-direct v6, v7, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V
	
	public Object analyzeInstruction(){

		int regCount = ir.getInvolvedRegisters().size();

		if(regCount == 2){
			Register reg1 = ir.getInvolvedRegisters().get(0);  //v0
			Register reg2 = ir.getInvolvedRegisters().get(1);  //v1
			
	        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
	        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
	        
	        
	        if(callerEntry != null){
	        	callerEntry.setLineNumber(ir.getLineNumber());
	        	
	        	if(inputParamEntry != null){
	            	if(inputParamEntry.getEntryDetails().isTainted() ){
	            		callerEntry.getEntryDetails().setTainted(true);

	       			   	ArrayList<SourceInfo> existingSiList = inputParamEntry.getEntryDetails().getSourceInfoList();
        			   	ArrayList<SourceInfo> siList = callerEntry.getEntryDetails().getSourceInfoList();
        			   	
        			   	if(existingSiList != null && existingSiList.size()> 0){
        			   		if(siList == null)
        			   			siList = new ArrayList<SourceInfo>();
	        				for(SourceInfo si : existingSiList){
		        	    		if(!siList.contains(si))
		        	    			siList.add(si);
	        				}
        			   	}
        			   	callerEntry.getEntryDetails().setSourceInfoList(siList);
	            	}
    			   	callerEntry.getEntryDetails().setValue(inputParamEntry.getEntryDetails().getValue());
	        	}
	
	        	callerEntry.getEntryDetails().setField(false);
	        	callerEntry.getEntryDetails().setRecord(false);
	        	
//	        	return callerEntry;
	       }
		}
        else if (regCount == 1)
        {
//        	0x28 invoke-direct v6, Ljava/lang/StringBuilder;-><init>()V

        	Register reg1 = ir.getInvolvedRegisters().get(0);  //v6
			
	        SymbolTableEntry reg1Entry = localSymSpace.find(reg1.getName());
	        
	        if(reg1Entry != null)
	        {
	        	EntryDetails reg1EntryDetails = reg1Entry.getEntryDetails();
	        	reg1EntryDetails.setType("Ljava/lang/StringBuilder;"); // Just in case, type has not been set already.
	        	reg1Entry.setLineNumber(ir.getLineNumber());
	        	
	        	reg1EntryDetails.setConstant(false);
	        	reg1EntryDetails.setTainted(false);
	    	    reg1Entry.setInstrInfo(ir.getInstr().getText());
	    	    reg1EntryDetails.setField(false);
	    	    reg1EntryDetails.setRecord(false);
	    	   
	    	    reg1EntryDetails.setValue(" ");
	    	    reg1Entry.setEntryDetails(reg1EntryDetails);
	       }
	       else
	       {
	    	    reg1Entry = new SymbolTableEntry();
	        	
	        	EntryDetails reg1EntryDetails = reg1Entry.getEntryDetails();
	//    	    entry.setName(destReg.getName());
	        	reg1EntryDetails.setType("Ljava/lang/StringBuilder;"); // Just in case, type has not been set already.
	        	reg1Entry.setLineNumber(ir.getLineNumber());
	        	
	        	reg1EntryDetails.setConstant(false);
	        	reg1EntryDetails.setTainted(false);
	    	    reg1Entry.setInstrInfo(ir.getInstr().getText());
	    	    reg1EntryDetails.setField(false);
	    	    reg1EntryDetails.setRecord(false);
	    	   
	    	    reg1EntryDetails.setValue(" ");
	    	    reg1Entry.setEntryDetails(reg1EntryDetails);
	    	    
	        	this.localSymSpace.addEntry(reg1Entry);
	
	       }
        }
       logger.debug("\n <AppendAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
