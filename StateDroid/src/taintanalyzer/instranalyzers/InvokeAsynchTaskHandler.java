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
import models.cfg.InstructionReturnValue;
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

public class InvokeAsynchTaskHandler {

	private APK apk;
	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	private ContextStack contxtStack;
	private Context currContext;
	private TaintAnalyzer ta;
	private ArrayList<SymbolTableEntry> params;
	private ArrayList<SymbolTableEntry> paramEntries;
	private boolean isMethodCallWithinCurrentClass;
	private MultiValueMap funcKeySignatureMap;
	private SymbolTableEntry taskEntry = null;
	private SymbolTableEntry inputEntry = null;
	private boolean isContextSaved = false;

	public static Logger logger;

	public InvokeAsynchTaskHandler(APK appPkg, InstructionResponse currIR, SymbolSpace symbolSpace, ContextStack contextStack) {
		this.apk = appPkg;
		this.ir = currIR;
		this.localSymSpace = symbolSpace;
		this.contxtStack = contextStack;
		this.setFuncKeySignatureMap(Config.getInstance().getFuncKeySignatureMap());
	}

	public InvokeAsynchTaskHandler(TaintAnalyzer ta) {
		this.apk = ta.getApk();
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		this.contxtStack = ta.getContextStack();
		this.ta = ta;
		this.setFuncKeySignatureMap(Config.getInstance().getFuncKeySignatureMap());

		logger = Logger.getLogger(InvokeAsynchTaskHandler.class);

	}

	public boolean handleComponentCall(ClassObj cls) {
		currContext = new Context();

		if (cls != null) {
			this.localSymSpace.logInfoSymbolSpace();
			String currClsKey = cls.getKey();

			cls.setAnalyzedAtLeaseOnce(true);
			logger.debug("currComponent KEY " + currClsKey);
			boolean isRecursiveCall = false;

			Stack funcCallStack = Config.getInstance().getFuncCallStack();

			// we will call all methods of Asynch Task on our own, instead of taking them from iterator. Iterator will add
			// <init>, init method and will reset the original caller API also which we don't want.

			ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
			Register callerReg = involvedRegisters.get(0);
			taskEntry = localSymSpace.find(callerReg.getName());

			Register inputReg = involvedRegisters.get(1);
			inputEntry = localSymSpace.find(inputReg.getName());

			CFG cfg = (CFG) cls.findCFGByKey("onPreExecute");

			if (cfg != null && (!isRecursiveCall(cfg))) {
				this.saveParamEntries(cfg);
				this.saveContext();
				this.localSymSpace.push();
				SymbolTableEntry renamedTaskEntry = this.paramEntries.get(0);
				this.localSymSpace.addEntry(renamedTaskEntry, 0);
				this.localSymSpace.push();

				callMethodAnalysis(cfg);
				this.localSymSpace.removeEntriesUptoInclusiveLevel(0);
			}

			cfg = (CFG) cls.findCFGByKeyAndParams("doInBackground", new String[] { "[Ljava/lang/Object;" });
			// In almost all the cases I have seen, there is always an array input as parameter. So paramEntries should have that entry at index =1

			if (cfg != null && (!isRecursiveCall(cfg))) {
				this.saveParamEntries(cfg);
				if (!isContextSaved) {
					this.saveContext();
				}
				this.localSymSpace.push();
				SymbolTableEntry renamedTaskEntry = this.paramEntries.get(0);
				this.localSymSpace.addEntry(renamedTaskEntry, 0);
				this.localSymSpace.push();
				SymbolTableEntry renamedInputEntry = this.paramEntries.get(1);
				this.localSymSpace.addEntry(renamedInputEntry);

				callMethodAnalysis(cfg);
				this.localSymSpace.removeEntriesUptoInclusiveLevel(0);
			}
			inputEntry = null;
			cfg = (CFG) cls.findCFGByKeyAndParams("onPostExecute", new String[] { "Ljava/lang/Object;" });

			if (cfg != null && (!isRecursiveCall(cfg))) {
				Object obj = ta.getInstrReturnedObject();
				if (obj != null) {
					if (obj instanceof SymbolTableEntry) {
						inputEntry = (SymbolTableEntry) obj;
					} else if (obj instanceof InstructionReturnValue) {
						InstructionReturnValue returnObj = (InstructionReturnValue) obj;
						inputEntry = returnObj.getReturnEntry();
					}
				}
				this.saveParamEntries(cfg);
				if (!isContextSaved) {
					this.saveContext();
				}
				this.localSymSpace.push();
				SymbolTableEntry renamedTaskEntry = this.paramEntries.get(0);
				this.localSymSpace.addEntry(renamedTaskEntry, 0);
				this.localSymSpace.push();
				if (this.paramEntries.size() > 1) {
					SymbolTableEntry renamedInputEntry = this.paramEntries.get(1);
					this.localSymSpace.addEntry(renamedInputEntry);
				}
				callMethodAnalysis(cfg);
				this.localSymSpace.removeEntriesUptoInclusiveLevel(0);
			}

			cfg = (CFG) cls.findCFGByKeyAndParams("onProgressUpdate", new String[] { "[Ljava/lang/Object;" });

			if (cfg != null && (!isRecursiveCall(cfg))) {
				this.saveParamEntries(cfg);
				if (!isContextSaved) {
					this.saveContext();
				}
				this.localSymSpace.push();
				SymbolTableEntry renamedTaskEntry = this.paramEntries.get(0);
				this.localSymSpace.addEntry(renamedTaskEntry, 0);
				this.localSymSpace.push();

				callMethodAnalysis(cfg);
			}

			this.localSymSpace.removeEntriesUptoInclusiveLevel(0);
			this.getContext();

		}
		return true;
	}

	private void callMethodAnalysis(CFG cfg) {
		String cfgKey = cfg.getKey();
		Stack funcCallStack = Config.getInstance().getFuncCallStack();
		this.funcKeySignatureMap = Config.getInstance().getFuncKeySignatureMap();
		this.funcKeySignatureMap.put(cfgKey, cfg.getSignature());
		funcCallStack.add(cfg.getSignature());
		Config.getInstance().setFuncKeySignatureMap(funcKeySignatureMap);
		Config.getInstance().setFuncCallStack(funcCallStack);

		ta.setFirstBB(true);
		ta.analyze(cfg);
		cfg.nullifyBBOutSets();

		funcCallStack = Config.getInstance().getFuncCallStack();
		funcKeySignatureMap = Config.getInstance().getFuncKeySignatureMap();

		if (funcCallStack.size() > 0) {
			funcCallStack.pop();
			Config.getInstance().setFuncCallStack(funcCallStack);
		}
		this.funcKeySignatureMap.remove(cfgKey, cfg.getSignature());
		Config.getInstance().setFuncKeySignatureMap(funcKeySignatureMap);
	}

	private boolean isRecursiveCall(CFG cfg) {
		boolean isRecursive = false;

		this.funcKeySignatureMap = Config.getInstance().getFuncKeySignatureMap();
		String currCFGKey = cfg.getKey();
		if (funcKeySignatureMap.containsKey(currCFGKey)) {
			Iterator itr = funcKeySignatureMap.iterator(currCFGKey);

			while (itr.hasNext()) {
				MethodSignature ms = (MethodSignature) itr.next();
				if (ms.equals(cfg.getSignature())) {
					logger.warn(" ***** recursive call  " + cfg.getKey() + ", paramsCount " + ms.getParams().size());
					isRecursive = true;
					break;
				}
			}
		}
		return isRecursive;
	}

	public void printFuncCallStack() {
		Set<String> keySet = funcKeySignatureMap.keySet(); //keys();
		for (String key : keySet) {
			Iterator itr = funcKeySignatureMap.iterator(key);
			while (itr.hasNext()) {
				MethodSignature ms = (MethodSignature) itr.next();
				logger.warn(" ^^^^^^^  " + key + ", paramsCount " + ms.getParams().size());

			}

		}
	}

	public void logCurrInstr() {
		Set<String> keySet = funcKeySignatureMap.keySet(); //keys();
		for (String key : keySet) {
			Iterator itr = funcKeySignatureMap.iterator(key);
			while (itr.hasNext()) {
				MethodSignature ms = (MethodSignature) itr.next();
				logger.fatal(" ^^^^^^^  " + ms.getPkgClsName() + key + ", paramsCount " + ms.getParams().size());

			}

		}
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

	public void saveContext() {

		int level = 1;
		Stack symTabStack = localSymSpace.getEntries();

		logger.debug("[saveContext()] + stack size>> " + symTabStack.size() + ", up to levelValue  ," + level);

		level = 0;

		for (int i = symTabStack.size() - 1; i >= level; i--) {
			Hashtable ht = (Hashtable) localSymSpace.pop();
			currContext.addItem(ht);
		}
		isContextSaved = true;
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
		int maxRegCount = cfg.getSignature().getMaxRegNo();
		int totalParamCount = cfg.getSignature().getParams().size();

		paramEntries = new ArrayList<SymbolTableEntry>();

		if (taskEntry != null) {
			SymbolTableEntry newEntry = (SymbolTableEntry) taskEntry.clone();
			String newName = String.valueOf("v").concat(String.valueOf(maxRegCount - totalParamCount));

			newEntry.setName(newName);
			paramEntries.add(newEntry);
		}
		String cfgKey = cfg.getKey();
		if (cfgKey.contains("doInBackground") || cfgKey.contains("onPostExecute")) {
			if (inputEntry != null) {
				SymbolTableEntry newEntry = null;
				newEntry = new SymbolTableEntry(inputEntry); //deep copy

				String newName = String.valueOf("v").concat(String.valueOf(maxRegCount));
				newEntry.setName(newName);

				maxRegCount--;
				paramEntries.add(newEntry);
			}
		}
	}

	public MultiValueMap getFuncKeySignatureMap() {
		return funcKeySignatureMap;
	}

	public void setFuncKeySignatureMap(MultiValueMap funcKeySignatureMap) {
		this.funcKeySignatureMap = funcKeySignatureMap;
	}

}
