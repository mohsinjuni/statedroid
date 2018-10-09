package apihandlers.java.lang.Thread;

import java.util.ArrayList;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.ClassObj;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import taintanalyzer.instranalyzers.MethodHandler;
import configuration.Config;

public class InitAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta) {
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(InitAnalyzer.class);
		this.ta = ta;
	}

	/*
	 * 0x10 invoke-direct v1, v3, v2,
	 * Lcom/nubee/coinpirates/payment/PaymentAccessor$ConfirmRunner;-><init>
	 * (Lcom/nubee/coinpirates/payment/PaymentAccessor;
	 * Lcom/nubee/coinpirates/payment/PaymentAccessor$ConfirmRunner;)V
	 * 
	 * 0x1a invoke-direct v0, v1, v2,
	 * Ljava/lang/Thread;-><init>(Ljava/lang/Runnable; Ljava/lang/String;)V
	 * 
	 * 
	 * 0x28 invoke-virtual v0, Ljava/lang/Thread;->start()V
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
	public Object analyzeInstruction() {

		int regCount = ir.getInvolvedRegisters().size();
		String instrText = ir.getInstr().getText();
		// Regcount can be 1 or 2 or 3, depending upon type of initialization.
		// Ignoring if tainted variable can be passed as input variable.

		Register reg1 = ir.getInvolvedRegisters().get(0); // v0
		SymbolTableEntry reg1Entry = localSymSpace.find(reg1.getName());

		if(reg1Entry != null){
			if (regCount == 1) {
			} else if (regCount > 1) {
				// 0x98 invoke-direct v0, v1, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V

				// In all other cases, runnable can be any argument and is always followed by Thread.start(). Let Thread.startAnalyzer handle it.

				//				0x8 invoke-direct v1, v2, Lcom/mopub/mobileads/AdViewController$2;-><init>(Lcom/mopub/mobileads/AdViewController;)V
				//				0xe invoke-direct v0, v1, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V
				//				0x14 invoke-virtual v0, Ljava/lang/Thread;->start()V

				//Run a loop to find that Runnable argument. Get its type and set that type to caller/receiver object.

				// 0x1a invoke-direct v0, v1, v2, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable; Ljava/lang/String;)V
				// 0x3e invoke-direct v2, v3, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V
				ArrayList<Register> regs = ir.getInvolvedRegisters();
				Register runnableReg = null;

				for(int i=1; i < regs.size(); i++){
					Register reg = regs.get(i);
					if(reg.getType().equalsIgnoreCase("Ljava/lang/Runnable;")){
						runnableReg = reg;
						break;
					}
				}
				if(runnableReg != null){
					SymbolTableEntry runnableEntry = localSymSpace.find(runnableReg.getName());
					if (runnableEntry != null) // v1
					{
						//				0xe invoke-direct v0, v1, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable;)V
						//				0x14 invoke-virtual v0, Ljava/lang/Thread;->start()V
						// We need to make runnable as a thread entry, so that when its start method is called, all initialization parameters are
						// transferred to Thread class as well.
						//So, we just make a shallow-copy of Runnable for Thread entry... that is, update v0 to have same values of everything from v1, 
						// except that v0 would have reference of different object in memory than that of v1. Shallow-copy will make v0, v1 point to
						// same object in memory which we don't want in this case.


						reg1Entry.copyEverythingFromInputEntry(runnableEntry);
						String type = runnableEntry.getEntryDetails().getType();
						//						reg1Entry.getEntryDetails().setType(type);

						//StartAnalayzer will use this type to analyze its run() method.
					}
				}

			}
		}

		logger.debug("\n Thread.InitAnalyzer ");
		// localSymSpace.printSymbolSpace();
		return null;
	}
}
