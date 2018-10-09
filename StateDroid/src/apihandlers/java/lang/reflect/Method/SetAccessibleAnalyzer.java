package apihandlers.java.lang.reflect.Method;

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
import patternMatcher.events.csm.GetExtrasBundleEvent;
import patternMatcher.events.csm.intent.IntentDefinedEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import patternMatcher.events.csm.reflection.MethodSetAccessibleEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;


public class SetAccessibleAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SetAccessibleAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(SetAccessibleAnalyzer.class);
	}

	// 		0xf8 invoke-virtual v5, v12, Ljava/lang/reflect/Method;->setAccessible(Z)V
	
	public Object analyzeInstruction()
	{
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		Register param1Reg = involvedRegisters.get(1);

		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());
		SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
		
		SymbolTableEntry destEntry = null;
		
		if(callerApiEntry != null)
		{
			if(param1Entry != null){
				SymbolTableEntry field = new SymbolTableEntry(param1Entry);
				field.setName(param1Reg.getName());
				
				Hashtable recordFieldList = callerApiEntry.getEntryDetails().getRecordFieldList();
				if(recordFieldList == null){
					recordFieldList = new Hashtable();
				}
				recordFieldList.put(field.getName(), field);
				callerApiEntry.getEntryDetails().setRecordFieldList(recordFieldList);
				
			}
			EventFactory.getInstance().registerEvent("methodSetAccessibleEvent", new MethodSetAccessibleEvent());
			
			Event methodSetAccessibleEvent = EventFactory.getInstance().createEvent("methodSetAccessibleEvent");
			methodSetAccessibleEvent.setName("methodSetAccessibleEvent");
			methodSetAccessibleEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
			
			ta.setCurrCSMEvent(methodSetAccessibleEvent);
		}
 
	   return null;
	}
}
