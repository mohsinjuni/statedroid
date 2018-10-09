package apihandlers.java.io.File;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class CreateTempFileAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public CreateTempFileAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(CreateTempFileAnalyzer.class);
	}

//	0x12 invoke-static v4, v5, v3, Ljava/io/File;->createTempFile(Ljava/lang/String; Ljava/lang/String; Ljava/io/File;)Ljava/io/File;
	
/* Prototypes
 * 
 * 	static File	createTempFile(String prefix, String suffix) 
    	Creates an empty file in the default temporary-file directory, using the given prefix and suffix to generate its name.
	static File	createTempFile(String prefix, String suffix, File directory) 
     	Creates a new empty file in the specified directory, using the given prefix and suffix strings to generate its name.
     	
     	http://docs.oracle.com/javase/6/docs/api/java/io/File.html
*/     
     
	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register prefixReg = involvedRegisters.get(0);
		Register suffixReg = involvedRegisters.get(1);

		Register directoryReg=null;
		SymbolTableEntry directoryEntry= null;
		
		if(involvedRegisters.size() > 2)
		{
			directoryReg = involvedRegisters.get(2);
		    directoryEntry = localSymSpace.find(directoryReg.getName());
		}	
        SymbolTableEntry prefixEntry = localSymSpace.find(prefixReg.getName());
        SymbolTableEntry suffixEntry = localSymSpace.find(suffixReg.getName());
        
        String filePath = "<DefaultTempDirectory>";
        String prefix= "<file>";
        String suffix = ".tmp";

        if(directoryEntry != null)
        {
        	String value = directoryEntry.getEntryDetails().getValue();
        	if(value != null )  // TODO && Integer.parseInt(value) != 0  when null is passed as third parameter
        		filePath = value;
        }
        
        if(prefixEntry != null && prefixEntry.getEntryDetails().getValue() != null)
        {
        	String value = prefixEntry.getEntryDetails().getValue();
        	if(value != null)
        		prefix = value;
        }
        if(suffixEntry != null && suffixEntry.getEntryDetails().getValue() != null)
        {
        	String value = suffixEntry.getEntryDetails().getValue();
        	if(value != null)
        		suffix = value;
        }
        
        filePath = filePath.concat("/").concat(prefix).concat(suffix);

        SymbolTableEntry returnEntry = new SymbolTableEntry();
        
        EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
        
        logger.debug("CreateTempFileAnalyzer::" + filePath);
        returnEntry.setName("");   // will be set by move-result instruction.
    	
        returnEntryDetails.setType(ir.getReturnType()); 
        returnEntry.setLineNumber(ir.getLineNumber());
        returnEntryDetails.setTainted(false);
        returnEntryDetails.setConstant(false);
        returnEntryDetails.setField(false);
    	
        returnEntryDetails.setRecord(false);
    	
        returnEntryDetails.setValue(filePath);
//        returnEntry.setSourceAPI(" "); 
        returnEntry.setInstrInfo(ir.getInstr().getText());

		returnEntry.setEntryDetails(returnEntryDetails);
        
       logger.debug("\n CreateTempFileAnalyzer");
	    return returnEntry;
	}
}
