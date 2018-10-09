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
import patternMatcher.events.csm.reflection.ITelephonyAnswerRingingCallEvent;
import patternMatcher.events.csm.reflection.ITelephonyEndCallEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import patternMatcher.events.csm.reflection.ShowCallScreenWithDialpadEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;


public class ShowCallScreenWithDialpadAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ShowCallScreenWithDialpadAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(ShowCallScreenWithDialpadAnalyzer.class);
	}

/*
 * 
		0x20 invoke-interface v1, v2, Lcom/android/internal/telephony/ITelephony;->showCallScreenWithDialpad(Z)Z
 * 			
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction(){

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		Register inputParamReg = involvedRegisters.get(1);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());
		SymbolTableEntry inputParamEntry = this.localSymSpace.find(inputParamReg.getName());
		
		SymbolTableEntry destEntry = null;
		
		if(callerApiEntry != null){
			if(inputParamEntry != null){
				Hashtable recordFieldList = callerApiEntry.getEntryDetails().getRecordFieldList();
				if(recordFieldList == null){
					recordFieldList = new Hashtable();
				}
	    	    recordFieldList.put("inputParamEntry", inputParamEntry);
	    	    callerApiEntry.getEntryDetails().setRecordFieldList(recordFieldList);
	    	    
				EventFactory.getInstance().registerEvent("showCallScreenWithDialpadEvent", new ShowCallScreenWithDialpadEvent());
				Event event = EventFactory.getInstance().createEvent("showCallScreenWithDialpadEvent");
				
				event.setName("showCallScreenWithDialpadEvent");
				event.setCurrMethodName(instr.getCurrMethodName());
				event.setCurrPkgClsName(instr.getCurrPkgClassName());
				event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				event.setCurrComponentName(ta.getCurrComponentName());
				event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
							
				event.getEventInfo().put("callerAPIEntry", callerApiEntry);
	    	    event.getEventInfo().put("inputParamEntry", inputParamEntry);
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				
				ta.setCurrCSMEvent(event);
			}
		}
	   return null;
	}
}
