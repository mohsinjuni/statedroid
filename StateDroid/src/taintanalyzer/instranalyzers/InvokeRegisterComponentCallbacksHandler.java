package taintanalyzer.instranalyzers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.CFGComponent;
import models.cfg.ClassObj;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.cfg.Parameter;
import models.cfg.Register;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.Context;
import models.symboltable.ContextStack;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;
import enums.MethodTypes;

public class InvokeRegisterComponentCallbacksHandler {

	private APK apk;
	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	private ContextStack contxtStack;
	private Context currContext;
	private TaintAnalyzer ta;
	private ArrayList<SymbolTableEntry> params;
	private ArrayList<SymbolTableEntry> paramEntries;
	private boolean isMethodCallWithinCurrentClass;
	public static Logger logger;

	public InvokeRegisterComponentCallbacksHandler(APK appPkg, InstructionResponse currIR, SymbolSpace symbolSpace,
			ContextStack contextStack) {
		this.apk = appPkg;
		this.ir = currIR;
		this.localSymSpace = symbolSpace;
		this.contxtStack = contextStack;
	}

	public InvokeRegisterComponentCallbacksHandler(TaintAnalyzer ta) {
		this.apk = ta.getApk();
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		this.contxtStack = ta.getContextStack();
		this.ta = ta;
		logger = Logger.getLogger(InvokeRegisterComponentCallbacksHandler.class);

	}

	public boolean handleComponentCall(ClassObj cls) {
		currContext = new Context();

		if (cls != null) {
			this.localSymSpace.logInfoSymbolSpace();
			String currClsKey = cls.getKey();

			logger.debug("currComponent KEY " + currClsKey);
			boolean isRecursiveCall = false;

			Stack compCallStack = Config.getInstance().getFuncCallStack();

			this.saveContext();

			SymbolTableEntry callerAPIEntry = this.paramEntries.get(0);
			this.localSymSpace.push();
			this.localSymSpace.addEntry(callerAPIEntry, 0);

			CFG cfg = (CFG) cls.findCFGByKey("onActivityCreated");

			if (cfg != null) {
				this.localSymSpace.push();
				ta.setFirstBB(true);
				ta.analyze(cfg);
				cfg.nullifyBBOutSets();
			}

			cfg = (CFG) cls.findCFGByKey("onActivityStarted");
			if (cfg != null) {
				this.localSymSpace.push();
				this.addParamEntries(cfg);
				ta.setFirstBB(true);
				ta.analyze(cfg);
				cfg.nullifyBBOutSets();
			}

			cfg = (CFG) cls.findCFGByKey("onActivityResumed");
			if (cfg != null) {
				this.localSymSpace.push();
				this.addParamEntries(cfg);
				ta.setFirstBB(true);
				ta.analyze(cfg);
				cfg.nullifyBBOutSets();
			}

			cfg = (CFG) cls.findCFGByKey("onActivityPaused");
			if (cfg != null) {
				this.localSymSpace.push();
				this.addParamEntries(cfg);
				ta.setFirstBB(true);
				ta.analyze(cfg);
				cfg.nullifyBBOutSets();
			}

			cfg = (CFG) cls.findCFGByKey("onActivityStopped");
			if (cfg != null) {
				this.localSymSpace.push();
				this.addParamEntries(cfg);
				ta.setFirstBB(true);
				ta.analyze(cfg);
				cfg.nullifyBBOutSets();
			}

			cfg = (CFG) cls.findCFGByKey("onActivityDestroyed");
			if (cfg != null) {
				this.localSymSpace.push();
				this.addParamEntries(cfg);
				ta.setFirstBB(true);
				ta.analyze(cfg);
				cfg.nullifyBBOutSets();
			}

			cfg = (CFG) cls.findCFGByKey("onActivityRestarted");
			if (cfg != null) {
				this.localSymSpace.push();
				this.addParamEntries(cfg);
				ta.setFirstBB(true);
				ta.analyze(cfg);
				cfg.nullifyBBOutSets();
			}

			cfg = (CFG) cls.findCFGByKey("onLowMemory");
			if (cfg != null) {
				this.localSymSpace.push();
				this.addParamEntries(cfg);
				ta.setFirstBB(true);
				ta.analyze(cfg);
				cfg.nullifyBBOutSets();
			}

			cfg = (CFG) cls.findCFGByKey("onConfigurationChanged");
			if (cfg != null) {
				this.localSymSpace.push();
				this.addParamEntries(cfg);
				ta.setFirstBB(true);
				ta.analyze(cfg);
				cfg.nullifyBBOutSets();
			}

			cfg = (CFG) cls.findCFGByKey("onTrimMemory");
			if (cfg != null) {
				this.localSymSpace.push();
				this.addParamEntries(cfg);
				ta.setFirstBB(true);
				ta.analyze(cfg);
				cfg.nullifyBBOutSets();
			}

			this.localSymSpace.removeEntriesUptoInclusiveLevel(0);
			this.getContext();

		}
		return true;
	}

	public void handleUserDefinedMethod() {

	}

	public void addParamEntries(CFG cfg) {

		int maxRegCount = cfg.getSignature().getMaxRegNo(); // This value will be obtained from Method Signature.

		int paramCount = cfg.getSignature().getParams().size();
		int savedEntriesCount = paramEntries.size();
		int i = maxRegCount;

		for (; i > 0; i--) {
			if (paramCount > 0) {
				SymbolTableEntry entry = this.paramEntries.get(savedEntriesCount - 1);
				if (entry != null) {
					EntryDetails entryDetails = entry.getEntryDetails();
					String type = entryDetails.getType().trim();
					SymbolTableEntry newEntry = null;

					Hashtable immutableObjects = Config.getInstance().getImmutableObjects();

					if (immutableObjects.containsKey(type) || type.isEmpty()) {
						//Create a deep copy of the entry.
						newEntry = new SymbolTableEntry(entry); //deep copy
					} else {
						newEntry = (SymbolTableEntry) entry.clone(); //shallow copy
					}
					String newName = String.valueOf("v").concat(String.valueOf(i));

					newEntry.setLineNumber(entry.getLineNumber());
					newEntry.setName(newName);

					localSymSpace.addEntry(newEntry);

					paramCount--;
					savedEntriesCount--;
				}
			} else
				break;
		}

		SymbolTableEntry entry = paramEntries.get(0);
		if (entry != null) {
			SymbolTableEntry newEntry = (SymbolTableEntry) entry.clone(); //shallow copy
			String newName = String.valueOf("v").concat(String.valueOf(i));

			newEntry.setLineNumber(entry.getLineNumber());
			newEntry.setName(newName);

			localSymSpace.addEntry(newEntry);
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

	//		0xbc invoke-virtual v13, v8, v3, v9, v1, Lcom/test/maliciousactivity/MainActivity;->sendsms(Ljava/lang/String; Ljava/lang/String; Ljava/lang/String; Landroid/content/Context;)V
	//	onBtnClicked-BB@0xc2 0xc2 0xc4[ NEXT =  ] [ PREV = onBtnClicked-BB@0x0, onBtnClicked-BB@0x12, onBtnClicked-BB@0x62] 

	public void prepareNewMethodCall() {
		//		this.pushParamEntries();
	}

	public void saveContext() {

		this.saveParamEntries();

		int level = 1;
		Stack symTabStack = localSymSpace.getEntries();

		logger.debug("[saveContext()] + stack size>> " + symTabStack.size() + ", up to levelValue  ," + level);

		level = 0;

		for (int i = symTabStack.size() - 1; i >= level; i--) {
			Hashtable ht = (Hashtable) localSymSpace.pop();
			currContext.addItem(ht);
		}

		//		Context checkContxt = Config.getInstance().getPrevMethodContext();
		//		checkContxt.printContext();

		//		currContext.printContext();
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

	public void saveParamEntries() {
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();

		paramEntries = new ArrayList<SymbolTableEntry>();

		int involvedRegCount = involvedRegisters.size();
		logger.debug(" involvedRegCount ->" + involvedRegCount);
		if (involvedRegisters.size() > 0) {
			Register callerReg = involvedRegisters.get(0);
			SymbolTableEntry entry = localSymSpace.find(callerReg.getName());
			if (entry != null) {
				SymbolTableEntry newEntry = (SymbolTableEntry) entry.clone(); //shallow copy
				String callerAPI = ir.getCallerAPIName(); /////////// Setting component type.
				newEntry.setName(callerAPI);
				paramEntries.add(newEntry);
			}

			for (int i = 1; i < involvedRegCount; i++) {
				Register reg = involvedRegisters.get(i);
				entry = localSymSpace.find(reg.getName());
				if (entry != null) {
					EntryDetails entryDetails = entry.getEntryDetails();
					String type = entryDetails.getType().trim();
					SymbolTableEntry newEntry = null;

					Hashtable immutableObjects = Config.getInstance().getImmutableObjects();
					//Create a deep copy of the entry.
					newEntry = new SymbolTableEntry(entry); //deep copy
					paramEntries.add(newEntry);

				}
			}

		}

	}
}
