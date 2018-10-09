package apihandlers.java.lang.Class;

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
import patternMatcher.events.csm.reflection.ClassGetNameEvent;
import patternMatcher.events.csm.reflection.ObjectGetClassEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;


public class GetNameAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetNameAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetNameAnalyzer.class);
	}

/*
 * 
 		0xc8 check-cast v11, Landroid/telephony/TelephonyManager;
		0xcc invoke-virtual v11, Ljava/lang/Object;->getClass()Ljava/lang/Class;
		0xd2 move-result-object v12                                   //v12 has TelephonyManagerDefinedState
		0xd4 invoke-virtual v12, Ljava/lang/Class;->getName()Ljava/lang/String; //V12 will keep the same state.  
		0xda move-result-object v12
		0xdc invoke-static v12, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
		0xe2 move-result-object v1 * 			
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
			destEntry = new SymbolTableEntry(callerApiEntry); // deep copy
		   
     	    destEntry.setLineNumber(ir.getLineNumber());
//    	    destEntry.setName(callerApiReg.getName()); //It does not matter here because move instruction will set its name.
    	    // type = Ljava/lang/Class; , value = Landroid/telephony/TelephonyManager;
    	    
    	    destEntry.getEntryDetails().setType(callerApiEntry.getEntryDetails().getValue());
    	    destEntry.getEntryDetails().setValue(callerApiEntry.getEntryDetails().getValue());
    	    destEntry.setInstrInfo(ir.getInstr().getText());
       	       		   
			return destEntry;
 
		}
	   return null;
	}
}
