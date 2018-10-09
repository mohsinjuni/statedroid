package apihandlers.java.util.List;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import apihandlers.java.lang.StringBuilder.ToStringAnalyzer;
import configuration.Config;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;

public class AddAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public AddAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(AddAnalyzer.class);
	}

//		0x128 iget-object v0, v15, Lcom/bgu/sherlock/Moriarty/moriartyUtils/MoriartyBrowserInfo$MyThread;->dataList Ljava/util/List;
//		0x12c iget-object v1, v15, Lcom/bgu/sherlock/Moriarty/moriartyUtils/MoriartyBrowserInfo$MyThread;->data Lorg/json/JSONObject;
//		0x130 invoke-interface v0, v1, Ljava/util/List;->add(Ljava/lang/Object;)Z

	public Object analyzeInstruction(){

		String instrText = ir.getInstr().getText();
		Register destReg = ir.getInvolvedRegisters().get(0);
		Register inputValueReg = ir.getInvolvedRegisters().get(1);
     	   
        SymbolTableEntry entry = localSymSpace.find(destReg.getName());
        SymbolTableEntry inputValueEntry = localSymSpace.find(inputValueReg.getName());
        boolean isNewEntry = false;

        if(inputValueEntry != null){
        	if(inputValueEntry.getEntryDetails().isTainted()){
        		//Only then we are interested in this.
        		
        		if(entry == null){
        			entry = new SymbolTableEntry();
        			entry.setName(destReg.getName());
        			isNewEntry = true;
        		}
   			   	ArrayList<SourceInfo> existingSiList = inputValueEntry.getEntryDetails().getSourceInfoList();
			   	ArrayList<SourceInfo> siList = entry.getEntryDetails().getSourceInfoList();
			   	
			   	if(existingSiList != null && existingSiList.size()> 0){
			   		if(siList == null)
			   			siList = new ArrayList<SourceInfo>();
    				for(SourceInfo si : existingSiList){
        	    		if(!siList.contains(si))
        	    			siList.add(si);
    				}
			   	}
			   	entry.getEntryDetails().setSourceInfoList(siList);
			   	entry.getEntryDetails().setTainted(true);
			   	
			   	if(isNewEntry){
			   		this.localSymSpace.addEntry(entry);
			   	}
        	}
        }
       logger.debug("\n ToStringHandler");
       return null;
	}
}
