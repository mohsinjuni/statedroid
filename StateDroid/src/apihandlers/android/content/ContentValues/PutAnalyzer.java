package apihandlers.android.content.ContentValues;

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

	public PutAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(PutAnalyzer.class);
	}

/*
  0x48 invoke-virtual v5, v6, v1, Landroid/content/ContentValues;->put(Ljava/lang/String; Ljava/lang/String;)V

 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction(){

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register contentValuesReg = involvedRegisters.get(0);
		Register keyReg = involvedRegisters.get(1);
		Register valueReg = involvedRegisters.get(2);
		
		SymbolTableEntry contentValueEntry = this.localSymSpace.find(contentValuesReg.getName());
		if(contentValueEntry != null){
			
			SymbolTableEntry keyEntry = this.localSymSpace.find(keyReg.getName());
			SymbolTableEntry valueEntry = this.localSymSpace.find(valueReg.getName());
			
			// If any of the value entry is tainted, we mark the whole object as tainted, mark it as NOT-record. 
			//Effectively, it means, if one value is tainted, whole content-value object is tainted... Untainting is difficult. 
			//It will do create False Positive
			
			//Since we are interested only in valueEntry, we ignore keyEntry for now.
			if(valueEntry != null){
				if(valueEntry.getEntryDetails().isTainted()){
					ArrayList<SourceInfo> siList = contentValueEntry.getEntryDetails().getSourceInfoList();
					ArrayList<SourceInfo> valueSiList = valueEntry.getEntryDetails().getSourceInfoList();
					
					if(siList == null)
						siList = new ArrayList<SourceInfo>();
					
					for(SourceInfo si: valueSiList){
						if(!siList.contains(si))
							siList.add(si);
					}
					contentValueEntry.getEntryDetails().setTainted(true);
					contentValueEntry.getEntryDetails().setSourceInfoList(siList);
				}
				contentValueEntry.getEntryDetails().setRecord(false);
			}
		}else {
			contentValueEntry = new SymbolTableEntry();
			SymbolTableEntry valueEntry = this.localSymSpace.find(valueReg.getName());
			
			if(valueEntry != null){
				if(valueEntry.getEntryDetails().isTainted()){
					ArrayList<SourceInfo> siList = contentValueEntry.getEntryDetails().getSourceInfoList();
					ArrayList<SourceInfo> valueSiList = valueEntry.getEntryDetails().getSourceInfoList();
					if(siList == null)
						siList = new ArrayList<SourceInfo>();
					
					for(SourceInfo si: valueSiList){
						if(!siList.contains(si))
							siList.add(si);
					}
					contentValueEntry.getEntryDetails().setTainted(true);
					contentValueEntry.getEntryDetails().setSourceInfoList(siList);
				}
				contentValueEntry.getEntryDetails().setRecord(false);
			}
		}
     	logger.debug("\n Bundle.putString()");
//	       localSymSpace.printSymbolSpace();
	    return null;
	}
}
