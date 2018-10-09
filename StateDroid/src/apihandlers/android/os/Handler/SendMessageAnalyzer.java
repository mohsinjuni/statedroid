package apihandlers.android.os.Handler;

import java.util.ArrayList;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.MethodSignature;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import taintanalyzer.instranalyzers.MethodHandler;
import configuration.Config;

public class SendMessageAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SendMessageAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(SendMessageAnalyzer.class);
	}

	/*
	 *    // 0x142 invoke-virtual v1, v12, Landroid/os/Handler;->sendMessage(Landroid/os/Message;)Z
	 *    
	 *    In this case, v1 is actually a child class here. So, we will get its type and then check for sendMessage() method.

	 * 
	 * (non-Javadoc)
	 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
	 */

	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		
		Register callerAPIReg = involvedRegisters.get(0);
		Register inputReg = involvedRegisters.get(1);
		
		SymbolTableEntry callerAPIEntry = this.localSymSpace.find(callerAPIReg.getName());
		SymbolTableEntry inputEntry = this.localSymSpace.find(inputReg.getName());
		
		String instrTxt = ir.getInstr().getText();
		String instrSplitWithArrow[] = instrTxt.split("->");
		String arrowRightSideSplit[] = instrSplitWithArrow[1].split("[(]");

	
		MethodHandler mHandler = new MethodHandler(ta);
		
   	    APK apk = ta.getApk();
		
		
		if(callerAPIEntry != null)
		{
	  	    MethodSignature newMS = mHandler.getMethodSignatureFromCurrInstruction(ir);
	   	    newMS.setName("handleMessage");
	   	    newMS.setPkgClsName(callerAPIEntry.getEntryDetails().getType());
	   	    newMS.setReturnType("V");
		   
		   CFG newCFG = apk.findMethodBySignature(newMS);
		   
		   if(newCFG == null){
			   newMS.setReturnType("Z");
			   newCFG = apk.findMethodBySignature(newMS);
		   }
		   if(newCFG != null)
		   {
					   
			   logger.debug("cfg key -> " + newCFG.getKey());
			   logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + newMS.getParams().size());
			   logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: " + newCFG.getSignature().getParams().size());

 			   boolean result = mHandler.handleMethodCall(newCFG);
 			   
 			   newCFG.nullifyBBOutSets();
	 			  
 			   if(result)
 			   {
	 			   Object obj = ta.getInstrReturnedObject();
	 			   
	 			   if(null != obj)
	 			   {
 	 				   SymbolTableEntry entry = null;
 	 				   if(obj instanceof SymbolTableEntry){
 	 				    entry = (SymbolTableEntry) obj;
 	 				   }else if( obj instanceof InstructionReturnValue){
 	 					   InstructionReturnValue returnObj = (InstructionReturnValue) obj;
 	 					   entry = returnObj.getReturnEntry();
 	 				   }
	 				   
	 			       logger.debug("\n InvokeTaintAnalyzer");
	 			       localSymSpace.logInfoSymbolSpace();

	 			       logger.debug("\n </end> Global Entry");
	 			       Config.getInstance().getGlobalSymbolSpace().logInfoSymbolSpace();
	 			       
	 			       return entry;
	// 				   ta.getLocalSymSpace().addEntry(entry);
	 			   }
 			   }
			}
		}
  	   
		
     	logger.debug("\n Bundle.getString()");
//	       localSymSpace.printSymbolSpace();
	    return null;
	}
}
