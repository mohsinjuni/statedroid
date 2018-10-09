
package apihandlers.java.lang.StringBuilder;

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

public class AppendAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public AppendAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(AppendAnalyzer.class);
	}

//	0xa4 invoke-virtual v8, v2, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
	public Object analyzeInstruction(){

		Register reg1 = ir.getInvolvedRegisters().get(0);  //v8
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v2
		
        SymbolTableEntry returnEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
        
        
        logger.debug("start AppendAnalyzer");
        
        if(returnEntry != null){
//    	    entry.setName(destReg.getName());
        	returnEntry.getEntryDetails().setType(reg1.getType());
        	returnEntry.setLineNumber(ir.getLineNumber());
        	
        	logger.debug("isReg1 Tainted? " + returnEntry.getEntryDetails().isTainted());
        	
        	if(inputParamEntry != null){
        		
        		boolean tainted = false;
        		String sourceAPI = "";
        		String instrInfo = "";
        		
        		String returnEntryValue = returnEntry.getEntryDetails().getValue();
        		String inputParamValue = inputParamEntry.getEntryDetails().getValue();
  
        		returnEntryValue += " " + inputParamValue;
        		returnEntry.getEntryDetails().setValue(returnEntryValue);
        		
        		if(inputParamEntry.getEntryDetails().isTainted()){
        			tainted = true;

       			   	ArrayList<SourceInfo> existingSiList = inputParamEntry.getEntryDetails().getSourceInfoList();
    			   	ArrayList<SourceInfo> siList = returnEntry.getEntryDetails().getSourceInfoList();
    			   	
    			   	if(existingSiList != null && existingSiList.size()> 0){
    			   		if(siList == null)
    			   			siList = new ArrayList<SourceInfo>();
        				for(SourceInfo si : existingSiList){
	        	    		if(!siList.contains(si))
	        	    			siList.add(si);
        				}
    			   	}
    			   	returnEntry.getEntryDetails().setSourceInfoList(siList);
        		}
        		
        		if(!returnEntry.getEntryDetails().isTainted())
        			returnEntry.getEntryDetails().setTainted(tainted);
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
