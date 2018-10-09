package taintanalyzer.instranalyzers;

import java.util.ArrayList;
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

public class InvokeStaticMethodHandler {

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

	public static Logger logger;

	public InvokeStaticMethodHandler(APK appPkg, InstructionResponse currIR, SymbolSpace symbolSpace, ContextStack contextStack) {
		this.apk = appPkg;
		this.ir = currIR;
		this.localSymSpace = symbolSpace;
		this.contxtStack = contextStack;
	}

	public InvokeStaticMethodHandler(TaintAnalyzer ta) {
		this.apk = ta.getApk();
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		this.contxtStack = ta.getContextStack();
		this.ta = ta;
		this.setFuncKeySignatureMap(Config.getInstance().getFuncKeySignatureMap());
		logger = Logger.getLogger(InvokeStaticMethodHandler.class);

	}

	public boolean handleMethodCall(CFG cfg) {
		currContext = new Context();

		if (cfg != null) {
			String currCFGKey = cfg.getKey();

			logger.debug("currCFGKEY " + currCFGKey);
			boolean isRecursiveCall = false;

			Stack functionCallStack = Config.getInstance().getFuncCallStack();

			//			printFuncCallStack();
			if (funcKeySignatureMap.containsKey(currCFGKey)) {
				Iterator itr = funcKeySignatureMap.iterator(currCFGKey);
				while (itr.hasNext()) {
					MethodSignature ms = (MethodSignature) itr.next();
					if (ms.equals(cfg.getSignature())) {
						logger.error(" ***** recursive call  " + cfg.getKey() + ", paramsCount " + ms.getParams().size());
						isRecursiveCall = true;
						break;
					}
				}
			}
			if (isRecursiveCall) {
				isRecursiveCall = false;
				return false;
			} else {
				MethodSignature currCFGMS = cfg.getSignature();
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
				printFuncCallMap();

				this.saveContext(cfg);

				this.prepareNewMethodCall();
				// make method call

				ta.setFirstBB(true);
				ta.analyze(cfg);

				String calledMethodName = this.ir.getMethodOrObjectName();

				// Retrieve previous context.
				this.getContext();

				allParams = "";
				for (Parameter p : currCFGMS.getParams()) {
					if (p != null) {
						allParams += p.getType() + ", ";
					}
				}
				logger.error(" ----- " + cfg.getCurrPkgClassName() + "; " + cfg.getKey() + ", params= " + allParams);
				//				System.out.println(" ----- " +  cfg.getCurrPkgClassName() + "; "  +  cfg.getKey() + ", params= " + allParams );
				//				printFuncCallMap();
				functionCallStack = Config.getInstance().getFuncCallStack();
				funcKeySignatureMap = Config.getInstance().getFuncKeySignatureMap();

				//logger.warn(" ---- " + cfg.getKey() + ", paramsCount " + currCFGMS.getParams().size() );
				this.funcKeySignatureMap.remove(cfg.getKey(), currCFGMS);
				Object obj = this.funcKeySignatureMap.remove(cfg.getKey(), currCFGMS);

				if (functionCallStack.size() > 0)
					functionCallStack.pop();
				Config.getInstance().setFuncCallStack(functionCallStack);
				Config.getInstance().setFuncKeySignatureMap(this.funcKeySignatureMap);
			}

		}
		return true;
	}

	public void printFuncCallMap() {
		Set<String> keySet = funcKeySignatureMap.keySet(); //keys();
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

	public void printFuncCallStack() {
		Set<String> keySet = funcKeySignatureMap.keySet(); //keys();
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

	public void logCurrInstr(CFG cfg) {
		logger.warn("Insr which intiated the function call >>>> " + ir.getInstr().getText());
		logger.warn("caller Function >>>> " + ir.getInstr().getCurrPkgClassName() + ir.getInstr().getCurrMethodName());

		Set<String> keySet = funcKeySignatureMap.keySet(); //keys();
		for (String key : keySet) {
			Iterator itr = funcKeySignatureMap.iterator(key);
			while (itr.hasNext()) {
				MethodSignature ms = (MethodSignature) itr.next();
				logger.warn(" ^^^^^^^  " + ms.getPkgClsName() + key + ", paramsCount " + ms.getParams().size());

			}

		}

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

		//		ms.set

		return ms;
	}

	public void prepareNewMethodCall() {
		this.pushParamEntries();
	}

	public void saveContext(CFG cfg) {

		this.saveParamEntries(cfg);

		// We assume that if method call is within that activity, we will keep file level variables there in the stack and
		// if a method call goes to other activity, we will also pop-out the level-0 hashtable and store it as a context.
		String methodCallNature = null;
		int level = 1;

		Stack symTabStack = localSymSpace.getEntries();

		if (isMethodCallWithinCurrentClass) {
			level = 1;
		} else //if(!isMethodCallWithinCurrentClass)
		{
			level = 0;
		}

		level = 0;

		for (int i = symTabStack.size() - 1; i >= level; i--) {
			Hashtable ht = (Hashtable) localSymSpace.pop();
			currContext.addItem(ht);
		}

		logger.debug("[MethodHandler.saveContext()]");

		currContext.printContext();
		contxtStack.saveContext(currContext);
		//		else if // NOT within current class
		//		else if  // 

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

		// In case of no parameter," invoke-virtual v3, Lcom/test/maliciousactivity/User;->getPwdObject()",
		// v3 will be in the symbolTable only.

		paramEntries = new Hashtable();
		int maxRegCount = cfg.getSignature().getMaxRegNo(); // This value will be obtained from Method Signature.

		// for methods with no parameters, this will not be executed because caller register v3 is always at zero location.
		// Either you can define two extra members for caller and parameters or follow the above rule.

		/*
		 * 0x1a invoke-static v4, Ljava/lang/Integer;->numberOfTrailingZeros(I)I
		 * 0x20 move-result v2
		 * 
		 * 0x16e invoke-static v0,
		 * Lcom/google/ads/util/b;->b(Ljava/lang/String;)V
		 * 0x174 goto -2f
		 */
		int involvedRegCount = involvedRegisters.size();
		if (involvedRegisters.size() > 0) {
			for (int i = involvedRegCount - 1; i >= 0; i--) {
				Register reg = involvedRegisters.get(i);
				SymbolTableEntry entry = localSymSpace.find(reg.getName());
				if (entry != null) {
					EntryDetails entryDetails = entry.getEntryDetails();
					String type = entryDetails.getType().trim();
					SymbolTableEntry newEntry = null;

					if (type.equalsIgnoreCase("I") || type.equalsIgnoreCase("Z") || type.equalsIgnoreCase("D")
							|| type.equalsIgnoreCase("F") || type.equalsIgnoreCase("J") || type.equalsIgnoreCase("B")
							|| type.equalsIgnoreCase("C") || type.equalsIgnoreCase("S")

							|| type.equalsIgnoreCase("Ljava/lang/String;") || type.equalsIgnoreCase("Ljava/lang/Integer;")
							|| type.equalsIgnoreCase("Ljava/lang/Byte;") || type.equalsIgnoreCase("Ljava/lang/Character;")
							|| type.equalsIgnoreCase("Ljava/lang/Short;") || type.equalsIgnoreCase("Ljava/lang/Boolean;")
							|| type.equalsIgnoreCase("Ljava/lang/Long;") || type.equalsIgnoreCase("Ljava/lang/Double;")
							|| type.equalsIgnoreCase("Ljava/lang/Float;")) {
						//Create a deep copy of the entry.
						newEntry = new SymbolTableEntry(entry); //deep copy
					} else {
						newEntry = (SymbolTableEntry) entry.clone(); //shallow copy
					}
					String newName = String.valueOf("v").concat(String.valueOf(maxRegCount));

					newEntry.setLineNumber(entry.getLineNumber());
					newEntry.setName(newName);

					maxRegCount--;
					paramEntries.put(newEntry.getName(), newEntry);

				}
			}
		}

	}

	public void pushParamEntries() {
		// if other than <init> method, get caller object and get fields data from there and push it on the symboltable.
		if (paramEntries != null) {
			localSymSpace.push(paramEntries);
		}
	}

	public MultiValueMap getFuncKeySignatureMap() {
		return funcKeySignatureMap;
	}

	public void setFuncKeySignatureMap(MultiValueMap funcKeySignatureMap) {
		this.funcKeySignatureMap = funcKeySignatureMap;
	}

}
