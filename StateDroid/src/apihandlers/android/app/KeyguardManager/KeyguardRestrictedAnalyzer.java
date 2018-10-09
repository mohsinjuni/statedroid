
package apihandlers.android.app.KeyguardManager;

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.keyguardmanager.KeyguardRestrictedInputModeEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class KeyguardRestrictedAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	/*
	 * 	0x6 const-string v1, 'keyguard'
		0xa invoke-virtual v4, v1, Lcom/example/testapp/MainActivity;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;
		0x10 move-result-object v1
		0x12 check-cast v1, Landroid/app/KeyguardManager;
		0x16 invoke-virtual v1, Landroid/app/KeyguardManager;->inKeyguardRestrictedInputMode()Z
		0x1c move-result v0

	 */

	public KeyguardRestrictedAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(KeyguardRestrictedAnalyzer.class);
		this.ta = ta;
	}

	public Object analyzeInstruction()
	{
		Register reg1 = ir.getInvolvedRegisters().get(0);  //v1
        SymbolTableEntry keyguardMgrEntry = localSymSpace.find(reg1.getName());
        
  		EventFactory.getInstance().registerEvent("keyguardRestrictedInputModeEvent", new KeyguardRestrictedInputModeEvent());
		Event event = EventFactory.getInstance().createEvent("keyguardRestrictedInputModeEvent");
		event.setName("keyguardRestrictedInputModeEvent");
		
		if(keyguardMgrEntry != null){
			Hashtable eventInfo = (Hashtable) event.getEventInfo();
	    	eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			event.setEventInfo(eventInfo);
			ta.setCurrCSMEvent(event);
       }
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		returnEntry.getEntryDetails().setType(ir.getReturnType());
    	return returnEntry;
		
	}
}
