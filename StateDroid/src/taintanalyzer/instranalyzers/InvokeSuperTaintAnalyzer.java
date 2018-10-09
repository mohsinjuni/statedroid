package taintanalyzer.instranalyzers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Properties;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.cfg.Register;
import models.cfg.Instruction.API_TYPES;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.ApiTypesBySyntax;

public class InvokeSuperTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	boolean tainted = false;
	String[] used;
	String changed;
	private TaintAnalyzer ta;
	private Properties apiRulesMap;
	private SymbolTableEntry returnEntry;
	private APK apk;

	private Properties apiDefinedAnalyzers;

	private Object apiReturnsObj;

	public InvokeSuperTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		this.ta = ta;
		this.returnEntry = new SymbolTableEntry();
		this.apk = ta.getApk();

		logger = Logger.getLogger(InvokeSuperTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {

		// Don't do anything for this instruction.

		return null;
	}

	public Object getApiReturnsObj() {
		return apiReturnsObj;
	}

	public void setApiReturnsObj(Object apiReturnsObj) {
		this.apiReturnsObj = apiReturnsObj;
	}
}