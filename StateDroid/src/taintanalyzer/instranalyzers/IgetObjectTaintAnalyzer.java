package taintanalyzer.instranalyzers;

import java.util.Enumeration;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.Context;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;

public class IgetObjectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	SymbolTableEntry destLocalEntry;
	SymbolTableEntry srcLocalEntry;
	SymbolTableEntry destGlobalEntry;
	SymbolTableEntry srcGlobalEntry;
	Register srcReg;
	Register destReg;

	public IgetObjectTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(IgetObjectTaintAnalyzer.class);

	}

	// 	0x2a iget-object v0, v3, Lcom/test/maliciousactivity/MainActivity;->myUser Lcom/test/maliciousactivity/User;	
	//  0xe iget-object v2, v6, Lcom/myexample/motivatingexample/MainActivity;->message Ljava/lang/String;	

	// In iget-object case, we need to make a copy of the reference object. So we call .copy() function which keeps a reference
	// to the EntryDetails of original entry. SO whenever (case#1), v0 changes, myUser gets changed automatically.	

	public Object analyzeInstruction() {

		String instrText = ir.getInstr().getText();
		logger.debug(ir.getInstr().getText());

		destReg = ir.getInvolvedRegisters().get(0); //v0
		srcReg = ir.getInvolvedRegisters().get(1); //v3

		String objectName = ir.getMethodOrObjectName();
		srcLocalEntry = localSymSpace.find(srcReg.getName(), objectName);
		if (srcLocalEntry != null) {
			destLocalEntry = (SymbolTableEntry) srcLocalEntry.clone();
			destLocalEntry.setName(destReg.getName());
			destLocalEntry.setLineNumber(ir.getLineNumber());
			destLocalEntry.setInstrInfo(this.ir.getInstr().getText());
			destLocalEntry.getEntryDetails().setType(srcLocalEntry.getEntryDetails().getType());

			this.localSymSpace.addEntry(destLocalEntry);

		} else {
			//Create a dummy entry
			destLocalEntry = new SymbolTableEntry();
			EntryDetails destEntryDetails = destLocalEntry.getEntryDetails();

			destLocalEntry.setName(destReg.getName());
			destLocalEntry.setLineNumber(ir.getLineNumber());
			destLocalEntry.setInstrInfo(this.ir.getInstr().getText());

			destEntryDetails.setType(destReg.getType()); // Lcom/test/maliciousactivity/User;	

			destEntryDetails.setValue(" ");
			destEntryDetails.setTainted(false);
			destEntryDetails.setConstant(false);

			destEntryDetails.setRecord(false);
			destLocalEntry.setEntryDetails(destEntryDetails);

			this.localSymSpace.addEntry(destLocalEntry);
		}

		Context ctxt = Config.getInstance().getPrevMethodContext();
		ctxt.printContext();

		logger.debug("\n <<<<<<<<<IgetTaintAnalyzer>>>>>>>>>>>>");
		localSymSpace.logInfoSymbolSpace();
		return null;
	}

}
