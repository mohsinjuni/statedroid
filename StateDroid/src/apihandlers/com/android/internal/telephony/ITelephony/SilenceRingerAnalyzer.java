package apihandlers.com.android.internal.telephony.ITelephony;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.csm.GetExtrasBundleEvent;
import patternMatcher.events.csm.intent.IntentDefinedEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import patternMatcher.events.csm.reflection.ITelephonyEndCallEvent;
import patternMatcher.events.csm.reflection.ITelephonySilenceRingerEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;


public class SilenceRingerAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SilenceRingerAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(SilenceRingerAnalyzer.class);
	}

/*
 * 
		0x258 check-cast v13, Lcom/android/internal/telephony/ITelephony;
		0x25c invoke-interface v13, Lcom/android/internal/telephony/ITelephony;->endCall()Z
		
		-----------
		
		0x6 iget-object v1, v2, Lcom/google/android/v54new/service/CallService;->iTelephony Lcom/android/internal/telephony/ITelephony;
		0xa invoke-interface v1, Lcom/android/internal/telephony/ITelephony;->endCall()Z
 * 			
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());
		
		SymbolTableEntry destEntry = null;
		
		if(callerApiEntry != null)
		{
			
			destEntry = new SymbolTableEntry(); 
			   
     	    destEntry.setLineNumber(ir.getLineNumber());
    	    destEntry.setName(callerApiReg.getName());
    	    
    	    destEntry.getEntryDetails().setType(ir.getReturnType());
 	    	destEntry.getEntryDetails().setValue(callerApiEntry.getEntryDetails().getValue()); 
    	        	    
    	    destEntry.setInstrInfo(ir.getInstr().getText());
    	    
			EventFactory.getInstance().registerEvent("iTelephonySilenceRingerEvent", new ITelephonySilenceRingerEvent());
			
			Event event = EventFactory.getInstance().createEvent("iTelephonySilenceRingerEvent");
			
			event.setName("iTelephonySilenceRingerEvent");
			event.setCurrMethodName(instr.getCurrMethodName());
			event.setCurrPkgClsName(instr.getCurrPkgClassName());
			
			event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
			event.setCurrComponentName(ta.getCurrComponentName());
			event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
						
			event.getEventInfo().put("callerAPIEntry", callerApiEntry);
			event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
			
			ta.setCurrCSMEvent(event);
			
			return destEntry;

		}
 
	   return null;
	}
}
