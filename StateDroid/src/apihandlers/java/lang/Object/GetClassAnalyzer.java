package apihandlers.java.lang.Object;

import java.lang.reflect.Method;
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
import patternMatcher.events.csm.reflection.ObjectGetClassEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;


public class GetClassAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetClassAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetClassAnalyzer.class);
	}

/*
 * 
 	Case#1 telephonyManager.getClass.<something>
 		0x4 invoke-virtual v8, v6, Ledu/uta/togglesettings/MainActivity;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;
		0xa move-result-object v3
		0xc check-cast v3, Landroid/net/ConnectivityManager;
		0x10 invoke-virtual v3, Ljava/lang/Object;->getClass()Ljava/lang/Class;
		0x16 move-result-object v2
		0x28 const-string v6, 'setMobileDataEnabled'
		0x2c invoke-virtual v2, v6, v0, Ljava/lang/Class;->getMethod(Ljava/lang/String; [Ljava/lang/Class;)Ljava/lang/reflect/Method;
		0x32 move-result-object v5
		
	DIFFERENT CASE: telephonyManager.class.<something>  {fyi:not handled here}
	
		0xdc invoke-virtual v0, v15, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;
		0xe2 move-result-object v14
		0xe4 check-cast v14, Landroid/telephony/TelephonyManager;
		0xe8 const-class v15, Landroid/telephony/TelephonyManager;
		0xec const-string v16, 'getITelephony'
		0xf4 invoke-virtual/range v15 ... v17, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String; [Ljava/lang/Class;)Ljava/lang/reflect/Method;
		0xfa move-result-object v5
 * 			
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());
		SymbolTableEntry destEntry = null;
		
		if(callerApiEntry != null){
			
			destEntry = new SymbolTableEntry(callerApiEntry); // made deep copy as it was required in one example.
		   
     	    destEntry.setLineNumber(ir.getLineNumber());
    	    destEntry.setName(callerApiReg.getName());
    	    // type = Landroid/telephony/TelephonyManager; , "Ljava/lang/Class;" is a parent class.  ===> Special case.
    	    
    	    destEntry.getEntryDetails().setType(callerApiEntry.getEntryDetails().getType());
    	    destEntry.getEntryDetails().setValue(callerApiEntry.getEntryDetails().getType());
    	    destEntry.setInstrInfo(ir.getInstr().getText());
    	    destEntry.getEntryDetails().setState(callerApiEntry.getEntryDetails().getState());

//			TODO: Voilating state pattern by not sending the event and setting object' state accordingly. For now, we just don't touch the object
    	    // and let it keep its original state as it is.

			return destEntry;
 
		}
		else
		{
			destEntry = new SymbolTableEntry();
			destEntry.setName(callerApiReg.getName());
			destEntry.getEntryDetails().setType("Ljava/lang/Object;");
			localSymSpace.addEntry(destEntry);
		}
	   return null;
	}
}
