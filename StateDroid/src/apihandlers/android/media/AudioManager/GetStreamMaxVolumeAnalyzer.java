
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

public class GetStreamMaxVolumeAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private final String fieldName = "path";

	public GetStreamMaxVolumeAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(GetStreamMaxVolumeAnalyzer.class);
		this.ta = ta;

	}

//			0x1dc invoke-virtual v3, v15, Landroid/media/AudioManager;->getStreamMaxVolume(I)I


//TODO: other method takes FileDescriptor as input parameter.
	public Object analyzeInstruction()
	{

		Register reg1 = ir.getInvolvedRegisters().get(0);  //v3
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v15
		
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
        

        //Value of v15 matters here. If it is STREAM_VOICE_CALL, its return value be 5.
        if(callerEntry != null && inputParamEntry != null)
        {
        	
        	SymbolTableEntry returnEntry = new SymbolTableEntry(inputParamEntry); // make a deep copy and modify it laters.
        	
        	returnEntry.setName(""); //will be set by move- instruction
        	
        	if(inputParamEntry.getEntryDetails().getValue().trim().equalsIgnoreCase("0"))
        		returnEntry.getEntryDetails().setValue("5");
        	
        	//Nothing else needs to be changed.
        	
            logger.debug("\n <GetStreamMaxVolumeAnalyzer>");
        	return returnEntry;
       }
       logger.debug("\n <GetStreamMaxVolumeAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
