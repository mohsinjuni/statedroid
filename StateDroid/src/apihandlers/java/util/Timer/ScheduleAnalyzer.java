package apihandlers.java.util.Timer;

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

public class ScheduleAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private APK apk;

	public ScheduleAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ScheduleAnalyzer.class);
		apk = ta.getApk();
		this.ta = ta;
	}

	/*
	 * 0x10 invoke-direct v1, v3, v2,
		0x4c new-instance v2, Lcom/google/android/v54new/service/CallService$2;
		0x50 invoke-direct v2, v6, Lcom/google/android/v54new/service/CallService$2;-><init>(Lcom/google/android/v54new/service/CallService;)V
		0x56 iput-object v2, v6, Lcom/google/android/v54new/service/CallService;->task Ljava/util/TimerTask;
		0x5a invoke-static v8, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I
		0x60 move-result v2
		0x62 mul-int/lit16 v0, v2, 1000
		0x66 iget-object v2, v6, Lcom/google/android/v54new/service/CallService;->timer Ljava/util/Timer;
		0x6a iget-object v3, v6, Lcom/google/android/v54new/service/CallService;->task Ljava/util/TimerTask;
		0x6e int-to-long v4, v0
		0x70 invoke-virtual v2, v3, v4, v5, Ljava/util/Timer;->schedule(Ljava/util/TimerTask; J)V

	 * 
	 * 
	 * 
	 * 
	 *  void	schedule(TimerTask task, Date time)
		void	schedule(TimerTask task, Date firstTime, long period)
		void	schedule(TimerTask task, long delay)
		void	schedule(TimerTask task, long delay, long period)
	 * 
	 * 
	 */
	// Call run() method here, in place of schedule()
	public Object analyzeInstruction() {

		logger.debug("MethodHandler in StartAnalyzer is called.");
		MethodHandler mHandler = new MethodHandler(ta);

		//Always first parameter in all constructors.
		Register taskReg = ir.getInvolvedRegisters().get(1); // v1

		SymbolTableEntry taskEntry = localSymSpace.find(taskReg.getName());
		if (taskEntry != null) // v1
		{
			APK apk = ta.getApk();
			String taskType = taskEntry.getEntryDetails().getType();
			ClassObj task = apk.findClassByKey(taskType);

			if (task != null) {
				CFG runMethod = (CFG) task.findCFGByKey("run");

				mHandler = new MethodHandler(ta);
				if (runMethod != null) {
					logger.debug("cfg key -> " + runMethod.getKey());
					task.setAnalyzedAtLeaseOnce(true);
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
