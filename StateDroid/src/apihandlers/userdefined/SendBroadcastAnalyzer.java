package apihandlers.userdefined;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
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
import patternMatcher.events.csm.SendBroadcastEvent;
import patternMatcher.events.csm.StartActivityEvent;
import patternMatcher.events.csm.context.StartActivityIntentEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class SendBroadcastAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SendBroadcastAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(SendBroadcastAnalyzer.class);
	}

/*
   getApplicationContext().sendBroadcast(addIntent); 
   
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		int regCount = involvedRegisters.size();
		Register callerApiReg = involvedRegisters.get(0);
		
		//caller + intent
		if(regCount == 2) {
			// Just intent is being passed.
			Register intentReg = involvedRegisters.get(1);
			SymbolTableEntry intentEntry = localSymSpace.find(intentReg.getName());
			
			if(intentEntry != null){
				EventFactory.getInstance().registerEvent("sendBroadcastEvent", new SendBroadcastEvent());
				Event event = EventFactory.getInstance().createEvent("sendBroadcastEvent");
				event.setName("sendBroadcastEvent");
				event.setCurrMethodName(instr.getCurrMethodName());
				event.setCurrPkgClsName(instr.getCurrPkgClassName());
				
				event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				event.setCurrComponentName(ta.getCurrComponentName());
				event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
				
				event.getEventInfo().put("intent", intentEntry );
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				ta.setCurrCSMEvent(event);
			}
		}
       logger.debug("\n startActivityAnalyzer");
       return null;
		
	}
}
