package apihandlers.android.content.Intent;

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
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;


public class SetDataAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SetDataAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(SetDataAnalyzer.class);
	}

/*
 * 	 *   	0xa new-instance v0, Landroid/content/Intent;
			0xe const-string v1, 'android.intent.action.CALL'
			0x12 invoke-direct v0, v1, Landroid/content/Intent;-><init>(Ljava/lang/String;)V
			
			0x42 invoke-static v1, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
			0x48 move-result-object v1
			0x4a invoke-virtual v0, v1, Landroid/content/Intent;->setData(Landroid/net/Uri;)Landroid/content/Intent;
 * 			
 */
	public Object analyzeInstruction(){
		
		String pkgClsName = ir.getCallerAPIName();
		String methdObjectName = ir.getMethodOrObjectName();
		String qualifiedAPIName = pkgClsName.concat("->").concat(methdObjectName);
	
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());

		// caller + 1 input param
		if(involvedRegisters.size() == 2) {
			Register param1Reg = involvedRegisters.get(1);
			SymbolTableEntry uriEntry = this.localSymSpace.find(param1Reg.getName());

			if(param1Reg.getType().equalsIgnoreCase("Landroid/net/Uri;")){
				if(callerApiEntry != null){
					Hashtable recordFieldList = callerApiEntry.getEntryDetails().getRecordFieldList();
					if(recordFieldList == null) 
						recordFieldList = new Hashtable();
					recordFieldList.put("uriEntry", uriEntry);
					callerApiEntry.getEntryDetails().setRecordFieldList(recordFieldList);
				}
				EventFactory.getInstance().registerEvent("intentSetDataEvent", new IntentSetDataEvent());
				Event event = EventFactory.getInstance().createEvent("intentSetDataEvent");
				event.setName("intentSetDataEvent");
							
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				ta.setCurrCSMEvent(event);
			}
		}
 	   localSymSpace.logInfoSymbolSpace();
	   return null;
	}
}
