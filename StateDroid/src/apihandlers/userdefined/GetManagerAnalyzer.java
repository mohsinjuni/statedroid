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
import patternMatcher.events.csm.GetManagerEvent;
import patternMatcher.events.csm.GetSystemServiceEvent;
import patternMatcher.events.csm.StartActivityEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetManagerAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetManagerAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(GetManagerAnalyzer.class);
	}

	public Object analyzeInstruction(){

//		0x62 invoke-virtual v9, v10, Lcom/sssp/MyAdmin;->getManager(Landroid/content/Context;)Landroid/app/admin/DevicePolicyManager;
//		0x68 move-result-object v9

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		int regCount = involvedRegisters.size();
		
		Register callerApiReg = involvedRegisters.get(0);
		String returnType = ir.getReturnType();
		if(returnType.equalsIgnoreCase("Landroid/app/admin/DevicePolicyManager;")){
			SymbolTableEntry dpmEntry = new SymbolTableEntry();
			dpmEntry.getEntryDetails().setType("Landroid/app/admin/DevicePolicyManager;");

			EventFactory.getInstance().registerEvent("getManagerEvent", new GetManagerEvent());
			Event event = EventFactory.getInstance().createEvent("getManagerEvent");
			event.setName("getManagerEvent");
			
			InstructionReturnValue retValue = new InstructionReturnValue(dpmEntry, event);
//				ta.setCurrCSMEvent(startActivityEvent);
			return retValue;
		}
       logger.debug("\n GetManagerAnalyzer");
       return null;
		
	}
}
