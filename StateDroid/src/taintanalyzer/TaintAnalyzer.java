package taintanalyzer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import models.cfg.APK;
import models.cfg.BasicBlock;
import models.cfg.BasicBlockResponse;
import models.cfg.CFG;
import models.cfg.CFGComponent;
import models.cfg.ClassObj;
import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.cfg.Package;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.Context;
import models.symboltable.ContextStack;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;

import patternMatcher.AttackObserver;
import patternMatcher.attackreporter.Report;
import patternMatcher.events.Event;
import patternMatcher.statemachines.asm.appremovalASM.AppRemovalASMObserver;
import patternMatcher.statemachines.asm.audiomanagerASM.AudioManagerASMObserver;
import patternMatcher.statemachines.asm.audiovideorecorderASM.AudioVideoRecorderASMObserver;
import patternMatcher.statemachines.asm.contentresolverASM.ContentResolverASMObserver;
import patternMatcher.statemachines.asm.filereaderASM.FileReaderASMObserver;
import patternMatcher.statemachines.asm.keyguardmanagerASM.KeyguardManagerASMObserver;
import patternMatcher.statemachines.asm.lockscreenphonecallerASM.LockScreenPhoneCallerASMObserver;
import patternMatcher.statemachines.asm.lockscreensilentphonecallblockerASM.LockScreenSilentPhoneCallBlockerASMObserver;
import patternMatcher.statemachines.asm.nickispycASM.NickiSpyCASMObserver;
import patternMatcher.statemachines.asm.phonecallASM.PhoneCallASMObserver;
import patternMatcher.statemachines.asm.phonecallblockerASM.PhoneCallBlockerASMObserver;
import patternMatcher.statemachines.asm.phonecallerASM.PhoneCallerASMObserver;
import patternMatcher.statemachines.asm.phonecallforwardingASM.PhoneCallForwardingASMObserver;
import patternMatcher.statemachines.asm.ringermodesilencerASM.RingerModeSilencerASMObserver;
import patternMatcher.statemachines.asm.settingstogglerASM.SettingsTogglerASMObserver;
import patternMatcher.statemachines.asm.silentlockscreenphonecallblockerASM.SilentLockScreenPhoneCallBlockerASMObserver;
import patternMatcher.statemachines.asm.silentlockscreenphonecallerASM.SilentLockScreenPhoneCallerASMObserver;
import patternMatcher.statemachines.asm.silentphonecallblockerASM.SilentPhoneCallBlockerASMObserver;
import patternMatcher.statemachines.asm.silentphonecallerASM.SilentPhoneCallerASMObserver;
import patternMatcher.statemachines.asm.smsautoreplyblockerASM.SmsAutoReplyBlockerASMObserver;
import patternMatcher.statemachines.asm.smsblockautoreplierASM.SmsBlockAutoReplierASMObserver;
import patternMatcher.statemachines.asm.smsdeleteandsmsASM.SmsDeleteAndSendASMObserver;
import patternMatcher.statemachines.asm.smssendanddeleteASM.SmsSendAndDeleteASMObserver;
import patternMatcher.statemachines.csm.abortbroadcast.AbortBroadcastObserver;
import patternMatcher.statemachines.csm.appremoval.AppRemovalObserver;
import patternMatcher.statemachines.csm.audiomanager.AudioManagerObserver;
import patternMatcher.statemachines.csm.contentresolver.ContentResolverObserver;
import patternMatcher.statemachines.csm.context.ContextObserver;
import patternMatcher.statemachines.csm.filereading.FileReadingObserver;
import patternMatcher.statemachines.csm.incomingsmsautoreplier.IncomingSmsAutoReplierObserver;
import patternMatcher.statemachines.csm.informationleaker.InformationLeakerObserver;
import patternMatcher.statemachines.csm.informationstoringintodb.InformationStoringIntoDBObserver;
import patternMatcher.statemachines.csm.intent.IntentObserver;
import patternMatcher.statemachines.csm.keyguardmanager.KeyguardManagerObserver;
import patternMatcher.statemachines.csm.mediarecorder.MediaRecorderObserver;
import patternMatcher.statemachines.csm.reflection.ReflectionObserver;
import patternMatcher.statemachines.csm.resetpassword.ResetPasswordObserver;
import patternMatcher.statemachines.csm.runtimeexecution.RuntimeExecutionObserver;
import patternMatcher.statemachines.csm.settingstoggler.SettingsTogglerObserver;
import patternMatcher.statemachines.csm.smssender.SmsSenderObserver;
import patternMatcher.statemachines.csm.streamvolumemodifier.StreamVolumeModifierObserver;
import patternMatcher.statemachines.csm.uri.uriObserver;
import patternMatcher.statemachines.csm.url.urlObserver;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import analyzer.Analyzer;
import configuration.Config;
import enums.ComponentTypes;

public class TaintAnalyzer extends Analyzer {

	private Instruction prev = null;
	private InstructionResponse ir;
	private boolean firstBB;
	private SymbolSpace globalSymSpace;
	private SymbolSpace taLocalSymSpace;

	public double totalTimeTakenCSM = 0.0;
	public double totalTimeTakenASM = 0;
	private Package pkg;
	private APK apk;
	private ContextStack contextStack;
	// private Hashtable recordsList;
	private Hashtable lastBBOutSet;
	private ContextStack scenarioContextStack;
	private String threadType = "";
	private String currComponentName = ""; 
	private String currComponentCallback = "";
	private String currComponentPkgName = "";
	private CFG currCFG=null; 
	private static ArrayList<AttackObserver> csmObservers;
	private static ArrayList<AttackObserver> asmObservers;
	private Event currCSMEvent = null;
	private Event currASMEvent = null;
	private ClassObj currCls = null;

	private ArrayList<Report> reportList;
	private Properties instrTaintAnalyzers;
	private static Logger logger;
	private int handleComponentsFlag = 0;  // 0 ==> manifest components; 1 => non-manifest-components; 2 => individual threads and background tasks

	public Object instrReturnedObject;
	private long startTime = 0;
	int interval = 1;


	public TaintAnalyzer() {

		super();

		taLocalSymSpace = Config.getInstance().getLocalSymbolSpace();
		setGlobalSymSpace(new SymbolSpace());
		setLastBBOutSet(new Hashtable());

		logger = Logger.getLogger(TaintAnalyzer.class);
		instrTaintAnalyzers = Config.getInstance()
				.getInstructionTaintAnalyzersMap();
		scenarioContextStack = new ContextStack();
		startTime = System.currentTimeMillis();

		initializeCSMs();
		initializeASMs();	
	}

	@SuppressWarnings("unchecked")
	public void analyze(Instruction ins) {
		Class cls = null;
		InstructionResponse taintIR = new InstructionResponse();
		BaseTaintAnalyzer baseTaintAnalyzer;

		if(!ins.getText().contains("invalid_class_name")){
			ir = ins.instructionHandler(prev);

			if (ir != null) {
				String instTypeBySyntax = ir.getInstr().getTypeBySyntax();

				logger.debug("type by syntax " + instTypeBySyntax);

				if (instrTaintAnalyzers.containsKey(instTypeBySyntax)) {
					String analyzerName = instrTaintAnalyzers
							.getProperty(instTypeBySyntax);

					String completeAnalyzerName = new StringBuilder(
							"taintanalyzer.instranalyzers.").append(analyzerName)
							.toString();

					logger.debug("TaintAnalyzer.java -> completeAnalyzerName "
							+ completeAnalyzerName);

					logger.debug("In " + ins.getCurrMethodName() + ", Before ==>"
							+ ins.getText());

					try {

						cls = Class.forName(completeAnalyzerName);
						baseTaintAnalyzer = (BaseTaintAnalyzer) cls
								.getDeclaredConstructor(
										new Class[] { TaintAnalyzer.class })
										.newInstance(this);
						setInstrReturnedObject(baseTaintAnalyzer
								.analyzeInstruction()); // In most cases, it returns SymbolTableEntry but due to state-machines, it can now return InstructionReturnValue object.

						logger.debug("In " + ins.getCurrMethodName()
								+ ", After ==>" + ins.getText());

					} 
					catch (ClassNotFoundException e) {
						e.printStackTrace();
					}catch (InstantiationException e) {
						e.printStackTrace();
					}catch (IllegalAccessException e) {
						e.printStackTrace();
					}catch (IllegalArgumentException e) {
						e.printStackTrace();
					}catch (InvocationTargetException e) {
						e.printStackTrace();
					}catch (NoSuchMethodException e) {
						e.printStackTrace();
					}catch (SecurityException e) {
						e.printStackTrace();
					}			}
			}
		}
	}

	public void analyze(BasicBlock bb) {
		BasicBlockResponse bbR = new BasicBlockResponse();
		InstructionResponse lastIR = new InstructionResponse();

		taLocalSymSpace.logInfoSymbolSpace();

		logger.info("bbVisited =>" + bb.getKey());
		prev = null;
		Iterator it = bb.iterator();
		while (it.hasNext()) {
			CFGComponent comp = (CFGComponent) it.next();
			if ((Instruction) comp != null) {
				logger.debug("Instr >>" + comp.getText());
				comp.accept(this);
				prev = (Instruction) comp;
			}
		}
		Hashtable out = taLocalSymSpace.pop();
		lastBBOutSet = out;
		bb.setOUT(out);

		bb.setShadowCopyOfOut(out);
		bb.setShadowCopyOfGlobalSymTable();
	}

	public void analyze(CFG cfg) {
		this.setCurrCFG(cfg);
		logger.debug("Method key ----====================> "
				+ cfg.getCurrPkgClassName() + "; " + cfg.getKey());

		long currTime = System.currentTimeMillis();
		long diffTime = (currTime - startTime) / 60000;

		if (diffTime >= 10) {
			startTime = currTime;
			logger.fatal("<<<<<<<<<<<<<<<<<<<<<<  Time Check: " + interval
					* diffTime + " minutes passed >>>>>>>>>>>>>>>>>>>>>");
			interval++;
		}

		int compSize = cfg.getCompCollection().size();
		Iterator it = cfg.iterator(); // BBOrderIdentifieer
		while (it.hasNext()) {
			CFGComponent comp = (CFGComponent) it.next();
			if (!firstBB) {
				Hashtable predUnionTable = cfg
						.setSymbolTableEntriesFromPredecessors(comp);
				cfg.setGlobalSymbolSpace(comp);

				taLocalSymSpace.push(predUnionTable);
			}
			logger.info("BB Key :: " + comp.getKey() + ", compCount >>>>>> "
					+ comp.getCompCollection().size());
			comp.accept(this);
			firstBB = false;
		}
		this.setCurrCFG(null);
		cfg.setExceptionObject(null);

		// }

		// When a method is analyzed completely, all its symbol tables are
		// removed automatically. Since it iterates over a list of basic blocks,
		// and after each basic block, symbol table for that BB is removed. So
		// eventually, when the last BB is analyzed, symbol table for the
		// last BB is actually last symbol table for that CFG. Since symbol
		// space entries are propagated to all the successors, we don't have
		// symbol table at CFG level.
	}

	public void analyze(ClassObj classObj) {

		initializeASMs(); //reset all ASMs	

		// CFG permutations will come here.
		Iterator it = classObj.iterator();
		currCls = classObj;

		int scenarioCount = 0;
		String pkgClsEntryName = "";
		// Again, this iterator is responsible to return cfg items to this
		// method for analysis.
		// And this iterator should return only callback methods, not the
		// utility functions which
		// you can find out with the given sample method in controller class.

		String prevPermSequence = "<<permSequence>>= ";
		int nwayCounter = 1;
		long startTime = System.currentTimeMillis();
		Config.getInstance().setCurrCFGPermutation(new ArrayList<String>());
		while (it.hasNext()) {
			CFGComponent comp = (CFGComponent) it.next();

			String methodKey = comp.getKey();

			if (!methodKey.startsWith("border")
					&& !Config.getInstance().isComponentAnalysisTimedOut()
					) {
				CFG cfg = (CFG) classObj.findCFGByKey(methodKey);
				Config.getInstance().getCurrCFGPermutation().add(methodKey);
				String methodType = cfg.getType();
				firstBB = true;

				// pkgClsEntryName contains variable name of entry generated by
				// <init> function. Since each method replaces
				// that entry name for itself, so we change the name for each
				// next method.
				pkgClsEntryName = cfg.pushInputParamsForCallbacks(
						taLocalSymSpace, classObj);

				currComponentCallback = cfg.getKey();

				//	logger.error("Method key ----====================> " + methodKey + ", paramters => " + cfg.getParamList().toString());

				prevPermSequence += cfg.getKey() + "::";

				cfg.accept(this);

				cfg.nullifyBBOutSets();
				logger.info("<Returned from cfg> = " + cfg.getKey()
						+ ", pckgClsEntryName = " + pkgClsEntryName);
				
			} else if (methodKey.equalsIgnoreCase("borderCFG")) {
				taLocalSymSpace.removeEntriesUptoInclusiveLevel(1);
			} else if (methodKey.equalsIgnoreCase("borderPermutation")) {
				Config.getInstance().setCurrCFGPermutation(
						new ArrayList<String>());

				taLocalSymSpace.removeEntriesUptoInclusiveLevel(0);
				logger.debug(prevPermSequence);

				prevPermSequence = "<<permSequence>>= ";
			} else if (methodKey.equalsIgnoreCase("borderNWayPermutation")) {
				long endTime = System.currentTimeMillis();
				long diffTime = (endTime - startTime) / 1000;

				if (Config.getInstance().isAttackReported()) {
					logger.fatal("Nway Permutation sequence finished in time (sec) = "
							+ diffTime + "  with P= " + nwayCounter);
					Config.getInstance().setAttackReported(false);
				}
				nwayCounter++;
				startTime = System.currentTimeMillis();

			}

		}

	}

	public void analyze(Package pckage) {
		// It iterates over list of Activities. Since other classes are utility
		// classes, they will be
		// called upon a call to that class only. But we want to handle
		// Activities on our own.

		// Here we control order between activities and also create a
		// symbolSpace for file level scope.


		boolean printClassName = false;

		this.pkg = pckage;

		contextStack = new ContextStack();

		// System.out.println("pkgName:" + pkg.getKey());

		AndroidManifest manifest = Config.getInstance().getAndroidManifest();

		String packageName = manifest.getPackageName();

		packageName = packageName.replace(".", "/");
		packageName = new String("L").concat(packageName);

		if(handleComponentsFlag == 0){
			Iterator it = pkg.iterator();
			while (it.hasNext()) {
				CFGComponent comp = (CFGComponent) it.next();
				ClassObj clsObj = (ClassObj) comp;
				String clsKey = clsObj.getCurrPkgClassName();

				if (clsObj.getType().equalsIgnoreCase(
						ComponentTypes.contentProvider.toString())) {
					taLocalSymSpace.removeEntriesUptoInclusiveLevel(0);
					currComponentName = clsObj.getKey();
					clsObj.setAnalyzedAtLeaseOnce(true);

					Config.getInstance().setComponentAnalysisStartTime(System.currentTimeMillis());
					clsObj.accept(this);
					if(printClassName){
						System.out.println("ClsKey = " + clsObj.getCurrPkgClassName() + ", type =  " + clsObj.getType());
					}
				}
			}

			it = pkg.iterator();
			while (it.hasNext()) {
				CFGComponent comp = (CFGComponent) it.next();
				ClassObj clsObj = (ClassObj) comp;
				String clsKey = clsObj.getCurrPkgClassName();

				if (clsObj.getType().equalsIgnoreCase(
						ComponentTypes.application.toString())) {
					taLocalSymSpace.removeEntriesUptoInclusiveLevel(0);
					currComponentName = clsObj.getKey();
					clsObj.setAnalyzedAtLeaseOnce(true);
					Config.getInstance().setComponentAnalysisStartTime(System.currentTimeMillis());

					clsObj.accept(this);				 
					if(printClassName){
						System.out.println("ClsKey = " + clsObj.getCurrPkgClassName() + ", type =  " + clsObj.getType());
					}
				}
			}
			//Prioritizing broadcast receivers for now. TODO: Do revert it back to normal.

			// First analyze all components from manifest.
			ArrayList<String> manifestComponents = manifest.getApplication().getComponentList();
			if(manifestComponents != null && manifestComponents.size() > 0){
				it = pkg.iterator();
				while (it.hasNext()) {
					CFGComponent comp = (CFGComponent) it.next();
					ClassObj clsObj = (ClassObj) comp;
					String clsKey = clsObj.getCurrPkgClassName().concat(";"); 

					//					 System.out.println( " CurrCls-- <beforeIF> " + clsKey + ", Type:" +clsObj.getType());
					if ((
							clsObj.getType().equalsIgnoreCase(
									ComponentTypes.activity.toString())
									|| clsObj.getType().equalsIgnoreCase(
											ComponentTypes.service.toString())
											|| clsObj.getType().equalsIgnoreCase(
													ComponentTypes.broadcastReceiver.toString())
													|| clsObj.getType().equalsIgnoreCase(
															ComponentTypes.adapter.toString()) 
															||  clsObj.getType().equalsIgnoreCase(
																	ComponentTypes.contentObserver.toString())		
																	|| clsObj.getType().equalsIgnoreCase(
																			ComponentTypes.viewGroup.toString())		
																			|| clsObj.getType().equalsIgnoreCase(
																					ComponentTypes.webView.toString())		
							)
							&& (!clsObj.isClassBlacklisted())
							&& (manifestComponents.contains(clsKey))
							&& (!clsObj.isAnalyzedAtLeaseOnce())				 
							)
					{
						//Last check if component is enabled or disabled.
						ComponentManifest compManifest = manifest.getApplication().getComponent(clsKey);
						if(compManifest.isEnabled()){

							clsObj.setAnalyzedAtLeaseOnce(true);

							taLocalSymSpace.removeEntriesUptoInclusiveLevel(0);

							currComponentName = clsObj.getKey();
							if(printClassName){
								System.out.println("ClsKey = " + clsObj.getCurrPkgClassName() + ", type =  " + clsObj.getType());
							}
							Config.getInstance().setComponentAnalysisStartTime(System.currentTimeMillis());
							clsObj.accept(this);

						}
					}
				}
			}
		}
		else if(handleComponentsFlag == 1){
			Iterator it = pkg.iterator();
			while (it.hasNext()) {
				CFGComponent comp = (CFGComponent) it.next();
				ClassObj clsObj = (ClassObj) comp;
				String clsKey = clsObj.getCurrPkgClassName(); // .concat(clsObj.getKey());
				if ((
						clsObj.getType().equalsIgnoreCase(
								ComponentTypes.activity.toString())
								|| clsObj.getType().equalsIgnoreCase(
										ComponentTypes.service.toString())
										|| clsObj.getType().equalsIgnoreCase(
												ComponentTypes.broadcastReceiver.toString())
						)
						&& (!clsObj.isClassBlacklisted())
						&& (!clsObj.isAnalyzedAtLeaseOnce())
						)
				{
					taLocalSymSpace.removeEntriesUptoInclusiveLevel(0);
					clsObj.setAnalyzedAtLeaseOnce(true);

					currComponentName = clsObj.getKey();
					if(printClassName){
						System.out.println("ClsKey = " + clsObj.getCurrPkgClassName() + ", type =  " + clsObj.getType());
					}

					Config.getInstance().setComponentAnalysisStartTime(System.currentTimeMillis());
					clsObj.accept(this);
				}
			}
		}

		else if(handleComponentsFlag == 2){
			Iterator it = pkg.iterator();
			while (it.hasNext()) {
				CFGComponent comp = (CFGComponent) it.next();
				ClassObj clsObj = (ClassObj) comp;
				String clsKey = clsObj.getCurrPkgClassName(); // .concat(clsObj.getKey());

				// logger.fatal( " CurrCls-- " + clsKey + ", Type:" +
				if ((
						clsObj.getType().equalsIgnoreCase(ComponentTypes.task.toString()) 
						||	clsObj.getType().equalsIgnoreCase(ComponentTypes.thread.toString())
						)
						&& (!clsObj.isClassBlacklisted())
						&& (!clsObj.isAnalyzedAtLeaseOnce())
						)
				{
					taLocalSymSpace.removeEntriesUptoInclusiveLevel(0);
					clsObj.setAnalyzedAtLeaseOnce(true);

					currComponentName = clsObj.getKey();
					if(printClassName){
						System.out.println("ClsKey = " + clsObj.getCurrPkgClassName() + ", type =  " + clsObj.getType());
					}
					Config.getInstance().setComponentAnalysisStartTime(System.currentTimeMillis());

					clsObj.accept(this);
				}
			}
		}


	}

	public void analyze(APK appPkg) {
		this.apk = appPkg;

		Iterator it = apk.iterator();
		while (it.hasNext()) {
			CFGComponent comp = (CFGComponent) it.next();

			reportList = new ArrayList<Report>();
			currComponentPkgName = comp.getKey();

			comp.accept(this);
		}

		logger.fatal("< ============================  Analyzing non-manifest components =======================>>");
		boolean isNonComponentAnalysis = Config.getInstance().isNonManifestCompAnalysis();
		if(isNonComponentAnalysis){
			handleComponentsFlag = 1;
			logger.fatal("<<<<<<<<, [Results from non-manifest components, if any >>>>>>>>>>>>>>>>>>>");
			it = apk.iterator();
			while (it.hasNext()) {
				CFGComponent comp = (CFGComponent) it.next();
				reportList = new ArrayList<Report>();
				currComponentPkgName = comp.getKey();

				comp.accept(this);
			}
			//All individual threads are analyzed at the end so that proper paths for these components from other components can be found. 
			handleComponentsFlag = 2;
			logger.fatal("<<<<<<<<, [Results from individual threads and AsyncTasks, if any >>>>>>>>>>>>>>>>>>>");
			it = apk.iterator();
			while (it.hasNext()) {
				CFGComponent comp = (CFGComponent) it.next();
				reportList = new ArrayList<Report>();
				currComponentPkgName = comp.getKey();

				comp.accept(this);
			}
		}
	}

	public void iteratePackages(APK apk){
		Iterator it = apk.iterator();
		while (it.hasNext()) {
			CFGComponent comp = (CFGComponent) it.next();
			currComponentPkgName = comp.getKey();
			comp.accept(this);
		}
	}

	public void saveScenarioContext() {
		if (this.taLocalSymSpace.getEntries().size() > 0) {
			Hashtable ht = (Hashtable) this.taLocalSymSpace.getItem(0);

			Hashtable htNewCopy = null;
			if (ht != null) {
				htNewCopy = new Hashtable();

				Enumeration<String> enumKey = ht.keys();
				while (enumKey.hasMoreElements()) {
					String key = enumKey.nextElement().toString();
					SymbolTableEntry arg = (SymbolTableEntry) ht.get(key);

					SymbolTableEntry clonedEntry = new SymbolTableEntry(arg); // deep
					// copy
					htNewCopy.put(key, clonedEntry);
				}
			}
			Context context = new Context();
			if (htNewCopy.size() > 0)
				context.addItem(htNewCopy);

			logger.debug("@@ saveScenarioContext @@");
			context.printContext();

			scenarioContextStack.saveContext(context);
		}
	}

	public ArrayList<Report> getReportList() {
		return reportList;
	}

	public void setReportList(ArrayList<Report> reportList) {
		this.reportList = reportList;
	}

	public void getAndSetScenarioContext() {
		Context context = scenarioContextStack.retrieveContextByPeek();
		logger.debug("@@ getAndSetScenarioContext @@");

		if (context != null) {
			context.printContext();

			Hashtable ht = (Hashtable) context.peekTopItem();
			if (ht != null) {
				// ht = (Hashtable) ht.clone();
				Hashtable htNewCopy = new Hashtable();

				Enumeration<String> enumKey = ht.keys();
				while (enumKey.hasMoreElements()) {
					String key = enumKey.nextElement().toString();
					SymbolTableEntry arg = (SymbolTableEntry) ht.get(key);

					// Since we don't want other scenarios to mess up the
					// original scenario-0 entries, we make a deep copy
					SymbolTableEntry clonedEntry = new SymbolTableEntry(arg); // deep
					// copy
					htNewCopy.put(key, clonedEntry);
				}

				// First remove all the entries.
				this.taLocalSymSpace.removeEntriesUptoInclusiveLevel(0);

				// push old context;
				if (htNewCopy != null) {
					this.taLocalSymSpace.push(htNewCopy);
				}
			}
		}

	}

	public void logCurrFuncMap() {
		MultiValueMap funcMap =  Config.getInstance().getFuncKeySignatureMap();
		Set<String> keySet =funcMap.keySet(); // keys();

		logger.fatal(" Current Activity::  " + currComponentName + " \n");
		if (keySet != null) {
			for (String key : keySet) {
				Iterator itr = funcMap.iterator(key);
				while (itr.hasNext()) {
					MethodSignature ms = (MethodSignature) itr.next();
					logger.fatal(" ^^^^^^^  " + ms.getPkgClsName() + key
							+ ", paramsCount " + ms.getParams().size());

				}
			}
		}

	}

	public void setCurrCSMEvent(Event currEventParam) {

		this.currCSMEvent = currEventParam;

		if (currCSMEvent != null) {
			// logger.fatal("CSMEventRecevied: " + currEventParam.getName());
			notifyCSMObservers();
		}
	}

	public void setCurrASMEvent(Event currEventParam) {

		this.currASMEvent = currEventParam;

		if (currASMEvent != null) {
			// logger.fatal("ASMEventRecevied: " + currEventParam.getName());
			notifyASMObservers();
		}
	}
	
	public void initializeCSMs(){
		
		this.csmObservers = new ArrayList<AttackObserver>();
		
		AttackObserver abortBroadcastObserver = new AbortBroadcastObserver(this);
		this.attachCSM(abortBroadcastObserver);

		AttackObserver appRemovalObserver = new AppRemovalObserver(this);
		this.attachCSM(appRemovalObserver);

		AttackObserver audioManagerObserver = new AudioManagerObserver(this);
		this.attachCSM(audioManagerObserver);

		AttackObserver contentResolverObserver = new ContentResolverObserver(this);
		this.attachCSM(contentResolverObserver);

		AttackObserver contextObserver = new ContextObserver(this);
		this.attachCSM(contextObserver);

		AttackObserver fileReadingObserver = new FileReadingObserver(this);
		this.attachCSM(fileReadingObserver);

		AttackObserver incomingSmsAutoReplierObserver = new IncomingSmsAutoReplierObserver(this);
		this.attachCSM(incomingSmsAutoReplierObserver);

		AttackObserver informationLeakerObserver = new InformationLeakerObserver(this);
		this.attachCSM(informationLeakerObserver);

		AttackObserver informationStoringIntoDBObserver = new InformationStoringIntoDBObserver(this);
		this.attachCSM(informationStoringIntoDBObserver);

		AttackObserver intentObserver = new IntentObserver(this);
		this.attachCSM(intentObserver);

		AttackObserver keyguardManagerObserver = new KeyguardManagerObserver(this);
		this.attachCSM(keyguardManagerObserver);

		AttackObserver audioVideoRecorderObserver = new MediaRecorderObserver(this);
		this.attachCSM(audioVideoRecorderObserver);

		AttackObserver reflectionObserver = new ReflectionObserver(this);
		this.attachCSM(reflectionObserver);

		AttackObserver resetPasswordObserver = new ResetPasswordObserver(this);
		this.attachCSM(resetPasswordObserver);

		AttackObserver runtimeExecutionObserver = new RuntimeExecutionObserver(this);
		this.attachCSM(runtimeExecutionObserver);

		AttackObserver settingsTogglerObserver = new SettingsTogglerObserver(this);
		this.attachCSM(settingsTogglerObserver);

		AttackObserver premiumSmsObserver = new SmsSenderObserver(this);
		this.attachCSM(premiumSmsObserver);

		AttackObserver streamVolumeModifierObserver = new StreamVolumeModifierObserver(this);
		this.attachCSM(streamVolumeModifierObserver);

		AttackObserver uriObserver = new uriObserver(this);
		this.attachCSM(uriObserver);

		AttackObserver urlObserver = new urlObserver(this);
		this.attachCSM(urlObserver);

	}

	public void notifyCSMObservers() {
		Method method;
		long startTime = System.currentTimeMillis();

		for (AttackObserver obsr : csmObservers) {
			// The following try block invokes obsr.update(childEvent) method.
			try {
				method = obsr.getClass().getMethod("update",
						currCSMEvent.getClass());
				method.invoke(obsr, currCSMEvent);
			}catch (IllegalAccessException e) {
				e.printStackTrace();
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
			}catch (InvocationTargetException e) {
				e.printStackTrace();
			}catch (NoSuchMethodException e) {
				e.printStackTrace();
			}catch (SecurityException e) {
				e.printStackTrace();
			}			
		}
		this.currCSMEvent = null;
		long endTime = System.currentTimeMillis();
		double diff = (double) ((endTime-startTime)/1000);
		totalTimeTakenCSM +=  diff;

	}

	public void initializeASMs(){

		this.asmObservers = new ArrayList<AttackObserver>();

		AttackObserver appRemovalASMObserver = new AppRemovalASMObserver(this);
		this.attachASM(appRemovalASMObserver);

		AttackObserver audioManagerASMObserver = new AudioManagerASMObserver(this);
		this.attachASM(audioManagerASMObserver);

		AttackObserver audioVideoRecorderASMObserver = new AudioVideoRecorderASMObserver(this);
		this.attachASM(audioVideoRecorderASMObserver);

		AttackObserver contentResolverASMObserver = new ContentResolverASMObserver(this);
		this.attachASM(contentResolverASMObserver);

		AttackObserver fileReaderASMObserver = new FileReaderASMObserver(this);
		this.attachASM(fileReaderASMObserver);

		AttackObserver keyguardManagerASMObserver = new KeyguardManagerASMObserver(this);
		this.attachASM(keyguardManagerASMObserver);

		AttackObserver lockScreenPhoneCallerASMObserver = new LockScreenPhoneCallerASMObserver(this);
		this.attachASM(lockScreenPhoneCallerASMObserver);

		AttackObserver lockScreenSilentPhoneCallBlockerASMObserver = new LockScreenSilentPhoneCallBlockerASMObserver(this);
		this.attachASM(lockScreenSilentPhoneCallBlockerASMObserver);

		AttackObserver phoneCallASMObserver = new PhoneCallASMObserver(this);
		this.attachASM(phoneCallASMObserver);

		AttackObserver phoneCallBlockerASMObserver = new PhoneCallBlockerASMObserver(this);
		this.attachASM(phoneCallBlockerASMObserver);

		AttackObserver phoneCallerASMObserver = new PhoneCallerASMObserver(this);
		this.attachASM(phoneCallerASMObserver);

		AttackObserver phoneCallForwardingASMObserver = new PhoneCallForwardingASMObserver(this);
		this.attachASM(phoneCallForwardingASMObserver);

		AttackObserver ringerModeSilencerASMObserver = new RingerModeSilencerASMObserver(this);
		this.attachASM(ringerModeSilencerASMObserver);

		AttackObserver settingsTogglerASMObserver = new SettingsTogglerASMObserver(this);
		this.attachASM(settingsTogglerASMObserver);

		AttackObserver silentLockScreenPhoneCallBlockerASMObserver = new SilentLockScreenPhoneCallBlockerASMObserver(this);
		this.attachASM(silentLockScreenPhoneCallBlockerASMObserver);

		AttackObserver silentLockScreenPhoneCallerASMObserver = new SilentLockScreenPhoneCallerASMObserver(this);
		this.attachASM(silentLockScreenPhoneCallerASMObserver);

		AttackObserver silentPhoneCallBlockerASMObserver = new SilentPhoneCallBlockerASMObserver(this);
		this.attachASM(silentPhoneCallBlockerASMObserver);

		AttackObserver silentPhoneCallerASMObserver = new SilentPhoneCallerASMObserver(this);
		this.attachASM(silentPhoneCallerASMObserver);

		AttackObserver smsAutoReplyBlockerASMObserver = new SmsAutoReplyBlockerASMObserver(this);
		this.attachASM(smsAutoReplyBlockerASMObserver);

		AttackObserver smsBlockAutoReplierASMObserver = new SmsBlockAutoReplierASMObserver(this);
		this.attachASM(smsBlockAutoReplierASMObserver);

		AttackObserver smsDeleteAndSendASMObserver = new SmsDeleteAndSendASMObserver(this);
		this.attachASM(smsDeleteAndSendASMObserver);

		AttackObserver smsSendAndDeleteASMObserver = new SmsSendAndDeleteASMObserver(this);
		this.attachASM(smsSendAndDeleteASMObserver);

		AttackObserver nickiSpyCASMObserver = new NickiSpyCASMObserver(this);
		this.attachASM(nickiSpyCASMObserver);

	}
	public void notifyASMObservers() {
		Method method;

		long startTime = System.currentTimeMillis();

		for (AttackObserver obsr : asmObservers) {
			// The following try block invokes obsr.update(childEvent) method.
			try {
				method = obsr.getClass().getMethod("update",
						currASMEvent.getClass());
				method.invoke(obsr, currASMEvent);
			}catch (IllegalAccessException e) {
				e.printStackTrace();
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
			}catch (InvocationTargetException e) {
				e.printStackTrace();
			}catch (NoSuchMethodException e) {
				e.printStackTrace();
			}catch (SecurityException e) {
				e.printStackTrace();
			}			
		}
		this.currASMEvent = null;

		long endTime = System.currentTimeMillis();
		double diff = (double) ((endTime-startTime)/1000);
		totalTimeTakenASM += (double) diff;

	}

	public void attachCSM(AttackObserver pObs) {
		csmObservers.add(pObs);
	}

	public void attachASM(AttackObserver pObs) {
		asmObservers.add(pObs);
	}

	public Instruction getPrev() {
		return prev;
	}

	public void setPrev(Instruction prev) {
		this.prev = prev;
	}

	public boolean isFirstBB() {
		return firstBB;
	}

	public void setFirstBB(boolean firstBB) {
		this.firstBB = firstBB;
	}

	public ContextStack getContextStack() {
		return contextStack;
	}

	public void setContextStack(ContextStack contextStack) {
		this.contextStack = contextStack;
	}

	public InstructionResponse getIr() {
		return ir;
	}

	public void setIr(InstructionResponse ir) {
		this.ir = ir;
	}

	public Object getInstrReturnedObject() {
		return instrReturnedObject;
	}

	public void setInstrReturnedObject(Object instrReturnedObject) {
		this.instrReturnedObject = instrReturnedObject;
	}

	public Hashtable getLastBBOutSet() {
		return lastBBOutSet;
	}

	public void setLastBBOutSet(Hashtable lastBBOutSet) {
		this.lastBBOutSet = lastBBOutSet;
	}

	public SymbolSpace getGlobalSymSpace() {
		return globalSymSpace;
	}

	public void setGlobalSymSpace(SymbolSpace globalSymSpace) {
		this.globalSymSpace = globalSymSpace;
	}

	public Package getPkg() {
		return pkg;
	}

	public void setPkg(Package pkg) {
		this.pkg = pkg;
	}

	public APK getApk() {
		return apk;
	}

	public void setApk(APK apk) {
		this.apk = apk;
	}

	public String getThreadType() {
		return threadType;
	}

	public void setThreadType(String threadType) {
		this.threadType = threadType;
	}

	public Event getCurrEvent() {
		return currCSMEvent;
	}

	public String getCurrComponentName() {
		return currComponentName;
	}

	public void setCurrComponentName(String currComponentName) {
		this.currComponentName = currComponentName;
	}

	public String getCurrComponentCallback() {
		return currComponentCallback;
	}

	public void setCurrComponentCallback(String currComponentCallback) {
		this.currComponentCallback = currComponentCallback;
	}

	public String getCurrComponentPkgName() {
		return currComponentPkgName;
	}

	public void setCurrComponentPkgName(String currComponentPkgName) {
		this.currComponentPkgName = currComponentPkgName;
	}

	public static ArrayList<AttackObserver> getAsmObservers() {
		return asmObservers;
	}

	public static void setAsmObservers(ArrayList<AttackObserver> asmObservers) {
		TaintAnalyzer.asmObservers = asmObservers;
	}

	public ClassObj getCurrCls() {
		return currCls;
	}

	public void setCurrCls(ClassObj currCls) {
		this.currCls = currCls;
	}

	public CFG getCurrCFG() {
		return currCFG;
	}

	public void setCurrCFG(CFG currCFG) {
		this.currCFG = currCFG;
	}

}
