package apihandlers.userdefined;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.GetPackageManagerDefinedEvent;
import patternMatcher.events.csm.GetSystemServiceEvent;
import patternMatcher.events.csm.StartActivityEvent;
import patternMatcher.events.csm.cursor.CursorGetStringEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetPackageManagerAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetPackageManagerAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(GetPackageManagerAnalyzer.class);
	}

//		0x24 invoke-virtual v5, Lcom/example/appRemoval2/MainActivity;->getPackageManager()Landroid/content/pm/PackageManager;
//		0x2a move-result-object v0

	public Object analyzeInstruction(){

		SymbolTableEntry returnEntry = new SymbolTableEntry();
		returnEntry.getEntryDetails().setType(ir.getReturnType());
		
		EventFactory.getInstance().registerEvent("getPackageManagerDefinedEvent", new GetPackageManagerDefinedEvent());
		Event event = EventFactory.getInstance().createEvent("getPackageManagerDefinedEvent");
		event.setName("getPackageManagerDefinedEvent");
		
		InstructionReturnValue irValue = new InstructionReturnValue(returnEntry, event);
		return irValue;
	}
}
