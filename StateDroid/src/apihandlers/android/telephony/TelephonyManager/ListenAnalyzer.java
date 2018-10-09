package apihandlers.android.telephony.TelephonyManager;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.ClassObj;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import taintanalyzer.instranalyzers.MethodHandler;
import configuration.Config;

public class ListenAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private APK apk;

	public ListenAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ListenAnalyzer.class);
		apk = ta.getApk();
		this.ta = ta;
	}

//		0x42 const/16 v3, 32
//		0x46 invoke-virtual v1, v2, v3, Landroid/telephony/TelephonyManager;->listen(Landroid/telephony/PhoneStateListener; I)V
	public Object analyzeInstruction() {
		logger.debug("MethodHandler in StartAnalyzer is called.");
		OnCallMethodHandler mHandler = new OnCallMethodHandler(ta);
		
		Register reg0 = ir.getInvolvedRegisters().get(0); // v1
		Register reg1 = ir.getInvolvedRegisters().get(1); // v2
		Register reg2 = ir.getInvolvedRegisters().get(2); // v3

		SymbolTableEntry callerEntry = localSymSpace.find(reg0.getName());
		SymbolTableEntry param1Entry = localSymSpace.find(reg1.getName());
		SymbolTableEntry param2Entry = localSymSpace.find(reg2.getName());
		
		String param2Value = param2Entry.getEntryDetails().getValue();

		if (callerEntry != null && param1Entry != null && !param2Value.equalsIgnoreCase("0")){
			String param1Type = param1Entry.getEntryDetails().getType();
			
			APK apk = ta.getApk();
			ClassObj listener = apk.findClassByKey(param1Type);

			if (listener != null) {
				CFG method = (CFG) listener.findCFGByKey("onCallStateChanged");

				mHandler = new OnCallMethodHandler(ta);
				if (method != null) {
					logger.debug("cfg key -> " + method.getKey());
					listener.setAnalyzedAtLeaseOnce(true);
					boolean result = mHandler.handleMethodCall(method);
					method.nullifyBBOutSets();
				}
			}
		}
		this.localSymSpace.logInfoSymbolSpace();
		return null;

	}
}
