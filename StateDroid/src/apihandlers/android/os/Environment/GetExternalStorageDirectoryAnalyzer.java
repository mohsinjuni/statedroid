package apihandlers.android.os.Environment;

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetExternalStorageDirectoryAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetExternalStorageDirectoryAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetExternalStorageDirectoryAnalyzer.class);
	}

//	0x2 invoke-static Landroid/os/Environment;->getExternalStorageDirectory()Ljava/io/File;
	
	public Object analyzeInstruction()
	{

		SymbolTableEntry fileEntry = new SymbolTableEntry();
		
	    fileEntry.setName("");   // will be set by move-result instruction.
    	
    	fileEntry.getEntryDetails().setType(ir.getReturnType()); 
    	fileEntry.setLineNumber(ir.getLineNumber());
    	
    	fileEntry.getEntryDetails().setValue("/sdcard/");
    	fileEntry.setInstrInfo(ir.getInstr().getText());
    	        	   
     	logger.debug("\n getExternalStorageDirectory()");
//	       localSymSpace.printSymbolSpace();
	    return fileEntry;
	}
}
