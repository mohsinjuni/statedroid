package taintanalyzer.instranalyzers;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class FilledNewArrayTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;

	/*
	 * 
	 * 0xb4 filled-new-array v0, v5, [I
	 * 0xba move-result-object v0
	 * 
	 * 
	 * filled-new-array {parameters},type_id
	 * 
	 * Generates a new array of type_id and fills it with the parameters5.
	 * Reference to the newly generated
	 * array can be obtained by a move-result-object instruction, immediately
	 * following the filled-new-array instruction.
	 */

	//v0 and v5 are basically array elements. In most cases, it contains only two registers.

	public FilledNewArrayTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(NewArrayTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {
		Register param1Reg;
		Register param2Reg;

		String text = ir.getInstr().getText();

		if (!text.contains("0x0 filled-new-array")) {
			//0x0 filled-new-array 			  This case generates null-pointer exception. Let's not do anything for this.

			//		   System.out.println(ir.getInstr().getText());
			int regCount = ir.getInvolvedRegisters().size();
			if (regCount > 0) {
				param1Reg = ir.getInvolvedRegisters().get(0); // v0
				param2Reg = ir.getInvolvedRegisters().get(1); // v5

				SymbolTableEntry param1Entry = localSymSpace.find(param1Reg.getName());
				SymbolTableEntry param2Entry = localSymSpace.find(param2Reg.getName());

				//Creates a new array, so even if an entry exists, it will be replaced.

				SymbolTableEntry arrayEntry = new SymbolTableEntry();
				EntryDetails arrayEntryDetails = arrayEntry.getEntryDetails();

				arrayEntry.setInstrInfo(ir.getInstr().getText());
				arrayEntry.setLineNumber(ir.getLineNumber());
				arrayEntry.setName(param1Reg.getName());

				arrayEntryDetails.setType(ir.getReturnType());

				Hashtable recordFieldList = (Hashtable) arrayEntryDetails.getRecordFieldList();

				if (param1Entry != null) {
					if (recordFieldList == null)
						recordFieldList = new Hashtable();
					recordFieldList.put("key1", param1Entry);
				}
				if (param2Entry != null) {
					if (recordFieldList == null)
						recordFieldList = new Hashtable();
					recordFieldList.put("key2", param2Entry);
				}
				arrayEntryDetails.setRecordFieldList(recordFieldList);
				arrayEntry.setEntryDetails(arrayEntryDetails);

				//	   this.localSymSpace.addEntry(arrayEntry);

				logger.debug("\n FilledNewArrayTaintAnalyzer");
				localSymSpace.logInfoSymbolSpace();

				return arrayEntry;
			}
		}
		return null;
	}

}
