
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

public class GetRingerModeAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private final String fieldName = "path";

	public GetRingerModeAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(GetRingerModeAnalyzer.class);
		this.ta = ta;
	}
	
//		0x16 invoke-virtual v0, Landroid/media/AudioManager;->getRingerMode()I
//		0x1c move-result v0

	public Object analyzeInstruction(){
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v0
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());

        if(callerEntry != null){
        	SymbolTableEntry returnEntry = new SymbolTableEntry();
        	returnEntry.getEntryDetails().setValue("existingRingerModeValue");
        	returnEntry.getEntryDetails().setType("I");
        	return returnEntry;
       }
       return null;
	}
}
