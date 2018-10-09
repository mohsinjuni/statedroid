package apihandlers.java.io.File;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetCanonicalPathAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetCanonicalPathAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(GetCanonicalPathAnalyzer.class);
	}

//	0x40 invoke-virtual v1, Ljava/io/File;->getCanonicalPath()Ljava/lang/String;
	
/* Prototypes
 * 
   String getCanonicalPath() 
         Returns the canonical pathname string of this abstract pathname.
               	
     	http://docs.oracle.com/javase/6/docs/api/java/io/File.html
*/     
     
	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register fileReg = involvedRegisters.get(0);

        SymbolTableEntry fileEntry = localSymSpace.find(fileReg.getName());
        SymbolTableEntry filePathEntry = new SymbolTableEntry();
        
        
        if(fileEntry != null)
        {
        	//Since this method returns a string, which is an immutable object, so effectively copies value of the string,
        	// so it needs deep copy.
        	
			filePathEntry = new SymbolTableEntry(fileEntry); // deep copy
        	filePathEntry.getEntryDetails().setValue(fileEntry.getEntryDetails().getValue());
        	
        	logger.error("GETCANONICAl = " + filePathEntry.getEntryDetails().getValue());
        }
    	filePathEntry.getEntryDetails().setType(ir.getReturnType());


       logger.debug("\n CreateTempFileAnalyzer");
//	       localSymSpace.printSymbolSpace();
	   return filePathEntry;
	}
}
