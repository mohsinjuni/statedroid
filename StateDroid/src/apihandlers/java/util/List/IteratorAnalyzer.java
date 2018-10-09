
package apihandlers.java.util.List;

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

public class IteratorAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public IteratorAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(IteratorAnalyzer.class);
	}

//		0x1ee invoke-interface v10, Ljava/util/List;->iterator()Ljava/util/Iterator;
//		0x1f4 move-result-object v23
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
