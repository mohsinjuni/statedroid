package taintanalyzer.instranalyzers;

import handler.InvokeStaticHandler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.statemachines.csm.uri.states.SensitiveUriContentProviderDefinedState;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.Constants;

public class SgetObjectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	boolean tainted = false;
	String[] used;
	String changed;
	SymbolTableEntry destEntry;
	SymbolTableEntry destGlobalEntry;
	Register destReg;
	private TaintAnalyzer ta;

	public SgetObjectTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		logger = Logger.getLogger(SgetObjectTaintAnalyzer.class);
		this.ta = ta;
	}

	// Also take care of immutable objects...String etc.

	//	0x22 sget-object v2, Lcom/geinimi/c/j;->a Ljava/lang/String;
	public Object analyzeInstruction() {

		String instrText = ir.getInstr().getText();
		SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
		SymbolSpace globalSymSpace = Config.getInstance().getGlobalSymbolSpace();

		Hashtable sensitiveDBURLs = Constants.getInstance().getSensitiveURIsURLs(); //This is correct because it checks for URIs, not 'content://...'

		destReg = ir.getInvolvedRegisters().get(0); //v2
		String srcDestType = "";

		String qualifiedAPIName = ir.getCallerAPIName().trim().concat("->").concat(ir.getMethodOrObjectName().trim());
		Properties sourceSinkAPIMap = Config.getInstance().getSourceSinkAPIMap();
		Hashtable immutableObjects = Config.getInstance().getImmutableObjects();

		String apiInfo = "";
		Instruction instr = ir.getInstr();
		apiInfo = String.valueOf(" [PkgClass] = ").concat(
				instr.getCurrPkgClassName().concat(" , [method] = ").concat(instr.getCurrMethodName()));

		//Check for source data.
		if (sourceSinkAPIMap.containsKey(qualifiedAPIName)) {

			destEntry = new SymbolTableEntry();
			destEntry.setInstrInfo(ir.getInstr().getText());
			destEntry.setLineNumber(ir.getLineNumber());
			destEntry.setName(destReg.getName());
			destEntry.getEntryDetails().setType(ir.getReturnType()); // Mostly it is String.

			destEntry.getEntryDetails().setTainted(true);
			destEntry.getEntryDetails().setValue(qualifiedAPIName);

			SourceInfo srcInfo = new SourceInfo();
			srcInfo.setSrcAPI(qualifiedAPIName);
			srcInfo.setSrcInstr(ir.getInstr().getText());
			
			ArrayList<SourceInfo> siList = destEntry.getEntryDetails().getSourceInfoList();
			if (siList == null)
				siList = new ArrayList<SourceInfo>();

			if (!siList.contains(srcInfo))
				siList.add(srcInfo);

			destEntry.getEntryDetails().setSourceInfoList(siList);

			localSymSpace.addEntry(destEntry);
		} else if (sensitiveDBURLs.containsKey(qualifiedAPIName)) {

			destEntry = new SymbolTableEntry();
			destEntry.setInstrInfo(ir.getInstr().getText());
			destEntry.setLineNumber(ir.getLineNumber());
			destEntry.setName(destReg.getName());
			destEntry.getEntryDetails().setType(ir.getReturnType()); // Mostly it is String.

			destEntry.getEntryDetails().setValue(qualifiedAPIName);

			SourceInfo srcInfo = new SourceInfo();
			srcInfo.setSrcAPI(qualifiedAPIName);
			srcInfo.setSrcInstr(ir.getInstr().getText());

			ArrayList<SourceInfo> siList = destEntry.getEntryDetails().getSourceInfoList();
			if (siList == null)
				siList = new ArrayList<SourceInfo>();

			if (!siList.contains(srcInfo))
				siList.add(srcInfo);
			destEntry.getEntryDetails().setTainted(true);
			destEntry.getEntryDetails().setSourceInfoList(siList);

			//TODO: Do some code-refactoring. Currently, it's violating separation of concerns. All such logic should be handled by state machines.
			SensitiveUriContentProviderDefinedState state = new SensitiveUriContentProviderDefinedState();
			destEntry.getEntryDetails().setState(state);

			localSymSpace.addEntry(destEntry);
		} else {
			SymbolTableEntry srcEntry = globalSymSpace.find(qualifiedAPIName);
			if (srcEntry != null) {
				String returnType = ir.getReturnType();

				String srcEntryType = srcEntry.getEntryDetails().getType();

				if (immutableObjects.containsKey(returnType)) {
					// It needs deep copy.
					destEntry = new SymbolTableEntry(srcEntry); // deep copy
					destEntry.setInstrInfo(ir.getInstr().getText());
					destEntry.setLineNumber(ir.getLineNumber());
					destEntry.setName(destReg.getName());

					destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.

					localSymSpace.addEntry(destEntry);

				} else {
					//shallow copy.
					destEntry = (SymbolTableEntry) srcEntry.clone();
					destEntry.setInstrInfo(ir.getInstr().getText());
					destEntry.setLineNumber(ir.getLineNumber());
					destEntry.setName(destReg.getName());
					destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.

					//TODO: special case for now but need a better fix.
					if (ir.getReturnType().equalsIgnoreCase("Landroid/os/Handler;") && !srcEntryType.isEmpty()) {
						destEntry.getEntryDetails().setType(srcEntryType);
					}
					localSymSpace.addEntry(destEntry);

				}
			} else // if source entry is null.
			{
				String text = "		0xcc invoke-static " + ir.getCallerAPIName() + "-><clinit>()V";

				Instruction newInstr = new Instruction();
				newInstr.setText(text);
				InvokeStaticHandler invokeHandler = new InvokeStaticHandler(newInstr, null);
				InstructionResponse newIR = invokeHandler.execute();

				logger.debug("<isntr> = " + text);
				MethodHandler mHandler = new MethodHandler(ta);
				mHandler.setIr(newIR);

				MethodSignature ms = mHandler.getMethodSignatureFromCurrInstruction(newIR);

				APK apk = ta.getApk();
				if (ms != null) {
					CFG cfg = apk.findMethodBySignature(ms);
					if (cfg != null) {
						logger.debug("cfg key -> " + cfg.getKey());
						logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + ms.getParams().size());
						logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: " + cfg.getSignature().getParams().size());

						boolean result = mHandler.handleMethodCall(cfg);

						//<clinit>() methods don't return anything, usually at least.
						//So, after invoking its method, we check again for existence of the source entry.

						srcEntry = globalSymSpace.find(qualifiedAPIName);
						if (srcEntry != null) {
							String returnType = ir.getReturnType();
							String srcEntryType = srcEntry.getEntryDetails().getType();
							if (immutableObjects.containsKey(returnType)) {
								// It needs deep copy.
								destEntry = new SymbolTableEntry(srcEntry); // deep copy
								destEntry.setInstrInfo(ir.getInstr().getText());
								destEntry.setLineNumber(ir.getLineNumber());
								destEntry.setName(destReg.getName());
								destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.

								localSymSpace.addEntry(destEntry);
							} else {
								//shallow copy.
								destEntry = (SymbolTableEntry) srcEntry.clone();
								destEntry.setInstrInfo(ir.getInstr().getText());
								destEntry.setLineNumber(ir.getLineNumber());
								destEntry.setName(destReg.getName());

								if (ir.getReturnType().equalsIgnoreCase("Landroid/os/Handler;") && !srcEntryType.isEmpty()) {
									destEntry.getEntryDetails().setType(srcEntryType);
								} else {
									destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.
								}
								localSymSpace.addEntry(destEntry);

							}

						} else {
							//if it is still null, then add a dummy entry.

							destEntry = new SymbolTableEntry();
							destEntry.setInstrInfo(ir.getInstr().getText());
							destEntry.setLineNumber(ir.getLineNumber());
							destEntry.setName(destReg.getName());
							destEntry.getEntryDetails().setType(ir.getReturnType()); // Just to make sure.

							localSymSpace.addEntry(destEntry);

						}
					}
				}
			}
		}

		logger.debug("\n SgetObjectTaintAnalyzer");
		logger.debug("\n Printing Global SymSpace");
		globalSymSpace.logInfoSymbolSpace();
		return null;
	}

}
