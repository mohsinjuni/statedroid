
package apihandlers.android.media.AudioManager;

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

public class GetVibrateSettingAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private final String fieldName = "path";

	public GetVibrateSettingAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(GetVibrateSettingAnalyzer.class);
		this.ta = ta;
	}
	
//		0x26 invoke-virtual v0, v2, Landroid/media/AudioManager;->getVibrateSetting(I)I
//		0x2c move-result v0
	
//TODO: we ignore notification and vibration (given as parameter) for now.
	public Object analyzeInstruction(){
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v0
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());

        if(callerEntry != null){
        	SymbolTableEntry returnEntry = new SymbolTableEntry();
        	returnEntry.getEntryDetails().setValue("existingVibrateSettingValue");
        	returnEntry.getEntryDetails().setType("I");
        	return returnEntry;
       }
       return null;
	}
}
