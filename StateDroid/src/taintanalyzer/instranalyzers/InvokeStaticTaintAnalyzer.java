package taintanalyzer.instranalyzers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.MethodSignature;
import models.cfg.Register;
import models.cfg.Instruction.API_TYPES;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.attackreporter.Report;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.InformationLeakerASMEvent;
import patternMatcher.events.csm.SmsSenderEvent;

import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.ApiTypesBySyntax;

public class InvokeStaticTaintAnalyzer extends BaseTaintAnalyzer {

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

	public InvokeStaticTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		this.ta = ta;
		this.returnEntry = new SymbolTableEntry();
		this.apk = ta.getApk();

		logger = Logger.getLogger(InvokeStaticTaintAnalyzer.class);
	}

	/*
	 * 0x1a invoke-static v4, Ljava/lang/Integer;->numberOfTrailingZeros(I)I
	 * 0x20 move-result v2
	 * 
	 * 0x16e invoke-static v0, Lcom/google/ads/util/b;->b(Ljava/lang/String;)V
	 * 0x174 goto -2f
	 */

	public Object analyzeInstruction() {

		String instrText = ir.getInstr().getText();
		String qualifiedApiName = ir.getQualifiedAPIName();
		Properties sourceSinkAPIMap = Config.getInstance().getSourceSinkAPIMap();

		if (qualifiedApiName != null && sourceSinkAPIMap.containsKey(qualifiedApiName)) {
			String apiType = sourceSinkAPIMap.getProperty(qualifiedApiName).toString().trim();
			if (apiType.equalsIgnoreCase("source")) {
				ir.setSourceAPI(true);
			} else if (apiType.equalsIgnoreCase("sink")) {
				ir.setSinkAPI(true);
			}
		}

		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
		used = ir.getUsedRegisters();
		apiRulesMap = Config.getInstance().getApiRulesMap();
		String pkgClsName = ir.getCallerAPIName();
		String methdObjectName = ir.getMethodOrObjectName();
		String qualifiedAPIName = pkgClsName.concat("->").concat(methdObjectName);
		apiDefinedAnalyzers = Config.getInstance().getApiDefinedAnalyzers();
		if (apiDefinedAnalyzers.containsKey(qualifiedAPIName)) {
			String analyzer = apiDefinedAnalyzers.getProperty(qualifiedAPIName);

			String modifiedpkgClsName = pkgClsName.substring(1, pkgClsName.length() - 1);
			modifiedpkgClsName = modifiedpkgClsName.replace('/', '.').concat(".");

			String completeAnalyzerName = new StringBuilder("apihandlers.").append(modifiedpkgClsName).append(analyzer).toString();

			logger.debug("InvokeTaintAnalyzer.java -> completeAnalyzerName " + completeAnalyzerName);
			Class cls = null;
			BaseTaintAnalyzer baseTaintAnalyzer;

			try {
				cls = Class.forName(completeAnalyzerName);
				//	 						baseTaintAnalyzer = (BaseTaintAnalyzer) cls.getDeclaredConstructor(new Class[] {InstructionResponse.class, SymbolTable.class }).newInstance(ir, localSymSpace);
				baseTaintAnalyzer = (BaseTaintAnalyzer) cls.getDeclaredConstructor(new Class[] { TaintAnalyzer.class }).newInstance(ta);
				setApiReturnsObj(baseTaintAnalyzer.analyzeInstruction());

				logger.debug("\n InvokeStaticTaintAnalyzer");
				localSymSpace.logInfoSymbolSpace();

				return apiReturnsObj;

				// 						System.out.println("In "+ ins.getCurrMethodName() + ", After ==>" + ins.getText() );

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		} else if (apiRulesMap.containsKey(qualifiedAPIName)) {
			String key = apiRulesMap.getProperty(qualifiedAPIName);

			if (key.equalsIgnoreCase(API_TYPES.dumbAPI.toString())) {
				return returnEntry;
			} else if (key.equalsIgnoreCase(API_TYPES.storeVariable.toString())) {
				InvokeDirectDefaultTaintAnalyzer dfTA = new InvokeDirectDefaultTaintAnalyzer(ta);
				return (SymbolTableEntry) dfTA.analyzeInstruction();
			} else if (key.equalsIgnoreCase(API_TYPES.taintPropagater.toString())) {
				InvokeDirectDefaultTaintAnalyzer dfTA = new InvokeDirectDefaultTaintAnalyzer(ta);
				return (SymbolTableEntry) dfTA.analyzeInstruction();
			} else if (key.equalsIgnoreCase(API_TYPES.commonInitializer.toString())) {

			}

		} else if (apiRulesMap.containsKey(methdObjectName)) {
			return returnEntry;
		}
		//<init> situation is handled by else condition under this if condition.
		else if (ir.isSourceAPI()) {
			String apiInfo = "";
			Instruction instr = ir.getInstr();
			apiInfo = String.valueOf(" [SrcPkgClass] = ").concat(
					instr.getCurrPkgClassName().concat(" , [SrcMethod] = ").concat(instr.getCurrMethodName()));
			logger.error("This is a source API" + apiInfo + ", [api] = " + instr.getText());
			returnEntryDetails.setTainted(true);
			returnEntryDetails.setConstant(false);
			returnEntryDetails.setField(false);
			returnEntryDetails.setRecord(false);

			SourceInfo si = new SourceInfo();
			si.setSrcAPI(qualifiedAPIName);
			si.setSrcInstr(ir.getInstr().getText());

			// 			 	   returnEntryDetails.getSourceInfoList().add(si);
			ArrayList<SourceInfo> siList = returnEntryDetails.getSourceInfoList();
			if (siList == null)
				siList = new ArrayList<SourceInfo>();

			if (!siList.contains(si))
				siList.add(si);

			returnEntryDetails.setSourceInfoList(siList);

			returnEntryDetails.setType(ir.getReturnType());
			returnEntry.setInstrInfo(ir.getInstr().getText());
			returnEntryDetails.setValue(qualifiedAPIName);

			returnEntry.setEntryDetails(returnEntryDetails);

			return returnEntry;
		}
		// This check should be set here, instead of its handler/parser.
		else if (ir.isSinkAPI()) //key.equalsIgnoreCase(API_TYPES.sink.toString() ))
		{
			ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
			SymbolTableEntry entry = new SymbolTableEntry();
			String instrInfo = "";
			ArrayList<SourceInfo> srcAPIList = new ArrayList<SourceInfo>();

			Instruction instr = ir.getInstr();

			if (involvedRegisters != null) {
				for (int i = 0; i < involvedRegisters.size(); i++) {
					String regName = involvedRegisters.get(i).getName();
					entry = localSymSpace.find(regName);
					if (entry != null) {
						if (entry.getEntryDetails().isTainted()) {
							tainted = true;

							ArrayList<SourceInfo> entrySIList = entry.getEntryDetails().getSourceInfoList();
							if (entrySIList != null && entrySIList.size() > 0) {
								for (SourceInfo si : entrySIList) {
									if (!srcAPIList.contains(si))
										srcAPIList.add(si);
								}
							}
							// 			    		          // if any of source/used registers is tainted, destination register will get tainted.
						}
					}
				}
				if (tainted) {
					tainted = false;
					EventFactory.getInstance().registerEvent("InformationLeakageEvent", new InformationLeakerASMEvent());

					Event event = EventFactory.getInstance().createEvent("InformationLeakageEvent");

					event.setCurrMethodName(instr.getCurrMethodName());
					event.setCurrPkgClsName(instr.getCurrPkgClassName());
					event.setName("InformationLeakageEvent");

					event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
					event.setCurrComponentName(ta.getCurrComponentName());
					event.setCurrComponentPkgName(ta.getCurrComponentPkgName());

					event.getEventInfo().put("sources", srcAPIList);
					event.getEventInfo().put("instrText", instr.getText());

					ta.setCurrCSMEvent(event);

				}

				//We will put customized checks here. For example, if sendTextMessage is being called and first argument is a constant.

				String calledAPIName = ir.getCallerAPIName();
				String calledObjctMethodName = ir.getMethodOrObjectName();
				String str = calledAPIName.concat("->").concat(calledObjctMethodName);

				if (str.equalsIgnoreCase("Landroid/telephony/gsm/SmsManager;->sendTextMessage")
						|| str.equalsIgnoreCase("Landroid/telephony/SmsManager;->sendTextMessage")
						|| str.equalsIgnoreCase("Lcom/android/internal/telephony/SmsManager;->sendSms")
						|| str.equalsIgnoreCase("Landroid/telephony/SmsManager;->sendDataMessage")
						|| str.equalsIgnoreCase("Landroid/telephony/gsm/SmsManager;->sendDataMessage")
						|| str.equalsIgnoreCase("Landroid/telephony/SmsManager;->sendMultipartTextMessage")
						|| str.equalsIgnoreCase("Landroid/telephony/gsm/SmsManager;->sendMultipartTextMessage")) {

					String firstReg = involvedRegisters.get(1).getName();
					SymbolTableEntry recipientNoEntry = localSymSpace.find(firstReg);

					EventFactory.getInstance().registerEvent("SmsSenderEvent", new SmsSenderEvent());

					Event event = EventFactory.getInstance().createEvent("SmsSenderEvent");

					event.setCurrMethodName(instr.getCurrMethodName());
					event.setCurrPkgClsName(instr.getCurrPkgClassName());
					event.setName("SmsSenderEvent");

					event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
					event.setCurrComponentName(ta.getCurrComponentName());
					event.setCurrComponentPkgName(ta.getCurrComponentPkgName());

					event.getEventInfo().put("instrText", instr.getText());
					event.getEventInfo().put("recipientNoEntry", recipientNoEntry);

					ta.setCurrCSMEvent(event);

				}
			}
			logger.error(" \n\n **** Sink API => " + ir.getInstr().getText() + "\n\n");
			InvokeDirectDefaultTaintAnalyzer dfTA = new InvokeDirectDefaultTaintAnalyzer(ta);
			return (SymbolTableEntry) dfTA.analyzeInstruction();
		}

		//#Pre-defined method names that are invoked by user-defined caller APIs. i.e. a/b/c/myReceiver;->abortBroadcast()
		else if (apiDefinedAnalyzers.containsKey(methdObjectName)) {
			String analyzer = apiDefinedAnalyzers.getProperty(methdObjectName);

			String modifiedpkgClsName = pkgClsName.substring(1, pkgClsName.length() - 1);
			modifiedpkgClsName = modifiedpkgClsName.replace('/', '.').concat(".");

			String completeAnalyzerName = new StringBuilder("apihandlers.userdefined.").append(analyzer).toString();

			logger.debug("InvokeTaintAnalyzer.java -> completeAnalyzerName " + completeAnalyzerName);
			Class cls = null;
			BaseTaintAnalyzer baseTaintAnalyzer;

			try {
				cls = Class.forName(completeAnalyzerName);
				//	 						baseTaintAnalyzer = (BaseTaintAnalyzer) cls.getDeclaredConstructor(new Class[] {InstructionResponse.class, SymbolTable.class }).newInstance(ir, localSymSpace);
				baseTaintAnalyzer = (BaseTaintAnalyzer) cls.getDeclaredConstructor(new Class[] { TaintAnalyzer.class }).newInstance(ta);
				setApiReturnsObj(baseTaintAnalyzer.analyzeInstruction());

				// 						logger.debug("\n InvokeTaintAnalyzer");
				localSymSpace.logInfoSymbolSpace();

				return apiReturnsObj;

				// 						System.out.println("In "+ ins.getCurrMethodName() + ", After ==>" + ins.getText() );

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}

		} else {

			Hashtable blackListedAPIs = Config.getInstance().getBlackListedAPIs();

			Enumeration<String> keys = blackListedAPIs.keys();
			boolean isBlackListedApiCall = false;

			while (keys.hasMoreElements()) {
				String key = keys.nextElement();

				if (pkgClsName.startsWith(key)) {
					isBlackListedApiCall = true;
					break;
				}
			}
			if (isBlackListedApiCall) {
				InvokeVirtualDefaultTaintAnalyzer dfTA = new InvokeVirtualDefaultTaintAnalyzer(ta);
				logger.debug("InvokeDefaultAnalyzer");
				return (SymbolTableEntry) dfTA.analyzeInstruction();
			} else {

				InvokeStaticMethodHandler mHandler = new InvokeStaticMethodHandler(ta);
				MethodSignature ms = mHandler.getMethodSignatureFromCurrInstruction(ir);

				if (ms != null) {
					CFG cfg = apk.findMethodBySignature(ms);

					if (cfg != null) {

						logger.debug("key for new cfg called -> " + cfg.getKey());
						logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + ms.getParams().size());
						logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: " + cfg.getSignature().getParams().size());

						boolean result = mHandler.handleMethodCall(cfg);

						cfg.nullifyBBOutSets();
						// If method type was getPasword which would return a String password with
						// return-object vA type instruction. So we want this result in our current symbol table.

						if (result) {
							Object obj = ta.getInstrReturnedObject();

							if (null != obj) {
								SymbolTableEntry entry = null;
								if (obj instanceof SymbolTableEntry) {
									entry = (SymbolTableEntry) obj;
								} else if (obj instanceof InstructionReturnValue) {
									InstructionReturnValue returnObj = (InstructionReturnValue) obj;
									entry = returnObj.getReturnEntry();
								}

								logger.debug("\n Status of symbolspace after InvokeStatic");
								localSymSpace.logInfoSymbolSpace();
								logger.debug("\n Exiting InvokeStatic: returned entry");

								return entry;
								// 				   ta.getLocalSymSpace().addEntry(entry);
							}
						} else {
							logger.debug("\n Status of symbolspace after InvokeState");
							localSymSpace.logInfoSymbolSpace();
							logger.debug("\n Exiting InvokeStatic: returned NULL");

							return null;
						}
					} else {
						InvokeDirectDefaultTaintAnalyzer dfTA = new InvokeDirectDefaultTaintAnalyzer(ta);
						return (SymbolTableEntry) dfTA.analyzeInstruction();
					}

				}
			}
		}
		logger.debug("\n Status of symbolspace after InvokeState");
		localSymSpace.logInfoSymbolSpace();
		logger.debug("\n Exiting InvokeStatic");
		return null;
	}

	public Object getApiReturnsObj() {
		return apiReturnsObj;
	}

	public void setApiReturnsObj(Object apiReturnsObj) {
		this.apiReturnsObj = apiReturnsObj;
	}
}