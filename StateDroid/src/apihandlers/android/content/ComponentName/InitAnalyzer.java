package apihandlers.android.content.ComponentName;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.intent.IntentActionDefinedEvent;
import patternMatcher.events.csm.intent.IntentActionUriDefinedEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class InitAnalyzer extends BaseTaintAnalyzer {
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(InitAnalyzer.class);
	}
	
	// 0x4a new-instance v1, Landroid/content/ComponentName;
	// 0x4e const-class v2, Lcom/example/appRemoval2/MainActivity;
	// 0x52 invoke-direct v1, v5, v2,
	// Landroid/content/ComponentName;-><init>(Landroid/content/Context;
	// Ljava/lang/Class;)V

	public Object analyzeInstruction() {
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg
				.getName());

		if (callerApiEntry != null) {
			Register param1Reg = involvedRegisters.get(1);
			Register param2Reg = involvedRegisters.get(2);

			SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
			SymbolTableEntry param2Entry = this.localSymSpace.find(param2Reg.getName());
			Hashtable recordFieldList = callerApiEntry.getEntryDetails().getRecordFieldList();

			if (recordFieldList == null)
				recordFieldList = new Hashtable();
			if (param2Entry != null) {
				recordFieldList.put("compEntry", param2Entry);
				callerApiEntry.getEntryDetails().setRecordFieldList(
						recordFieldList);
			}
		}
		logger.debug("\n ComponentName.InitAnalyzer");
		return null;
	}
}
