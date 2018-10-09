package apihandlers.android.content.ContentResolver;

import handler.InvokeHandler;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.MethodSignature;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import taintanalyzer.instranalyzers.MethodHandler;
import configuration.Config;

public class RegisterContentObserverAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public RegisterContentObserverAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(RegisterContentObserverAnalyzer.class);
		this.ta = ta;
	}


/*
 * 		0xc8 iget-object v5, v7, Lcom/google/android/v54new/service/RegisterBroadcastService;->callObserver
 * 		 Lcom/google/android/v54new/sqlite/call/CallObserver;

	0xcc invoke-virtual v2, v4, v6, v5, Landroid/content/ContentResolver;->registerContentObserver
	(Landroid/net/Uri; Z Landroid/database/ContentObserver;)V
	
	final void	registerContentObserver(Uri uri, boolean notifyForDescendents, ContentObserver observer)
		Register an observer class that gets callbacks when data identified by a given content URI changes.
		  
 * 	(non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register cResolverReg = involvedRegisters.get(0);
		Register uriReg = involvedRegisters.get(1);
		Register booleanReg = involvedRegisters.get(2);
		Register cObserverReg = involvedRegisters.get(3);
		
		SymbolTableEntry cResolverEntry = localSymSpace.find(cResolverReg.getName());
		SymbolTableEntry uriEntry = localSymSpace.find(uriReg.getName());
		SymbolTableEntry booleanEntry = localSymSpace.find(booleanReg.getName());
		SymbolTableEntry cObserverEntry = localSymSpace.find(cObserverReg.getName());

		//We pretend that we received an instruction like given below. We use the following signature, only to find onChange() method.
		//So whenever there is a call to registerContentObserver, we invoke its onChange() method.
		
		//String text = "		0xcc invoke-virtual v0, v1, Lcom/google/android/v54new/sqlite/call/CallObserver;->onChange(Z)V";
		
		//Let's create one fake entry for type Z.
		
		SymbolTableEntry inputEntry = new SymbolTableEntry();
		inputEntry.getEntryDetails().setType("Z");
		inputEntry.setName("v250");
		this.localSymSpace.addEntry(inputEntry);
		
		String text = "		0xcc invoke-virtual " + cObserverReg.getName() + ", v250, ";
		text += cObserverEntry.getEntryDetails().getType();
		text += "->onChange(Z)V";

		Instruction newInstr = new Instruction();
		newInstr.setText(text);
		InvokeHandler invokeHandler = new InvokeHandler(newInstr, null);
		InstructionResponse newIR = invokeHandler.execute();

		logger.debug("<isntr> = " + text);
		MethodHandler mHandler = new MethodHandler(ta);
		mHandler.setIr(newIR);
		
		
   	    MethodSignature ms = mHandler.getMethodSignatureFromCurrInstruction(newIR);

   	    APK apk = ta.getApk();
   	    if(ms != null){
 		   CFG cfg = apk.findMethodBySignature(ms);
 		   
 		   if(cfg != null){
 			   logger.debug("cfg key -> " + cfg.getKey());
 			   logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + ms.getParams().size());
 			   logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: " + cfg.getSignature().getParams().size());
  			   boolean result = mHandler.handleMethodCall(cfg);
 	 			  
  			   if(result){
 	 			   Object obj = ta.getInstrReturnedObject();
 	 			   if(null != obj){
 	 				   SymbolTableEntry returnEntry = null;
 	 				   if(obj instanceof SymbolTableEntry){
 	 				    returnEntry = (SymbolTableEntry) obj;
 	 				   }else if( obj instanceof InstructionReturnValue){
 	 					   InstructionReturnValue returnObj = (InstructionReturnValue) obj;
 	 					   returnEntry = returnObj.getReturnEntry();
 	 				   }
 	 			       localSymSpace.logInfoSymbolSpace();
 	 			       return returnEntry;
 	 			   }
  			   }
 		   }
 	   }
	   return null;
	}
}
