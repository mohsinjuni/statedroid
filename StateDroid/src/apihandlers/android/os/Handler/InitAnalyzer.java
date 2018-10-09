package apihandlers.android.os.Handler;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;

public class InitAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(InitAnalyzer.class);
	}

// Lde/ecspride/LooperThread$1; <init> ()V 0
// <init>-BB@0x0 0x0 0x8[ NEXT =  ] [ PREV = ] 
// 0x0 invoke-direct v0, Landroid/os/Handler;-><init>()V
	
	public Object analyzeInstruction()
	{

		Register destReg = ir.getInvolvedRegisters().get(0);
     	   
        SymbolTableEntry entry = this.localSymSpace.find(destReg.getName());

        if(entry == null){
        	entry = new SymbolTableEntry();
        	entry.setName(destReg.getName());
        	
	        EntryDetails entryDetails = entry.getEntryDetails();
	        String currPkgClassName = ir.getInstr().getCurrPkgClassName();
	        if(!currPkgClassName.endsWith(";")){
	        	currPkgClassName = currPkgClassName + ";";
	        }
	        entryDetails.setType(currPkgClassName);
	        entry.setEntryDetails(entryDetails);
	        
	        this.localSymSpace.addEntry(entry);
        }else{
        	
        }
        logger.debug("\n InitAnalyzer");
//	       localSymSpace.printSymbolSpace();
	       return null;
	}
}
