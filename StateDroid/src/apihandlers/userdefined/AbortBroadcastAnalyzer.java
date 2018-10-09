package apihandlers.userdefined;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.SymbolSpace;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class AbortBroadcastAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public AbortBroadcastAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(AbortBroadcastAnalyzer.class);
	}

/*
 * 0x0 invoke-virtual v0, Lcom/example/smsreceiver/SMSBroadcastReceiver;->abortBroadcast()V
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */

	public Object analyzeInstruction()
	{

		// Detecting type of abort notification is done by CSM. From here, we send all the information and let its CSM 
		// filter or extract remaining information.
		int regCount = ir.getInvolvedRegisters().size();
		
		EventFactory.getInstance().registerEvent("abortBroadcastEvent", new AbortBroadcastEvent());
		
		Event broadcastEvent = EventFactory.getInstance().createEvent("abortBroadcastEvent");
		
		broadcastEvent.setName("abortBroadcastEvent");
		broadcastEvent.setCurrMethodName(instr.getCurrMethodName());
		broadcastEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
		
		broadcastEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
		broadcastEvent.setCurrComponentName(ta.getCurrComponentName());
		broadcastEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
		
		broadcastEvent.getEventInfo().put("instrResponse", ir);
		broadcastEvent.setID("abortBroadcastEvent");
		ta.setCurrCSMEvent(broadcastEvent);

       logger.debug("\n AborBroadcastEvent");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
