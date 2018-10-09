package patternMatcher.statemachines.csm.abortbroadcast.states;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;

import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.manifest.IntentFilter;
import models.symboltable.SymbolSpace;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.IncomingSmsAutoReplierASMEvent;
import patternMatcher.events.asm.IncomingSmsBlockerASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.abortbroadcast.AbortBroadcastStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;

public class InitialState extends AbortBroadcastStates {

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	//StateMachine_AutoReply4SMS contains the test

	@Override
	public State update(AbortBroadcastEvent e) {
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("Initial State");

		AndroidManifest am = Config.getInstance().getAndroidManifest();
		ArrayList<String> permList = am.getUsedPermStrList();
		String currPkgClsInfo = e.getCurrPkgClsName();
		ComponentManifest compInfo = am.findComponentManifest(currPkgClsInfo);

		if (compInfo != null && compInfo.getType().equalsIgnoreCase(ComponentTypes.broadcastReceiver.toString())
				&& (ta.getCurrComponentCallback().equalsIgnoreCase("onReceive"))
		//	&& ( permList.contains("android.permission.RECEIVE_SMS"))
		) {

			ArrayList<IntentFilter> compIntFilters = compInfo.getIntentFilters();
			HashSet<String> allActions = new HashSet<String>();
			for (IntentFilter intF : compIntFilters) {
				for (String act : intF.getActionList()) {
					allActions.add(act);
				}
			}
			//StateMachine_AutoReply4SMS contains the test
			Hashtable eventInfo = e.getEventInfo();
			ArrayList<String> currCFGPermutation = (ArrayList<String>) eventInfo.get("currCFGPermutation");
			String instr = (String) eventInfo.get("instrText");
			InstructionResponse ir = (InstructionResponse) eventInfo.get("instrResponse");

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
			for (int i = 0; i < funcCallStack.size(); i++) {
				MethodSignature oldMS = funcCallStack.get(i);
				MethodSignature newMS = new MethodSignature(oldMS);
				funcCallStackCopy.add(newMS);
			}
			rep.setFunctionCallStack(funcCallStackCopy);

			boolean isSMS = true;
			if (allActions.contains("android.provider.Telephony.SMS_RECEIVED") && allActions.contains("android.intent.action.PHONE_STATE")) {
				rep.setMessage(" ##### This app can abort SMS and CALL broadcast notification");
			} else if (allActions.contains("android.provider.Telephony.SMS_RECEIVED")) {
				rep.setMessage(" ##### This app can abort SMS broadcast notification");
			} else if (allActions.contains("android.intent.action.PHONE_STATE")) {
				rep.setMessage(" ##### This app can abort CALL broadcast notification");
				isSMS = false;
			} else {
				rep.setMessage(" ##### This app can abort SMS/CALL broadcast notification");
			}
			if (!AttackReporter.getInstance().checkIfAbortBroadcastReporttExists(rep)) {
				AttackReporter.getInstance().getAbortBroadcastReportList().add(rep);
				rep.printReport();
			}
			if (isSMS) {
				generateAndSendASMEvent(ir);
			}
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}

	public void generateAndSendASMEvent(InstructionResponse ir) {
		EventFactory.getInstance().registerEvent("incomingSmsBlockerASMEvent", new IncomingSmsBlockerASMEvent());
		Event event = EventFactory.getInstance().createEvent("incomingSmsBlockerASMEvent");
		event.setName("incomingSmsBlockerASMEvent");

		event.setCurrMethodName(ir.getInstr().getCurrMethodName());
		event.setCurrPkgClsName(ir.getInstr().getCurrPkgClassName());

		event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
		event.setCurrComponentName(ta.getCurrComponentName());
		event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
		event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);

		ta.setCurrASMEvent(event);
	}
}
