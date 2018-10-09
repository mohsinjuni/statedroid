
package apihandlers.android.app.admin.DevicePolicyManager;

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.LockNowEvent;
import patternMatcher.events.csm.ResetPasswordEvent;
import patternMatcher.events.csm.keyguardmanager.KeyguardRestrictedInputModeEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class LockNowAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	/*
		0x34 invoke-virtual v1, v3, Landroid/app/admin/DevicePolicyManager;->lockNow(I)Z
		0x3a move-result v2
		
	 */

	public LockNowAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(LockNowAnalyzer.class);
		this.ta = ta;
	}

	public Object analyzeInstruction(){
		
		Register reg0 = ir.getInvolvedRegisters().get(0); // v0
		
		
        SymbolTableEntry dpmEntry = localSymSpace.find(reg0.getName());
        if(dpmEntry != null){

      		EventFactory.getInstance().registerEvent("lockNowEvent", new LockNowEvent());
			Event event = EventFactory.getInstance().createEvent("lockNowEvent");
			event.setName("lockNowEvent");
			Hashtable eventInfo = (Hashtable) event.getEventInfo();
	    	eventInfo.put(InstructionResponse.CLASS_NAME, ir);
	    	eventInfo.put("instrText", ir.getInstr().getText());
	    	eventInfo.put("dpmEntry", dpmEntry);
			event.setEventInfo(eventInfo);

			ta.setCurrCSMEvent(event);
		
        }
    	return dpmEntry;
	}
}
