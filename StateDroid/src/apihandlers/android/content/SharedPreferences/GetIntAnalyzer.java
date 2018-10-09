package apihandlers.android.content.SharedPreferences;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;


public class GetIntAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetIntAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetIntAnalyzer.class);
	}

/*
 * 
 		0x3a const-string v4, 'volume'
		0x3e const/4 v5, 3
		0x40 invoke-interface v2, v4, v5, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String; I)I
		0x46 move-result v3
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction()
	{

		String pkgClsName = ir.getCallerAPIName();
		String methdObjectName = ir.getMethodOrObjectName();
		String qualifiedAPIName = pkgClsName.concat("->").concat(methdObjectName);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register reg0 = involvedRegisters.get(0);
		Register reg1 = involvedRegisters.get(1);
		Register reg2 = involvedRegisters.get(2);
	
		SymbolTableEntry sharedPrefEntry = this.localSymSpace.find(reg0.getName());
		SymbolTableEntry keyEntry = this.localSymSpace.find(reg1.getName());
		SymbolTableEntry defaultValueEntry = this.localSymSpace.find(reg2.getName());

		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();

		if(sharedPrefEntry != null && keyEntry != null){
			String key = keyEntry.getEntryDetails().getValue();
			String defaultValue = "";
			if(defaultValueEntry != null){
				defaultValue = defaultValueEntry.getEntryDetails().getValue();
			}
			String value="";
			if(!key.isEmpty()){
				value = "Shared preference value with key= " + key + ", and default value= " + defaultValue;
			}else{
				value = "some value from Shared preferences";
			}
			returnEntryDetails.setValue(value);
		}
		returnEntryDetails.setType(ir.getReturnType());
		returnEntry.setInstrInfo(ir.getInstr().getText());
		returnEntry.setEntryDetails(returnEntryDetails);
	    return returnEntry;
	}
}
