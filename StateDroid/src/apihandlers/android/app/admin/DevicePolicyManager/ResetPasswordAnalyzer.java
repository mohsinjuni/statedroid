
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
import patternMatcher.events.csm.ResetPasswordEvent;
import patternMatcher.events.csm.keyguardmanager.KeyguardRestrictedInputModeEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class ResetPasswordAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	/*
	0x0 const-string v3, 'device_policy'
		0x4 invoke-virtual v5, v3, Lcom/example/lockscreen/MainActivity;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;
		0xa move-result-object v1
		0xc check-cast v1, Landroid/app/admin/DevicePolicyManager;
		0x10 new-instance v0, Landroid/content/ComponentName;
		0x14 const-class v3, Lcom/example/lockscreen/MainActivity;
		0x18 invoke-direct v0, v6, v3, Landroid/content/ComponentName;-><init>(Landroid/content/Context; Ljava/lang/Class;)V
		0x1e const/4 v3, 0
		0x20 invoke-virtual v1, v0, v3, Landroid/app/admin/DevicePolicyManager;->setPasswordQuality(Landroid/content/ComponentName; I)V
		0x26 const/4 v3, 5
		0x28 invoke-virtual v1, v0, v3, Landroid/app/admin/DevicePolicyManager;->setPasswordMinimumLength(Landroid/content/ComponentName; I)V
		0x2e const-string v3, '123456'
		0x32 const/4 v4, 1
		0x34 invoke-virtual v1, v3, v4, Landroid/app/admin/DevicePolicyManager;->resetPassword(Ljava/lang/String; I)Z
		0x3a move-result v2
		
		
		boolean resetPassword (String password, int flags)
	 */

	public ResetPasswordAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(ResetPasswordAnalyzer.class);
		this.ta = ta;
	}

	public Object analyzeInstruction(){
		
		Register reg0 = ir.getInvolvedRegisters().get(0); // v0
		Register reg1 = ir.getInvolvedRegisters().get(1);  //v1
		
		
        SymbolTableEntry dpmEntry = localSymSpace.find(reg0.getName());
        if(dpmEntry != null){
        	SymbolTableEntry passwordEntry = localSymSpace.find(reg1.getName());
        	Hashtable recordFieldList = dpmEntry.getEntryDetails().getRecordFieldList();
        	if(recordFieldList == null){
        		recordFieldList = new Hashtable();
        	}
        	recordFieldList.put("password", passwordEntry);

      		EventFactory.getInstance().registerEvent("resetPasswordEvent", new ResetPasswordEvent());
			Event event = EventFactory.getInstance().createEvent("resetPasswordEvent");
			event.setName("resetPasswordEvent");
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
