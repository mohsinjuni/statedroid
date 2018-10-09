package apihandlers.java.lang.Thread;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.ClassObj;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.MethodSignature;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import taintanalyzer.instranalyzers.MethodHandler;

public class StartAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private APK apk;

	public StartAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(StartAnalyzer.class);
		apk = ta.getApk();
		this.ta = ta;
	}

	/*
	 * 0x10 invoke-direct v1, v3, v2,
	 * Lcom/nubee/coinpirates/payment/PaymentAccessor$ConfirmRunner;-><init>
	 * (Lcom/nubee/coinpirates/payment/PaymentAccessor;
	 * Lcom/nubee/coinpirates/payment/PaymentAccessor$ConfirmRunner;)V
	 * 
	 * 0x1a invoke-direct v0, v1, v2,
	 * Ljava/lang/Thread;-><init>(Ljava/lang/Runnable; Ljava/lang/String;)V 0x28
	 * invoke-virtual v0, Ljava/lang/Thread;->start()V
	 * 
	 * This basically calls ConfirmRunner.run() method.
	 * 
	 * Assuming v1 of type 'ConfirmRunner' is obtained from line-1. <init>
	 * method needs to update variables.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see taintanalyzer.BaseTaintAnalyzer#analyzeInstruction()
	 */

	/*
	 * 0xa4 invoke-direct v0, v1, v2, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable; Ljava/lang/String;)V 
	 * 0xaa iput-object v0, v3, Lcom/km/launcher/LauncherModel;->mLoader Ljava/lang/Thread; 
	 * 0xae iget-object v0, v3, Lcom/km/launcher/LauncherModel;->mLoader Ljava/lang/Thread; 
	 * 0xb2 invoke-virtual v0, Ljava/lang/Thread;->start()V
	 * 
	 * //TODO: Problem is in-between iput and iget instructions. Verify that iget and iput object handlers don't mess up the original runnable type.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see taintanalyzer.BaseTaintAnalyzer#analyzeInstruction()
	 */
	// Call run() method here, in place of start()
	public Object analyzeInstruction() {

		logger.debug("MethodHandler in StartAnalyzer is called.");
		MethodHandler mHandler = new MethodHandler(ta);

		// For 0x40 invoke-virtual v0, Ljava/lang/Thread;->start()V 
		// Get v0, then its type, find this class and call its run() method. That's it.
		
		Register runnableReg = ir.getInvolvedRegisters().get(0); // v1

		SymbolTableEntry runnableEntry = localSymSpace.find(runnableReg.getName());

		if (runnableEntry != null) // v1
		{
			APK apk = ta.getApk();
			logger.debug("inside InitAnalzer of java.lang.Thread");
			String threadType = runnableEntry.getEntryDetails().getType();
			ClassObj thread = apk.findClassByKey(threadType);

			if (thread != null) {
				CFG runMethod = (CFG) thread.findCFGByKey("run");

				mHandler = new MethodHandler(ta);
				if (runMethod != null) {
					logger.debug("cfg key -> " + runMethod.getKey());
					thread.setAnalyzedAtLeaseOnce(true);
					boolean result = mHandler.handleMethodCall(runMethod);
					runMethod.nullifyBBOutSets();
				}
			}
		}else{
			// it the caller entry is null, may be, check if run method is
			// defined in current class. The control should never come here ideally.

			String currPkgClssName = instr.getCurrPkgClassName();
			if (currPkgClssName != null && !currPkgClssName.isEmpty()) {
				ClassObj cls = apk.findClassByKey(currPkgClssName);
				if (cls != null) {
					CFG cfg = (CFG) cls.findCFGByKey("run");

					if (cfg != null) {
						logger.debug("cfg key -> " + cfg.getKey());
						cls.setAnalyzedAtLeaseOnce(true);

						boolean result = mHandler.handleMethodCall(cfg);
						if (result) {
							Object obj = ta.getInstrReturnedObject();
							if (null != obj) {
		 	 				   SymbolTableEntry entry = null;
		 	 				   if(obj instanceof SymbolTableEntry){
		 	 				    entry = (SymbolTableEntry) obj;
		 	 				   }else if( obj instanceof InstructionReturnValue){
		 	 					   InstructionReturnValue returnObj = (InstructionReturnValue) obj;
		 	 					   entry = returnObj.getReturnEntry();
		 	 				   }
								logger.debug("\n StartAnalyzer");
								localSymSpace.logInfoSymbolSpace();

								return entry;
							}
						}
						return null;
					}
				}
			}
		}
		this.localSymSpace.logInfoSymbolSpace();
		return null;

	}
}
