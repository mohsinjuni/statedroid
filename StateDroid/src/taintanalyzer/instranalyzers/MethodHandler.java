package taintanalyzer.instranalyzers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.cfg.Parameter;
import models.cfg.Register;
import models.symboltable.Context;
import models.symboltable.ContextStack;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class MethodHandler {

	private APK apk;
	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	private ContextStack contxtStack;
	private Context currContext;
	private TaintAnalyzer ta;
	private ArrayList<SymbolTableEntry> params;
	private Hashtable paramEntries;
	private boolean isMethodCallWithinCurrentClass;
	private MultiValueMap funcKeySignatureMap;
	private Register callerAPIReg = null;

	public static Logger logger;

	public MethodHandler(APK appPkg, InstructionResponse currIR, SymbolSpace symbolSpace, ContextStack contextStack) {
		this.apk = appPkg;
		this.ir = currIR;
		this.localSymSpace = symbolSpace;
		this.contxtStack = contextStack;
	}

	public MethodHandler(TaintAnalyzer ta) {
		this.apk = ta.getApk();
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		this.contxtStack = ta.getContextStack();
		this.ta = ta;
		this.setFuncKeySignatureMap(Config.getInstance().getFuncKeySignatureMap());

		logger = Logger.getLogger(MethodHandler.class);

	}

	public boolean handleMethodCall(CFG cfg, Register parameterReg) {
		this.callerAPIReg = parameterReg;
		return handleMethodCall(cfg);
	}

	public boolean handleMethodCall(CFG cfg) {
		currContext = new Context();
		if (cfg != null) {

			String currCFGKey = cfg.getKey();
			this.localSymSpace.logInfoSymbolSpace();
			logger.debug("currCFGKEY " + currCFGKey);
			boolean isRecursiveCall = false;
			Stack functionCallStack = Config.getInstance().getFuncCallStack();

			if (funcKeySignatureMap.containsKey(currCFGKey)) {
				Iterator itr = funcKeySignatureMap.iterator(currCFGKey);

				while (itr.hasNext()) {
					MethodSignature ms = (MethodSignature) itr.next();
					if (ms.equals(cfg.getSignature())) {
						logger.warn(" ***** recursive call  " + cfg.getKey() + ", paramsCount " + ms.getParams().size());
						isRecursiveCall = true;
						break;
					}
				}
			}
			if (isRecursiveCall) {
				// Ignore recursive method calls
				isRecursiveCall = false;
				return false;
			} else {
				MethodSignature currCFGMS = cfg.getSignature();

				// Overloaded functions are replaced with each other.
				this.funcKeySignatureMap.put(cfg.getKey(), currCFGMS);
				Config.getInstance().setFuncKeySignatureMap(funcKeySignatureMap);

				functionCallStack.add(currCFGMS);
				Config.getInstance().setFuncCallStack(functionCallStack);

				String allParams = "";

				for (Parameter p : currCFGMS.getParams()) {
					if (p != null) {
						allParams += p.getType() + ", ";
					}
				}
				logger.error(" +++++ " + cfg.getCurrPkgClassName() + "; " + cfg.getKey() + ", params= " + allParams);
				printFuncCallStack();

				this.saveContext(cfg);

				this.prepareNewMethodCall();
				
				ta.setFirstBB(true);
				ta.analyze(cfg);
				
				this.getContext();

				allParams = "";
				for (Parameter p : currCFGMS.getParams()) {
					if (p != null) {
						allParams += p.getType() + ", ";
					}
				}
				functionCallStack = Config.getInstance().getFuncCallStack();
				funcKeySignatureMap = Config.getInstance().getFuncKeySignatureMap();
				logger.error(" ----- " + cfg.getCurrPkgClassName() + "; " + cfg.getKey() + ", params= " + allParams);

				Object obj = this.funcKeySignatureMap.remove(cfg.getKey(), currCFGMS);
				if (functionCallStack.size() > 0)
					functionCallStack.pop();
				Config.getInstance().setFuncCallStack(functionCallStack);
				Config.getInstance().setFuncKeySignatureMap(this.funcKeySignatureMap);
			}
		}
		return true;
	}

	public void printFuncCallStack() {
		Set<String> keySet = funcKeySignatureMap.keySet(); // keys();
		for (String key : keySet) {
			Iterator itr = funcKeySignatureMap.iterator(key);
			while (itr.hasNext()) {
				MethodSignature ms = (MethodSignature) itr.next();
				String allParams = "";

				for (Parameter p : ms.getParams()) {
					if (p != null) {
						allParams += p.getType() + ", ";
					}
				}
				logger.error(" ^^^^^^^  " + key + ", params= " + allParams);

			}

		}
	}

	public void logCurrInstr() {
		// logger.warn("Insr which intiated the function call >>>> " +
		// ir.getInstr().getText());
		// logger.warn("caller Function >>>> " +
		// ir.getInstr().getCurrPkgClassName() +
		// ir.getInstr().getCurrMethodName());

		Set<String> keySet = funcKeySignatureMap.keySet(); // keys();
		for (String key : keySet) {
			Iterator itr = funcKeySignatureMap.iterator(key);
			while (itr.hasNext()) {
				MethodSignature ms = (MethodSignature) itr.next();
				logger.fatal(" ^^^^^^^  " + ms.getPkgClsName() + "; " + key + ", paramsCount " + ms.getParams().size());

			}

		}

	}

	public void handleUserDefinedMethod() {

	}

	public MethodSignature getMethodSignatureFromCurrInstruction(InstructionResponse tmpIR) {
		MethodSignature ms = new MethodSignature();

		ArrayList<Parameter> params = new ArrayList<Parameter>();

		String splitByArrow[] = tmpIR.getInstr().getText().split("->");
		String leftArrowSplitBySpace[] = splitByArrow[0].split(" ");

		String pkgClsName = leftArrowSplitBySpace[leftArrowSplitBySpace.length - 1];

		String instrSplitByLeftParanthesis[] = tmpIR.getInstr().getText().split("[(]");
		String instrSplitByRightParanthesis[] = instrSplitByLeftParanthesis[1].split("[)]");

		if (null != instrSplitByRightParanthesis[0] && !instrSplitByRightParanthesis[0].isEmpty()) {
			String paramsArr[] = instrSplitByRightParanthesis[0].split(" ");

			for (int i = 0; i < paramsArr.length; i++) {
				Parameter param = new Parameter();
				param.setType(paramsArr[i]);
				params.add(param);
			}
		}
		ms.setReturnType(tmpIR.getReturnType());
		ms.setName(tmpIR.getMethodOrObjectName());
		ms.setParams(params);
		ms.setPkgClsName(pkgClsName);

		// ms.set

		return ms;
	}

	// 0xbc invoke-virtual v13, v8, v3, v9, v1,
	// Lcom/test/maliciousactivity/MainActivity;->sendsms(Ljava/lang/String;
	// Ljava/lang/String; Ljava/lang/String; Landroid/content/Context;)V
	// onBtnClicked-BB@0xc2 0xc2 0xc4[ NEXT = ] [ PREV = onBtnClicked-BB@0x0,
	// onBtnClicked-BB@0x12, onBtnClicked-BB@0x62]

	public void prepareNewMethodCall() {
		this.pushParamEntries();
	}

	public void saveContext(CFG cfg) {

		this.saveParamEntries(cfg);

		// We assume that if method call is within that activity, we will keep
		// file level variables there in the stack and
		// if a method call goes to other activity, we will also pop-out the
		// level-0 hashtable and store it as a context.

		int level = 1;
		getMethodCallScope();

		Stack symTabStack = localSymSpace.getEntries();

		if (isMethodCallWithinCurrentClass) {
			level = 1;
		} else // if(!isMethodCallWithinCurrentClass)
		{
			level = 0;
		}

		logger.debug("[saveContext()] + stack size>> " + symTabStack.size() + ", up to levelValue  ," + level);

		level = 0;

		for (int i = symTabStack.size() - 1; i >= level; i--) {
			Hashtable ht = (Hashtable) localSymSpace.pop();
			currContext.addItem(ht);
		}

		Config.getInstance().setPrevMethodContext(currContext);
		currContext.printContext();
		contxtStack.saveContext(currContext);

	}

	public void getMethodCallScope() {

		Instruction currInst = ir.getInstr();

		if (currInst.getCurrPkgClassName().equalsIgnoreCase(ir.getCallerAPIName()))
			isMethodCallWithinCurrentClass = true;
		else
			isMethodCallWithinCurrentClass = false;
	}

	public void getContext() {
		Context prevContext = contxtStack.retrieveContext();
		Stack symTabStack = localSymSpace.getEntries();

		int contextStackSize = prevContext.getStackSize();

		for (int i = contextStackSize; i > 0; i--) {
			Hashtable ht = (Hashtable) prevContext.popItem();
			localSymSpace.push(ht);
		}

	}

	public void saveParamEntries(CFG cfg) {
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		int involvedRegCount = involvedRegisters.size();

		MethodSignature cfgMS = cfg.getSignature();
		int msParamSize = cfgMS.getParams().size();
		paramEntries = new Hashtable();
		int maxRegCount = cfg.getSignature().getMaxRegNo();

		if (callerAPIReg != null) {

			//This happens which a runnable object or timerTask passes itself as a parameter so that its run method is invoked later. Additional cases will be added later.

			SymbolTableEntry entry = this.localSymSpace.find(callerAPIReg.getName());
			if (entry != null) {
				SymbolTableEntry newEntry = (SymbolTableEntry) entry.clone(); // shallow
																				// copy
				String newName = String.valueOf("v").concat(String.valueOf(maxRegCount));

				newEntry.setLineNumber(entry.getLineNumber());
				newEntry.setName(newName);
				paramEntries.put(newEntry.getName(), newEntry);
			}
			return;
		}

		HashSet<String> sixtyFourBitRegisters = Config.getInstance().getSixtyFourBitRegisters();
		logger.debug(" involvedRegCount ->" + involvedRegCount);

		//		if(involvedRegCount == msParamSize+1){
		if (involvedRegisters.size() > 0) {
			for (int i = involvedRegCount - 1; i > 0; i--) {
				Register reg = involvedRegisters.get(i);
				String regType = reg.getType();
				SymbolTableEntry entry = localSymSpace.find(reg.getName());
				if (entry != null) {
					EntryDetails entryDetails = entry.getEntryDetails();
					String type = entryDetails.getType().trim();
					SymbolTableEntry newEntry = null;

					Hashtable immutableObjects = Config.getInstance().getImmutableObjects();

					if (immutableObjects.containsKey(type)) {
						// Create a deep copy of the entry.
						newEntry = new SymbolTableEntry(entry); // deep copy
					} else {
						newEntry = (SymbolTableEntry) entry.clone(); // shallow
																		// copy
					}
					String newName = String.valueOf("v").concat(String.valueOf(maxRegCount));

					newEntry.setLineNumber(entry.getLineNumber());
					newEntry.setName(newName);

					maxRegCount--;
					paramEntries.put(newEntry.getName(), newEntry);

				} else {
					// TODO: if the register is null, create a new one and give
					// it to the callee method.

				}
			}

			Register callerReg = involvedRegisters.get(0);
			SymbolTableEntry entry = localSymSpace.find(callerReg.getName());
			if (entry != null) {
				SymbolTableEntry newEntry = (SymbolTableEntry) entry.clone(); // shallow
																				// copy
				String newName = String.valueOf("v").concat(String.valueOf(maxRegCount));

				newEntry.setLineNumber(entry.getLineNumber());
				newEntry.setName(newName);
				paramEntries.put(newEntry.getName(), newEntry);
			}

		}
	}

	public void pushParamEntries() {
		if (paramEntries != null) {
			localSymSpace.push(paramEntries);

		}
		this.localSymSpace.logInfoSymbolSpace();
	}

	public InstructionResponse getIr() {
		return ir;
	}

	public void setIr(InstructionResponse ir) {
		this.ir = ir;
	}

	public MultiValueMap getFuncKeySignatureMap() {
		return funcKeySignatureMap;
	}

	public void setFuncKeySignatureMap(MultiValueMap funcKeySignatureMap) {
		this.funcKeySignatureMap = funcKeySignatureMap;
	}

}
