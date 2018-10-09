package patternMatcher.statemachines.csm.resetpassword.states;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;

import models.cfg.MethodSignature;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.manifest.IntentFilter;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.LockNowEvent;
import patternMatcher.events.csm.ResetPasswordEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.abortbroadcast.AbortBroadcastStates;
import patternMatcher.statemachines.csm.context.states.DevicePolicyManagerDefinedState;
import patternMatcher.statemachines.csm.resetpassword.ResetPasswordStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;


public class InitialState extends ResetPasswordStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public InitialState(TaintAnalyzer taParam){
		this.ta = taParam;
	}
	
	public InitialState(){}
	
	@Override
	public State update(ResetPasswordEvent e) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("Initial State");
		
		Hashtable eventInfo = e.getEventInfo();
		if(eventInfo != null && eventInfo.containsKey("dpmEntry")){
			SymbolTableEntry dpmEntry = (SymbolTableEntry) eventInfo.get("dpmEntry");
			if(dpmEntry != null && dpmEntry.getEntryDetails().getState() instanceof DevicePolicyManagerDefinedState){
				String instr = (String) eventInfo.get("instrText");
				String info1 = instr;
				String permStr = Config.getInstance().getCurrCFGPermutationString();
	
				GenericReport rep = new GenericReport();
				rep.setInstrContainerCls(e.getCurrPkgClsName());
				rep.setInstContainerMthd(e.getCurrMethodName());
				rep.setCompPkgName(ta.getCurrComponentPkgName());
				rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
				rep.setCurrComponentClsName(ta.getCurrComponentName());
		 		rep.setSinkAPI(info1);
		 		rep.setPermutationStr(permStr);
	 		
		 		Stack<MethodSignature> funcCallStack = Config.getInstance().getFuncCallStack();
		 		Stack<MethodSignature> funcCallStackCopy = new Stack<MethodSignature>();
		 		
		 		//Default stack.clone() provides shallow-copy, so I will make deep-copy on my own.
		 		for(int i=0; i< funcCallStack.size();i++){
		 			MethodSignature oldMS = funcCallStack.get(i);
		 			MethodSignature newMS = new MethodSignature(oldMS);
		 			funcCallStackCopy.add(newMS);
		 		}
		 	    rep.setFunctionCallStack(funcCallStackCopy);
		 	    String password = "";
				Hashtable recordFieldList = dpmEntry.getEntryDetails().getRecordFieldList();
				if(recordFieldList != null && recordFieldList.containsKey("password")){
					SymbolTableEntry passwordEntry = (SymbolTableEntry) eventInfo.get("password");
					if(passwordEntry != null && !passwordEntry.getEntryDetails().getValue().isEmpty()){
						password = passwordEntry.getEntryDetails().getValue();
					}
				}
				if(password.isEmpty()){
					rep.setMessage("This app can reset device's password programmatically.");
				}else{
					rep.setMessage("This app can programmatically reset device's password with new password = " + password);
				}
	 	    
		 		if(!AttackReporter.getInstance().checkIfResetPasswordReportExists(rep)){
		 			AttackReporter.getInstance().getResetPasswordReportList().add(rep);
		 			rep.printReport();
		 		}
			}
		}
		return this;  //This does not matter anymore. Each object maintains its state itself.
	}

		@Override
	public State update(LockNowEvent e) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("Initial State");
		
		Hashtable eventInfo = e.getEventInfo();
		if(eventInfo != null && eventInfo.containsKey("dpmEntry")){
			SymbolTableEntry dpmEntry = (SymbolTableEntry) eventInfo.get("dpmEntry");
			if(dpmEntry != null && dpmEntry.getEntryDetails().getState() instanceof DevicePolicyManagerDefinedState){
				String instr = (String) eventInfo.get("instrText");
				String info1 = instr;
				String permStr = Config.getInstance().getCurrCFGPermutationString();
	
				GenericReport rep = new GenericReport();
				rep.setInstrContainerCls(e.getCurrPkgClsName());
				rep.setInstContainerMthd(e.getCurrMethodName());
				rep.setCompPkgName(ta.getCurrComponentPkgName());
				rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
				rep.setCurrComponentClsName(ta.getCurrComponentName());
		 		rep.setSinkAPI(info1);
		 		rep.setPermutationStr(permStr);
	 		
		 		Stack<MethodSignature> funcCallStack = Config.getInstance().getFuncCallStack();
		 		Stack<MethodSignature> funcCallStackCopy = new Stack<MethodSignature>();
		 		
		 		//Default stack.clone() provides shallow-copy, so I will make deep-copy on my own.
		 		for(int i=0; i< funcCallStack.size();i++){
		 			MethodSignature oldMS = funcCallStack.get(i);
		 			MethodSignature newMS = new MethodSignature(oldMS);
		 			funcCallStackCopy.add(newMS);
		 		}
		 	    rep.setFunctionCallStack(funcCallStackCopy);
				rep.setMessage("CSM: This app can lock device programmatically.");
	 	    
		 		if(!AttackReporter.getInstance().checkIfLockNowDeviceReportExists(rep)){
		 			AttackReporter.getInstance().getLockNowDevieReportList().add(rep);
		 			rep.printReport();
		 		}
			}
		}
		return this;  //This does not matter anymore. Each object maintains its state itself.
	}

}
