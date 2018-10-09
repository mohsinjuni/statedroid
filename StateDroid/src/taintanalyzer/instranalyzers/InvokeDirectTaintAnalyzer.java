package taintanalyzer.instranalyzers;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import models.cfg.APK;
import models.cfg.CFG;
import models.cfg.ClassObj;
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

public class InvokeDirectTaintAnalyzer extends BaseTaintAnalyzer {

	private InstructionResponse ir;
	private SymbolSpace localSymSpace;
	private boolean tainted=false;
	private String[] used ;
	private String changed;
	private TaintAnalyzer ta;
	
    private Properties apiRulesMap;
    private Properties apiDefinedAnalyzers;

    private SymbolTableEntry returnEntry;
    private Object apiReturnsObj;
	private APK apk;    
	
	public InvokeDirectTaintAnalyzer(TaintAnalyzer ta )
	{
		this.ir = ta.getIr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		this.ta = ta;
		this.returnEntry = new SymbolTableEntry();
		this.apk = ta.getApk();
		
		logger = Logger.getLogger(InvokeDirectTaintAnalyzer.class);	
	}	
	
	/*
	 * 
	 * 
	 * Invoke-vritual and invoke-direct seem to be the same but invoke-virtual resolves virtual methods before calling the actual
	 * method. I think, it means that if the method being called belongs to current class or super-class. This is confirmed again 
	 * from StackOverflow.
	 * 
	 * 
	 * 		0x0 new-instance v0, Ljava/util/Locale;
			0xc invoke-direct v0, v1, v2, Ljava/util/Locale;-><init>(Ljava/lang/String; Ljava/lang/String;)V
			
			0x78 invoke-direct v8, Lcom/google/analytics/tracking/android/GAThread;->generateClientId()Ljava/lang/String;
			0x7e move-result-object v4
		
	 * 
	 * 	First register is not always pointing to 'this' variable. It may refer to something else also.
	 * 
	 * 
	 * Edit1: Invoke-direct is used mostly (95%) for constructors. This means that the caller object stores the newly created
	 * object. Even if <init> method returns nothing, new object is created and stored in the caller object.
	 * 
	 * Other than <init> methods, it calls only private methods of different classes. 
	 * 
	 * Case#1  And in those cases, if the method returns nothing, invoke-direct returns nothing. For example,
	 * 
	 * 0x4 invoke-direct v2, v3, v4, v0, v1, Landroid/support/v4/app/BackStackRecord;->doAddOp(I Landroid/support/v4/app/Fragment; Ljava/lang/String; I)V
	 * 0x14 invoke-direct v0, Landroid/support/v4/app/FragmentTabHost;->ensureContent()V
	 * 
	 * Here are the definitions: 
	 * 
	 * "private void doAddOp(int containerViewId, Fragment fragment, String tag, int opcmd)"
	 *  private void More ...ensureContent() 
	 * 
	 * Case#2: When private method returns something, it also returns.
	 * 
	 * 0xb0 invoke-direct v6, v0, v1, Landroid/support/v4/app/FragmentTabHost;->doTabChanged(Ljava/lang/String; 
	       Landroid/support/v4/app/FragmentTransaction;)Landroid/support/v4/app/FragmentTransaction;
	       
	 * 0x0 invoke-direct v1, v2, Landroid/support/v4/content/ModernAsyncTask;->postResult(Ljava/lang/Object;)Ljava/lang/Object;
	 *  
	 *  
	 *  private FragmentTransaction doTabChanged(String tabId, FragmentTransaction ft);
	 *  private Result postResult(Result result) 
	 * 
	 * (non-Javadoc)
	 * @see taintanalyzer.BaseTaintAnalyzer#analyzeInstruction()
	 */
	
	public Object analyzeInstruction()
	{
		String qualifiedApiName = ir.getQualifiedAPIName();
		Properties sourceSinkAPIMap = Config.getInstance().getSourceSinkAPIMap();
		
		
		String instrText = ir.getInstr().getText();
//		if(instrText.contains("0x0 invoke-direct v0, Lcom/google/android/v54new/service/CallService;->closePhone")){
//			System.out.println(instrText);
//		}

		if(qualifiedApiName!= null && sourceSinkAPIMap.containsKey(qualifiedApiName))
		{
			String apiType = sourceSinkAPIMap.getProperty(qualifiedApiName).toString().trim();
			if(apiType.equalsIgnoreCase("source"))
			{
				ir.setSourceAPI(true);
			}
			else if(apiType.equalsIgnoreCase("sink"))
			{
				ir.setSinkAPI(true);
			}
		}
		
	 	   used = ir.getUsedRegisters();
	 	   String calledMethodNature = ir.getCalledMethodNature();
	 	    EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
			String pkgClsName = ir.getCallerAPIName();
			String methdObjectName = ir.getMethodOrObjectName();
			String qualifiedAPIName = pkgClsName.concat("->").concat(methdObjectName);
 			apiRulesMap = Config.getInstance().getApiRulesMap();
 		    apiDefinedAnalyzers = Config.getInstance().getApiDefinedAnalyzers();
 		    
 			if(apiDefinedAnalyzers.containsKey(qualifiedAPIName))
 			{
	 				String analyzer = apiDefinedAnalyzers.getProperty(qualifiedAPIName);

	 				pkgClsName = pkgClsName.substring(1, pkgClsName.length()-1);
	 				pkgClsName = pkgClsName.replace('/', '.');
	 				pkgClsName = pkgClsName.concat(".");
	 						
 					String completeAnalyzerName = new StringBuilder("apihandlers.").append(pkgClsName).append(analyzer).toString(); 

 					logger.debug("InvokeDirectTaintAnalyzer.java -> completeAnalyzerName " + completeAnalyzerName);
 					Class cls = null;
 					BaseTaintAnalyzer baseTaintAnalyzer;
 					
 					try {
 						cls = Class.forName(completeAnalyzerName);
//	 						baseTaintAnalyzer = (BaseTaintAnalyzer) cls.getDeclaredConstructor(new Class[] {InstructionResponse.class, SymbolTable.class }).newInstance(ir, localSymSpace);
 						baseTaintAnalyzer = (BaseTaintAnalyzer) cls.getDeclaredConstructor(new Class[] {TaintAnalyzer.class }).newInstance(ta);
 						setApiReturnsObj( baseTaintAnalyzer.analyzeInstruction());
 						
// 						System.out.println("In "+ ins.getCurrMethodName() + ", After ==>" + ins.getText() );

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
					}

	 			}
	 			else if(apiRulesMap.containsKey(qualifiedAPIName))
	 			{
	 				String key = apiRulesMap.getProperty(qualifiedAPIName);
	 				
	 				if(key.equalsIgnoreCase(API_TYPES.dumbAPI.toString()))
	 				{
	 					return returnEntry;
	 				}
	 				else if(key.equalsIgnoreCase(API_TYPES.storeVariable.toString() ))
	 				{
	 					InvokeDirectDefaultTaintAnalyzer dfTA = new InvokeDirectDefaultTaintAnalyzer(ta);
	 					return (SymbolTableEntry) dfTA.analyzeInstruction();
	 				}
	 				else if (key.equalsIgnoreCase(API_TYPES.taintPropagater.toString() ))
	 				{
	 					InvokeDirectDefaultTaintAnalyzer dfTA = new InvokeDirectDefaultTaintAnalyzer(ta);
	 					return (SymbolTableEntry) dfTA.analyzeInstruction();
	 				}
	 				else if (key.equalsIgnoreCase(API_TYPES.commonInitializer.toString() ))
	 				{

	 				}

	 			}
	 			else if (apiRulesMap.containsKey(methdObjectName))
	 			{
	 				return returnEntry;
	 			}
	 			//<init> situation is handled by else condition under this if condition.
 				else if (ir.isSourceAPI())
 				{
 					String apiInfo = "";
			 		  Instruction instr = ir.getInstr();
			 		  apiInfo = String.valueOf(" [SrcPkgClass] = ").concat(instr.getCurrPkgClassName()
 			 				 .concat (" , [SrcMethod] = ").concat(instr.getCurrMethodName()) );
 			 		 logger.error("This is a source API" + apiInfo + ", [api] = " + instr.getText());
 			 		returnEntryDetails.setTainted(true);
 			 		returnEntryDetails.setConstant(false);
 			 		returnEntryDetails.setField(false);
 			 		returnEntryDetails.setRecord(false);
 			 		
 			 	   SourceInfo si = new SourceInfo();
 			 	   si.setSrcAPI(qualifiedAPIName);
 			 	   si.setSrcInstr(ir.getInstr().getText());
 			 	   
 			 	   ArrayList<SourceInfo> siList = returnEntryDetails.getSourceInfoList();
 			 	   if(siList == null)
 			 		   siList = new ArrayList<SourceInfo>();
 			 	   
 			 	   if(!siList.contains(si))
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
		 		   
		 		   if(involvedRegisters != null)
		 		   {
		 		       for(int i=0; i<involvedRegisters.size(); i++) 
		 		       {
		 		    	   String regName = involvedRegisters.get(i).getName();
		 		           entry= localSymSpace.find(regName);
		 		           if(entry != null){
		    		           if (entry.getEntryDetails().isTainted()) {
		    		              tainted = true;
		    		              
		    		              ArrayList<SourceInfo> entrySIList = entry.getEntryDetails().getSourceInfoList();
		    		              
		    		              if(entrySIList != null && entrySIList.size() > 0)
		    		              {
			    		              for(SourceInfo si : entrySIList)
			    		              {
			    		            	  if(!srcAPIList.contains(si)) 
			    		            		  srcAPIList.add(si);
			    		              }
		    		              }
// 			    		          // if any of source/used registers is tainted, destination register will get tainted.
		    		           }
		 		           }
		 		        }
		    		    if(tainted)
		 		        {
		 		    	    tainted = false;

		 			 		EventFactory.getInstance().registerEvent("InformationLeakageEvent", new InformationLeakerASMEvent());
		 					
		 					Event event = EventFactory.getInstance().createEvent("InformationLeakageEvent");
		 				
		 					event.setCurrMethodName(instr.getCurrMethodName());
		 					event.setCurrPkgClsName(instr.getCurrPkgClassName());
		 					event.setName("InformationLeakageEvent");
		 					
		 					event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
		 					event.setCurrComponentName(ta.getCurrComponentName());
		 					event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
		 					event.setCurrComponentType(ta.getCurrCls().getType());
		 					//Set the component type here, as well.
		 					
		 					event.getEventInfo().put("sources", srcAPIList);
		 					event.getEventInfo().put("instrText", instr.getText());
		 					
		 					ta.setCurrCSMEvent(event);
		 					
		 		        }
		    		    
		    		    //We will put customized checks here. For example, if sendTextMessage is being called and first argument is a constant.
		    		    
		    		    String calledAPIName = ir.getCallerAPIName();
		    		    String calledObjctMethodName = ir.getMethodOrObjectName();
		    		    String str = calledAPIName.concat("->").concat(calledObjctMethodName);
		    		    
		    		    if( str.equalsIgnoreCase("Landroid/telephony/gsm/SmsManager;->sendTextMessage") 
		    		    		|| str.equalsIgnoreCase("Landroid/telephony/SmsManager;->sendTextMessage") 
		    		    		|| str.equalsIgnoreCase("Lcom/android/internal/telephony/SmsManager;->sendSms") 
		    		    		|| str.equalsIgnoreCase("Landroid/telephony/SmsManager;->sendDataMessage") 
		    		    		|| str.equalsIgnoreCase("Landroid/telephony/gsm/SmsManager;->sendDataMessage") 
		    		    		|| str.equalsIgnoreCase("Landroid/telephony/SmsManager;->sendMultipartTextMessage") 
		    		    		|| str.equalsIgnoreCase("Landroid/telephony/gsm/SmsManager;->sendMultipartTextMessage") 
		    		    		) 
    		    		{
 			    		    	
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
		 					event.setCurrComponentType(ta.getCurrCls().getType());
		 					
		 					event.getEventInfo().put("instrText", instr.getText());
		 					event.getEventInfo().put("recipientNoEntry", recipientNoEntry);
		 					event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);

		 					ta.setCurrCSMEvent(event);


	    		        }
   		    		}
	 		    	logger.error(" \n\n **** Sink API => " + ir.getInstr().getText() + "\n\n");
 					InvokeDirectDefaultTaintAnalyzer dfTA = new InvokeDirectDefaultTaintAnalyzer(ta);
 					return (SymbolTableEntry) dfTA.analyzeInstruction();
 				}
	 		   //#Pre-defined method names that are invoked by user-defined caller APIs. i.e. a/b/c/myReceiver;->abortBroadcast()
 				else if(apiDefinedAnalyzers.containsKey(methdObjectName)) 
	 		    {	 			   
	 				String analyzer = apiDefinedAnalyzers.getProperty(methdObjectName);

	 				String modifiedpkgClsName = pkgClsName.substring(1, pkgClsName.length()-1);
	 				modifiedpkgClsName = modifiedpkgClsName.replace('/', '.').concat(".");
	 						
 					String completeAnalyzerName = new StringBuilder("apihandlers.userdefined.").append(analyzer).toString(); 

 					logger.debug("InvokeTaintAnalyzer.java -> completeAnalyzerName " + completeAnalyzerName);
 					Class cls = null;
 					BaseTaintAnalyzer baseTaintAnalyzer;
 					
 					try {
 						cls = Class.forName(completeAnalyzerName);
//	 						baseTaintAnalyzer = (BaseTaintAnalyzer) cls.getDeclaredConstructor(new Class[] {InstructionResponse.class, SymbolTable.class }).newInstance(ir, localSymSpace);
 						baseTaintAnalyzer = (BaseTaintAnalyzer) cls.getDeclaredConstructor(new Class[] {TaintAnalyzer.class }).newInstance(ta);
 						setApiReturnsObj( baseTaintAnalyzer.analyzeInstruction());
 						
// 						logger.debug("\n InvokeTaintAnalyzer");
 				       localSymSpace.logInfoSymbolSpace();
 				       
 				       return apiReturnsObj;
 						
// 						System.out.println("In "+ ins.getCurrMethodName() + ", After ==>" + ins.getText() );

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
					}
	 		   }
	 		   else
	 		   {
	 			   
	 			   Hashtable blackListedAPIs = Config.getInstance().getBlackListedAPIs();
	 			   
	 			   Enumeration<String> keys = blackListedAPIs.keys();
	 			   boolean isBlackListedApiCall = false;
	 			   
	 			   while(keys.hasMoreElements())
	 			   {
	 				   String key = keys.nextElement();
	 				   
	 				   if(pkgClsName.startsWith(key))
	 				   {
	 					  isBlackListedApiCall = true;
	 					  break;
	 				   }
	 			   }
	 			   if(isBlackListedApiCall)
	 			   {
	 					InvokeVirtualDefaultTaintAnalyzer dfTA = new InvokeVirtualDefaultTaintAnalyzer(ta);
	 					return (SymbolTableEntry) dfTA.analyzeInstruction();
	 			   }
	 			   else
	 			   {
	 				   
   			 		   MethodHandler mHandler = new MethodHandler(ta);
			 		   MethodSignature ms = mHandler.getMethodSignatureFromCurrInstruction(ir);

			 		   //find parent-child relationship for next-else condition.
			 		   if(methdObjectName.equalsIgnoreCase("<init>")){
			 			   Register reg0 = ir.getInvolvedRegisters().get(0);
			 			   SymbolTableEntry entry = this.localSymSpace.find(reg0.getName());
			 			   if(entry != null){
			 				   String type = entry.getEntryDetails().getType();
			 				   
			 				   ClassObj currCls =  apk.findClassByKey(type);
			 				   if(currCls != null){
			 					   String parent = currCls.getParentName();
			 					   String callerAPIName = ir.getCallerAPIName();
			 					   if(parent.trim().equalsIgnoreCase(callerAPIName.trim())){
			 						   return null;
			 					   }
			 				   }
			 			   }
			 		   }
			 			CFG cfg = apk.findMethodBySignature(ms);
			 			
			 			if(cfg != null){
			 			   boolean result = mHandler.handleMethodCall(cfg);
			 			   String cfgKey = cfg.getKey();
			 			   cfg.nullifyBBOutSets();		   
			 			   
	   		 			   if( (cfgKey.equalsIgnoreCase("<init>")) 
		 					  || (cfgKey.equalsIgnoreCase("init"))
		 					  )
	   		 			   {
			 				   //Check for <clinit> also, since there is no explicit call to such static initialization method in the code.
			 				   String currClassKey = cfg.getCurrPkgClassName();
			 				   ClassObj cls = apk.findClassByKey(currClassKey);
			 				   if(cls != null){
			 					   CFG clinitCFG = (CFG) cls.findCFGByKey("<clinit>");
			 					   if(clinitCFG != null){
			 						   	mHandler.handleMethodCall(clinitCFG);
			 						   	clinitCFG.nullifyBBOutSets();
			 					   }
			 				   }
			 			   }
			 			   if(result)
			 			   {
				 			   Object obj = ta.getInstrReturnedObject();
				 			   if(null != obj)
				 			   {
			 	 				   SymbolTableEntry entry = null;
			 	 				   if(obj instanceof SymbolTableEntry){
			 	 				    entry = (SymbolTableEntry) obj;
			 	 				   }else if( obj instanceof InstructionReturnValue){
			 	 					   InstructionReturnValue returnObj = (InstructionReturnValue) obj;
			 	 					   entry = returnObj.getReturnEntry();
			 	 				   }
				 			       logger.debug("\n InvokeDirectTaintAnalyzer");

				 			       return entry;  //move instruction will set it into symbolTable.
				 			   }
			 			   }
			 			   return null;
		 			   } 
			 			else{
			 				
			 			
				 		   String callerAPI = ir.getCallerAPIName();
	 					   String callerAPI2 = callerAPI;
	 					   
	 					   if(callerAPI2.endsWith(";"))
	 						   callerAPI2 = callerAPI2.substring(0, callerAPI2.length()-1);
	 					   
	 					   ClassObj callerAPIClass = (ClassObj) apk.findClassByKey(callerAPI2);
	 					   
		 				   ArrayList<Register> allRegs = ir.getInvolvedRegisters();
		 				   Register runnableReg = null;
		 				   String runnableParamType="";
		 				   boolean isThereRunnableObject = false;
		 				   //TODO: These if-else conditions demand a better generic approach to handle all cases.
		 				   //check if any of input parameters or receiver object is runnable.
		 				   
		 				   // invoke-interface v5, Ljava/lang/Runnable;->run()V
		 				   // 0x36 invoke-direct v0, v4, v1, Ljava/lang/Thread;-><init>(Ljava/lang/Runnable; Ljava/lang/String;)V
		 				   
		 				   
		 				   for(int i=0; i< allRegs.size(); i++){
		 					   runnableReg = allRegs.get(i);
		 				   	   runnableParamType = runnableReg.getType();
		 				   	   if(runnableParamType.trim().equalsIgnoreCase("Ljava/lang/Runnable;")){
		 				   		   isThereRunnableObject = true;
		 				   		   break;
		 				   	   }
		 				   }
		 				   logger.debug("Looking for methodss");
		 				   
		 				   //As soon as it sees a runnable object, DT runs its run() method.
		 				   if(isThereRunnableObject)
		 				   {
		 					   //As soon as we get Runnable object as a parameter, we immediately calls its run() method. 
	
	//	Line 54394: 		0x74 invoke-virtual v1, v0, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
	//	Line 57641: 		0x4 invoke-virtual v1, v2, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z
		 					   
		 					   instrText = ir.getInstr().getText();
		 					   SymbolTableEntry callerEntry = this.localSymSpace.find(runnableReg.getName());
		 					   
		 					  // SymbolTableEntry listnerAPIEntry = this.localSymSpace.find(listenerAPIReg.getName().trim());
		 					   
		 					   if(callerEntry != null)
		 					   {
				 				   logger.debug("Handling Runnable instances");
	
			 					   String threadType = callerEntry.getEntryDetails().getType();
			 					   ClassObj thread = apk.findClassByKey(threadType);
			 					   
			 					   if(thread != null){
				 					   CFG runMethod = (CFG) thread.findCFGByKey("run");
		 				 			   
		 				 			   if(runMethod != null)
		 				 			   {
		 				 				   logger.debug("cfg key -> " + runMethod.getKey());
		 					 			   boolean result = mHandler.handleMethodCall(runMethod);
		 					 			   runMethod.nullifyBBOutSets();
		 					 			   
		 					 			  
			 				 		   }
			 					   }
		 					   }
		 					    return null;
		 				   }
		 				   else if(ir.getMethodOrObjectName().equalsIgnoreCase("start"))
		 				   {
		 					   String instrTxt = ir.getInstr().getText();
		 					   String instrSplitWithArrow[] = instrTxt.split("->");
		 					   String arrowRightSideSplit[] = instrSplitWithArrow[1].split("[(]");
		 					   
//		 				           String newInstrTxt = instrSplitWithArrow[0].concat("->run(").concat(arrowRightSideSplit[1]);
			           
	 				    	   MethodSignature newMS = mHandler.getMethodSignatureFromCurrInstruction(ir);
	 				    	   
	 				    	   newMS.setName("run");
	 				    	   
	 				 		   if(newMS != null)
	 				 		   {
	 				 			   CFG newCFG = apk.findMethodBySignature(newMS);
	 				 			   
	 				 			   if(newCFG != null)
	 				 			   {
	 				 				   
	 				 				   logger.debug("cfg key -> " + newCFG.getKey());
	 				 				   logger.trace("[InvokeTaintAnalyzer] from caller instr:: " + newMS.getParams().size());
	 				 				   logger.trace("[InvokeTaintAnalyzer] from apk found cfg:: " + newCFG.getSignature().getParams().size());
	
	 					 			   boolean result = mHandler.handleMethodCall(newCFG);
	 					 			   
	 					 			   newCFG.nullifyBBOutSets();
	 						 			  
	 					 			   if(result)
	 					 			   {
	 						 			   Object obj = ta.getInstrReturnedObject();
	 						 			   
	 						 			   if(null != obj)
	 						 			   {
						 	 				   SymbolTableEntry entry = null;
						 	 				   if(obj instanceof SymbolTableEntry){
						 	 				    entry = (SymbolTableEntry) obj;
						 	 				   }else if( obj instanceof InstructionReturnValue){
						 	 					   InstructionReturnValue returnObj = (InstructionReturnValue) obj;
						 	 					   entry = returnObj.getReturnEntry();
						 	 				   }
	 						 				   
	 						 			       logger.debug("\n InvokeTaintAnalyzer");
	 						 			       localSymSpace.logInfoSymbolSpace();
	
	 						 			       logger.debug("\n </end> Global Entry");
	 						 			       Config.getInstance().getGlobalSymbolSpace().logInfoSymbolSpace();
	 						 			       
	 						 			       return entry;
	 						 			   }
	 					 			   }
	 					 			   return null;
	 				 			   }
	 				 		   }
		 				   }
		 				   else{
					 		   //Default is default-handler.
			 					InvokeDirectDefaultTaintAnalyzer dfTA = new InvokeDirectDefaultTaintAnalyzer(ta);
			 					return (SymbolTableEntry) dfTA.analyzeInstruction();
		 				   }
			 			}
	 			   }
 			   }
	 	   logger.debug("\n InvokeDirectTaintAnalyzer");
	       localSymSpace.logInfoSymbolSpace();
	 	  return null;
	}
	public Object getApiReturnsObj() {
		return apiReturnsObj;
	}

	public void setApiReturnsObj(Object apiReturnsObj) {
		this.apiReturnsObj = apiReturnsObj;
	} 	   
}