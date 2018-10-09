package taintanalyzer.instranalyzers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.ClassObj;
import models.cfg.Instruction;
import models.cfg.InstructionReturnValue;
import models.cfg.Instruction.API_TYPES;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.InformationLeakerASMEvent;
import patternMatcher.events.csm.SmsSenderEvent;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;

public class InvokeVirtualTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;

	String[] used;
	String changed;
	private TaintAnalyzer ta;
	private Properties apiRulesMap;
	private SymbolTableEntry returnEntry;
	private APK apk;

	private Properties apiDefinedAnalyzers;

	private Object apiReturnsObj;

	public InvokeVirtualTaintAnalyzer(TaintAnalyzer ta) {
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		this.ta = ta;
		this.returnEntry = new SymbolTableEntry();
		this.apk = ta.getApk();

		logger = Logger.getLogger(InvokeVirtualTaintAnalyzer.class);
	}

	public Object analyzeInstruction() {

		String instrText = ir.getInstr().getText();
		SymbolTableEntry retEntry = null;
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

		//Note: The above if condition (line 95) was not allowing analysis of user-defined "com/android/provider;->a()" method. It's hard to get
		// complete list of com/android APIs which are only Android defined. And user can define it himself anyway, so forget about efficiency from here.
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
				localSymSpace.logInfoSymbolSpace();

				return apiReturnsObj;

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
		}
		//This check should be both for Landroid/Ljava etc & user-defined. because of the pre-defined methods (abortBroadcast()) can
		// be called by a user defined receiver class.
		else if (apiRulesMap.containsKey(qualifiedAPIName)) {
			String key = apiRulesMap.getProperty(qualifiedAPIName);
			logger.debug("apiRulesMap -> qualifiedAPIName condition true");

			if (key.equalsIgnoreCase(API_TYPES.dumbAPI.toString())) {
				return returnEntry;
			} else if (key.equalsIgnoreCase(API_TYPES.storeVariable.toString())) {
				InvokeVirtualDefaultTaintAnalyzer dfTA = new InvokeVirtualDefaultTaintAnalyzer(ta);
				return (SymbolTableEntry) dfTA.analyzeInstruction();
			} else if (key.equalsIgnoreCase(API_TYPES.taintPropagater.toString())) {
				InvokeVirtualDefaultTaintAnalyzer dfTA = new InvokeVirtualDefaultTaintAnalyzer(ta);
				return (SymbolTableEntry) dfTA.analyzeInstruction();
			}
		} else if (apiRulesMap.containsKey(methdObjectName)) {
			returnEntryDetails.setField(false);
			returnEntryDetails.setType(ir.getReturnType());
			returnEntryDetails.setRecord(false);
			returnEntryDetails.setTainted(false);
			returnEntryDetails.setValue(" ");

			logger.debug("apiRulesMap -> methodObjectName condition true");

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
			boolean tainted = false;
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

					EventFactory.getInstance().registerEvent("InformationLeakerEvent", new InformationLeakerASMEvent());

					Event event = EventFactory.getInstance().createEvent("InformationLeakerEvent");

					event.setName("InformationLeakerEvent");

					event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
					event.setCurrComponentName(ta.getCurrComponentName());
					event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
					event.setCurrMethodName(instr.getCurrMethodName());
					event.setCurrPkgClsName(instr.getCurrPkgClassName());

					event.getEventInfo().put("sources", srcAPIList);
					event.getEventInfo().put("instrText", instr.getText());

					ta.setCurrCSMEvent(event);

				}

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

					if (recipientNoEntry != null) {
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
						event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);

						ta.setCurrCSMEvent(event);
					}

				}
			}
			logger.error(" \n\n **** Sink API => " + ir.getInstr().getText() + "\n\n");

			InvokeVirtualDefaultTaintAnalyzer dfTA = new InvokeVirtualDefaultTaintAnalyzer(ta);
			return (SymbolTableEntry) dfTA.analyzeInstruction();

		} else if (ir.isSourceAPI()) {
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
				baseTaintAnalyzer = (BaseTaintAnalyzer) cls.getDeclaredConstructor(new Class[] { TaintAnalyzer.class }).newInstance(ta);
				setApiReturnsObj(baseTaintAnalyzer.analyzeInstruction());

				localSymSpace.logInfoSymbolSpace();

				return apiReturnsObj;

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
				logger.debug(" BlackListAPICalls.");
				return (SymbolTableEntry) dfTA.analyzeInstruction();
			} else {
				if (ir.getInvolvedRegisters().size() > 0) {
					logger.debug("MethodHandler called.");
					MethodHandler mHandler = new MethodHandler(ta);
					MethodSignature ms = mHandler.getMethodSignatureFromCurrInstruction(ir);

					//First find a method for current (child) class and then check for parent.
					Register callerAPIReg = ir.getInvolvedRegisters().get(0);
					SymbolTableEntry callerAPIEntry = this.localSymSpace.find(callerAPIReg.getName());

					if (callerAPIEntry != null) {

						String type = callerAPIEntry.getEntryDetails().getType();
						MethodSignature childMS = new MethodSignature(ms);
						childMS.setPkgClsName(type);

						CFG cfg = apk.findMethodBySignature(childMS);
						if (cfg == null) {
							cfg = apk.findMethodBySignature(ms);
						}
						if (cfg != null) {

							logger.debug("cfg key -> " + cfg.getKey());
							logger.debug("[InvokeTaintAnalyzer] from caller instr:: " + ms.getParams().size());
							logger.debug("[InvokeTaintAnalyzer] from apk found cfg:: " + cfg.getSignature().getParams().size());

							boolean result = mHandler.handleMethodCall(cfg);

							cfg.nullifyBBOutSets();
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

									logger.debug("\n InvokeTaintAnalyzer");
									localSymSpace.logInfoSymbolSpace();

									logger.debug("\n </end> Global Entry");
									Config.getInstance().getGlobalSymbolSpace().logInfoSymbolSpace();

									return entry; //move instruction will set it into symbolTable.
								}
							}
							return null;
						}
					}
					{
						//Called method may be run() method, which is called by ->start() method.
						// Thread->start() is handled explicitly but following type can't be.

						// 0xa invoke-virtual v0, Lcom/android/MonitorService$2;->start()V

						/*
						 * 0x4 invoke-direct v0, v1,
						 * Lcom/xxx/yyy/qzl$1;-><init>(Lcom/xxx/yyy/qzl;)V
						 * 0xa invoke-virtual v0, Lcom/xxx/yyy/qzl$1;->start()V
						 * 
						 * 
						 * needs to be replaced by
						 * "0xa invoke-virtual v0, Lcom/xxx/yyy/qzl$1;->run()V"
						 */
						String callerAPI = ir.getCallerAPIName();
						String callerAPI2 = callerAPI;

						if (callerAPI2.endsWith(";"))
							callerAPI2 = callerAPI2.substring(0, callerAPI2.length() - 1);

						ClassObj callerAPIClass = (ClassObj) apk.findClassByKey(callerAPI2);

						ArrayList<Register> allRegs = ir.getInvolvedRegisters();
						Register runnableReg = null;
						String runnableParamType = "";
						boolean isThereRunnableObject = false;
						//TODO: These if-else conditions demand a better generic approach to handle all cases.
						//check if any of input parameters or receiver object is runnable.

						// invoke-interface v5, Ljava/lang/Runnable;->run()V
						// 0x36 invoke-direct v0, v4, v1, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable; Ljava/lang/String;)V

						for (int i = 0; i < allRegs.size(); i++) {
							runnableReg = allRegs.get(i);
							runnableParamType = runnableReg.getType();
							if (runnableParamType.trim().equalsIgnoreCase("Ljava/lang/Runnable;")
									|| runnableParamType.trim().equalsIgnoreCase("Ljava/util/TimerTask;")) {
								isThereRunnableObject = true;
								break;
							}
						}
						logger.debug("Looking for methodss");
						if (isThereRunnableObject) {
							//As soon as we get Runnable object as a parameter, we immediately calls its run() method. 

							//	Line 54394: 		0x74 invoke-virtual v1, v0, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
							//	Line 57641: 		0x4 invoke-virtual v1, v2, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

							instrText = ir.getInstr().getText();
							SymbolTableEntry callerEntry = this.localSymSpace.find(runnableReg.getName());

							// SymbolTableEntry listnerAPIEntry = this.localSymSpace.find(listenerAPIReg.getName().trim());

							if (callerEntry != null) {
								logger.debug("Location Listener");

								String threadType = callerEntry.getEntryDetails().getType();
								ClassObj thread = apk.findClassByKey(threadType);

								if (thread != null) {
									CFG runMethod = (CFG) thread.findCFGByKey("run");

									if (runMethod != null) {
										logger.debug("cfg key -> " + runMethod.getKey());
										boolean result = mHandler.handleMethodCall(runMethod, runnableReg);
										runMethod.nullifyBBOutSets();

									}
								}
							}
							return null;
						} else if (ir.getMethodOrObjectName().equalsIgnoreCase("start")) {
							//		0x12 invoke-direct/range v0 ... v5, Lcom/adnovo/d;-><init>(Landroid/content/Context; Ljava/lang/String; Ljava/lang/String; Ljava/lang/String; Ljava/lang/String;)V
							//		0x18 invoke-virtual v0, Lcom/adnovo/d;->start()V

							instrText = ir.getInstr().getText();
							MethodSignature newMS = mHandler.getMethodSignatureFromCurrInstruction(ir);
							newMS.setName("run");
							if (newMS != null) {
								CFG newCFG = apk.findMethodBySignature(newMS);
								if (newCFG != null) {
									logger.debug("cfg key -> " + newCFG.getKey());
									logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + newMS.getParams().size());
									logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: " + newCFG.getSignature().getParams().size());

									boolean result = mHandler.handleMethodCall(newCFG);

									newCFG.nullifyBBOutSets();

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

											logger.debug("\n InvokeTaintAnalyzer");
											localSymSpace.logInfoSymbolSpace();

											logger.debug("\n </end> Global Entry");
											Config.getInstance().getGlobalSymbolSpace().logInfoSymbolSpace();

											return entry;
											// 				   ta.getLocalSymSpace().addEntry(entry);
										}
									}
								}
							}
						} else if (ir.getMethodOrObjectName().equalsIgnoreCase("sendEmptyMessage")
								|| ir.getMethodOrObjectName().equalsIgnoreCase("sendEmptyMessageAtTime")
								|| ir.getMethodOrObjectName().equalsIgnoreCase("sendEmptyMessageDelayed")
								|| ir.getMethodOrObjectName().equalsIgnoreCase("sendMessage")
								|| ir.getMethodOrObjectName().equalsIgnoreCase("sendMessageAtFrontOfQueue")
								|| ir.getMethodOrObjectName().equalsIgnoreCase("sendMessageAtTime")
								|| ir.getMethodOrObjectName().equalsIgnoreCase("sendMessageAtFrontOfQueue")
								|| ir.getMethodOrObjectName().equalsIgnoreCase("sendMessageDelayed")
								|| ir.getMethodOrObjectName().equalsIgnoreCase("dispatchMessage")) {
							//
							String instrTxt = ir.getInstr().getText();
							String instrSplitWithArrow[] = instrTxt.split("->");
							String arrowRightSideSplit[] = instrSplitWithArrow[1].split("[(]");

							// This could be
							// 0x142 invoke-virtual v1, v12, Landroid/os/Handler;->sendMessage(Landroid/os/Message;)Z
							// which is not correct. Handler is a parent class but v1 here is a child class such as CallHandler.
							// So if called by parent class, it will be handled by apihandlers.android.os.Handler.SendMessageAnalyzer
							// otherwise, it will be handled here automatically.

							MethodSignature newMS = mHandler.getMethodSignatureFromCurrInstruction(ir);

							newMS.setName("handleMessage");

							if (newMS != null) {
								CFG newCFG = apk.findMethodBySignature(newMS);

								if (newCFG != null) {

									logger.debug("cfg key -> " + newCFG.getKey());
									logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + newMS.getParams().size());
									logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: " + newCFG.getSignature().getParams().size());

									boolean result = mHandler.handleMethodCall(newCFG);

									newCFG.nullifyBBOutSets();

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

											logger.debug("\n InvokeTaintAnalyzer");
											localSymSpace.logInfoSymbolSpace();

											logger.debug("\n </end> Global Entry");
											Config.getInstance().getGlobalSymbolSpace().logInfoSymbolSpace();

											return entry;
											// 				   ta.getLocalSymSpace().addEntry(entry);
										}
									}
								}
							}
						} else if (ir.getMethodOrObjectName().equalsIgnoreCase("execute") && callerAPIClass != null
								&& callerAPIClass.getType().equalsIgnoreCase(ComponentTypes.task.toString())) {
							logger.debug("AsyncTask key -> " + callerAPIClass.getKey());
							InvokeAsynchTaskHandler taskHandler = new InvokeAsynchTaskHandler(ta);
							taskHandler.handleComponentCall(callerAPIClass);
							logger.debug("\n </end> AsynchTask Call Finished.");
							localSymSpace.logInfoSymbolSpace();
						} else if ((ir.getMethodOrObjectName().equalsIgnoreCase("registerActivityLifecycleCallbacks"))
								|| (ir.getMethodOrObjectName().equalsIgnoreCase("registerComponentCallbacks"))) {
							instrText = ir.getInstr().getText();
							SymbolTableEntry callerEntry = this.localSymSpace.find(runnableReg.getName());
							if (callerEntry != null) {
								String threadType = callerEntry.getEntryDetails().getType();
								ClassObj callbackInterface = apk.findClassByKey(threadType);

								if (callbackInterface != null) {
									logger.debug("RegisterCallbacks Handler -> " + callbackInterface.getKey());
									InvokeRegisterComponentCallbacksHandler regHandler = new InvokeRegisterComponentCallbacksHandler(ta);
									regHandler.handleComponentCall(callbackInterface);
									logger.debug("\n </end> AsynchTask Call Finished.");
									localSymSpace.logInfoSymbolSpace();
								}
							}
						} else if (ir.getMethodOrObjectName().equalsIgnoreCase("setOnClickListener")) {
							Register listenerAPIReg = ir.getInvolvedRegisters().get(ir.getInvolvedRegisters().size() - 1);

							SymbolTableEntry listnerAPIEntry = this.localSymSpace.find(listenerAPIReg.getName().trim());

							if (listnerAPIEntry != null) {
								logger.debug("Location Listener");

								callerAPI2 = listnerAPIEntry.getEntryDetails().getType();

								if (callerAPI2.endsWith(";"))
									callerAPI2 = callerAPI2.substring(0, callerAPI2.length() - 1);

								ClassObj cls = (ClassObj) apk.findClassByKey(callerAPI2);

								if (cls != null) {

									logger.debug("cls key -> " + cls.getKey());
									InvokeOnClickListenerkHandler locationHandler = new InvokeOnClickListenerkHandler(ta);
									locationHandler.handleComponentCall(cls);

									logger.debug("\n </end> LocationListener Call Finished.");
									localSymSpace.logInfoSymbolSpace();
								}

							}
						}

						else if (ir.getMethodOrObjectName().equalsIgnoreCase("requestLocationUpdates")) {
							//	0x34 iget-object v5, v6, Lde/ecspride/AnnonymousClass1;->locationListener Landroid/location/LocationListener;
							//	0x38 invoke-virtual/range v0 ... v5, Landroid/location/LocationManager;->requestLocationUpdates(Ljava/lang/String; J F Landroid/location/LocationListener;)V

							callerAPI = ir.getCallerAPIName();
							Register listenerAPIReg = ir.getInvolvedRegisters().get(ir.getInvolvedRegisters().size() - 1);

							SymbolTableEntry listnerAPIEntry = this.localSymSpace.find(listenerAPIReg.getName().trim());

							if (listnerAPIEntry != null) {
								logger.debug("Location Listener");

								callerAPI2 = listnerAPIEntry.getEntryDetails().getType();

								if (callerAPI2.endsWith(";"))
									callerAPI2 = callerAPI2.substring(0, callerAPI2.length() - 1);

								ClassObj cls = (ClassObj) apk.findClassByKey(callerAPI2);

								if (cls != null) {

									logger.debug("cls key -> " + cls.getKey());
									InvokeLocationListenerkHandler locationHandler = new InvokeLocationListenerkHandler(ta);
									locationHandler.handleComponentCall(cls);

									logger.debug("\n </end> LocationListener Call Finished.");
									localSymSpace.logInfoSymbolSpace();
								}

							}
						} else if (Config.isIsparentchildhandling())

						// find this method (1) from parent class because it may be an inherited method 
						//					(2) in the child classes because it may be a method called from a parent class object.
						//					Parent p = new Child1();, p.child1Method();
						{
							instrText = ir.getInstr().getText();
							//	 				    		   System.out.println("original instruction = " + instrText);

							ClassObj currCls = apk.findClassByKey(ir.getCallerAPIName()); //ta.getCurrCls();
							List<CFG> cfgList = new ArrayList<CFG>(); //We use this list to go through all CFGs of a parent and children classes.

							if (currCls != null) {
								ClassObj parent = currCls.getParent();
								MethodSignature newMS = mHandler.getMethodSignatureFromCurrInstruction(ir);
								CFG newCFG = null;
								
								//we will go only one level of parent classes.
								if (parent != null) {
									String parentPkgCls = parent.getCurrPkgClassName();
									parentPkgCls += ";";
									newMS.setPkgClsName(parentPkgCls);
									newCFG = apk.findMethodBySignature(newMS);
									logger.debug("<parent>=" + parent.getKey());

									if (newCFG != null)
										cfgList.add(newCFG);
								}
								//find in depth of child classes now. Go for only one level.
								ArrayList<ClassObj> children = currCls.getChildren();
								if (children != null) {
									for (ClassObj child : children) {
										logger.debug("<child>=" + child.getKey());

										newCFG = null;
										String childPkgCls = child.getCurrPkgClassName();
										childPkgCls += ";";
										newMS.setPkgClsName(childPkgCls);
										newCFG = apk.findMethodBySignature(newMS);

										if (newCFG != null) {
											cfgList.add(newCFG);
										}
									}
								}
								if (cfgList.size() > 0) {
									for (CFG cfgObj : cfgList) {
										//logger.fatal("original instruction = " + ir.getInstr().getText());
										//		 				 				   logger.fatal("new method call = " + newCFG.getCurrPkgClassName());

										logger.debug("cfg key -> " + cfgObj.getKey());
										logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + newMS.getParams().size());
										logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: "
												+ cfgObj.getSignature().getParams().size());

										boolean result = false;

										result = mHandler.handleMethodCall(cfgObj);
										cfgObj.nullifyBBOutSets();

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

												logger.debug("\n InvokeTaintAnalyzer");
												localSymSpace.logInfoSymbolSpace();

												logger.debug("\n </end> Global Entry");
												Config.getInstance().getGlobalSymbolSpace().logInfoSymbolSpace();

												retEntry = entry;
												//				 						 			       return entry;    //return breaks the for loop. But I want to analyze all CFGs in the cfgList.
												// Unfortunately, this will return from the last CFG only. Ideally, it should go through each
												// CFG one by one and return the results.
											} else
												return null;
										} else
											return null;
									}
								} else {
									logger.debug(" Going for DefaultVirtualTaintAnalyzer");
									InvokeVirtualDefaultTaintAnalyzer dfTA = new InvokeVirtualDefaultTaintAnalyzer(ta);
									return dfTA.analyzeInstruction();

								}
							} else {
								logger.debug(" Going for DefaultVirtualTaintAnalyzer");
								InvokeVirtualDefaultTaintAnalyzer dfTA = new InvokeVirtualDefaultTaintAnalyzer(ta);
								return dfTA.analyzeInstruction();
							}
						} else {
							logger.debug(" Going for DefaultVirtualTaintAnalyzer");
							InvokeVirtualDefaultTaintAnalyzer dfTA = new InvokeVirtualDefaultTaintAnalyzer(ta);
							return dfTA.analyzeInstruction();
						}
					}
				}
			}
		}
		logger.debug("\n InvokeTaintAnalyzer");
		localSymSpace.logInfoSymbolSpace();

		logger.debug("\n Global Entry");
		SymbolSpace globalSymSpace = Config.getInstance().getGlobalSymbolSpace();
		globalSymSpace.logInfoSymbolSpace();

		return retEntry;
	}

	public Object getApiReturnsObj() {
		return apiReturnsObj;
	}

	public void setApiReturnsObj(Object apiReturnsObj) {
		this.apiReturnsObj = apiReturnsObj;
	}
}