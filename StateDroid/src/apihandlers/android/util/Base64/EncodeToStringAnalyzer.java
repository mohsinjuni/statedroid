
package apihandlers.android.util.Base64;

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

public class EncodeToStringAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public EncodeToStringAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(EncodeToStringAnalyzer.class);
	}

// 		Since it's a static call, just get the bytes entry and return its copy.
//		Since it's also a string, create a deep copy and return the entry.
	
//		0x58 invoke-static v7, v8, Landroid/util/Base64;->encodeToString([B I)Ljava/lang/String;
//		0x5e move-result-object v3
	public Object analyzeInstruction(){
		Register reg0 = ir.getInvolvedRegisters().get(0);  //v7
        SymbolTableEntry inputParam1Entry = localSymSpace.find(reg0.getName());
        
        if(inputParam1Entry != null){
        	SymbolTableEntry retEntry = new SymbolTableEntry(inputParam1Entry); //deep copy
        	return retEntry;
        }
       return null;
	}

}
