
package apihandlers.java.lang.String;

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

public class GetBytesAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetBytesAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(GetBytesAnalyzer.class);
	}

		// v7 could be UTF8 where v10 contains the original value, so just return it as it is.
//		0x32 invoke-virtual v10, v7, Ljava/lang/String;->getBytes(Ljava/lang/String;)[B
//		0x38 move-result-object v1
	
	public Object analyzeInstruction(){
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v10
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        
        if(callerEntry != null){
        	SymbolTableEntry retEntry = new SymbolTableEntry(callerEntry); //deep copy
        	return retEntry;
        }
       logger.debug("\n <GetCharAnalyzer>");
       return null;
	}
}
