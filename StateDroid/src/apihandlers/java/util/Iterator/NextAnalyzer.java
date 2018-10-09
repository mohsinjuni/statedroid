
package apihandlers.java.util.Iterator;

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

public class NextAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public NextAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(NextAnalyzer.class);
	}

//	0x202 invoke-interface/range v23, Ljava/util/Iterator;->next()Ljava/lang/Object;
//	0x208 move-result-object v14

	public Object analyzeInstruction(){
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v10
        SymbolTableEntry entry = localSymSpace.find(reg1.getName());
        
        if(entry != null){
        	SymbolTableEntry retEntry = new SymbolTableEntry(entry); //deep copy
        	return retEntry;
        }
       return null;
	}

}
