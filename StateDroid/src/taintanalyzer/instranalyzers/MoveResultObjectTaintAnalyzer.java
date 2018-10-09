package taintanalyzer.instranalyzers;

import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class MoveResultObjectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	TaintAnalyzer ta;

	public MoveResultObjectTaintAnalyzer(TaintAnalyzer taParam) {
		ta = taParam;
		this.ir = taParam.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(MoveResultObjectTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		Register destReg = ir.getInvolvedRegisters().get(0);
		String instrText = ir.getInstr().getText();
		//		if(instrText.contains("0x96 move-result-object v21")){
		//			System.out.println(instrText);
		//		}
		SymbolTableEntry returnEntryClone = null;
		Object obj = ta.getInstrReturnedObject();

		if (obj != null) {
			SymbolTableEntry returnEntry = null;
			if (obj instanceof SymbolTableEntry) {
				returnEntry = (SymbolTableEntry) obj;
				returnEntryClone = (SymbolTableEntry) returnEntry.clone(); // shallow copy
				returnEntryClone.setName(destReg.getName());
				returnEntryClone.setLineNumber(ir.getLineNumber());
				returnEntryClone.setInstrInfo(returnEntry.getInstrInfo());
				this.localSymSpace.addEntry(returnEntryClone);
			} else if (obj instanceof InstructionReturnValue) {
				InstructionReturnValue instReturnObj = (InstructionReturnValue) obj;
				returnEntry = instReturnObj.getReturnEntry();

				returnEntryClone = (SymbolTableEntry) returnEntry.clone(); // shallow copy
				returnEntryClone.setName(destReg.getName());
				returnEntryClone.setLineNumber(ir.getLineNumber());
				returnEntryClone.setInstrInfo(returnEntry.getInstrInfo());
				this.localSymSpace.addEntry(returnEntryClone);

				Event event = instReturnObj.getEvent();
				event.getEventInfo().put("entry", returnEntryClone);
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);

				ta.setCurrCSMEvent(event);
			}
		} else {
			//Create a dummy entry
			returnEntryClone = new SymbolTableEntry();
			returnEntryClone.setName(destReg.getName());

			EntryDetails entrDetails = returnEntryClone.getEntryDetails();
			InstructionResponse prevIR = ta.getPrev().getInstResponse();
			if (prevIR != null) {
				String type = prevIR.getReturnType();
				if (type != null)
					entrDetails.setType(type); // Hopefully it's not null.
			}

			//Since it is a new entry, other values will be set to default.

			returnEntryClone.setEntryDetails(entrDetails);

			this.localSymSpace.addEntry(returnEntryClone);

		}

		logger.debug("\n MoveResultTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		return null;
	}
}
