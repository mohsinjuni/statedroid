package apihandlers.java.lang.Object;

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

//	0x0 invoke-direct v0, Ljava/lang/Object;-><init>()V
	
	public Object analyzeInstruction()
	{

		Register destReg = ir.getInvolvedRegisters().get(0);
     	   
        SymbolTableEntry entry=localSymSpace.find(destReg.getName());

        String regType = ir.getInstr().getCurrPkgClassName();

        if(entry != null && entry.getEntryDetails().getType().equalsIgnoreCase(regType))
        {
        	//This means that entry has already been defined there. Weird thing in Dalvik byte code, I know. :)

        	// Ignore in a case where an entry has been defined already. This case arises when we <init> method of a class.
        	// We push parameter entries then, which also includes 'this' variable. We do that object creation there because 
        	// in some of <init> methods, object.init() is not called. So we just wanted to make sure we would have that object. 
        	// We 
        }
       else
       {
    	   entry = new SymbolTableEntry();

    	   EntryDetails entryDetails = entry.getEntryDetails();
    	   
     	    entry.setName(destReg.getName());
       	    entry.setLineNumber(ir.getLineNumber());

     	    entryDetails.setType(regType);
     	    entryDetails.setTainted(false);
     	    entryDetails.setConstant(false);
     	    entryDetails.setField(false);
     	    entryDetails.setRecord(false);
     	    entry.setEntryDetails(entryDetails);
	   	    localSymSpace.addEntry(entry);
       }
	       logger.debug("\n InitAnalyzer");
	       return null;
	}
}
