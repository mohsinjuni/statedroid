package patternMatcher.statemachines.csm.incomingsmsautoreplier.states;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.IncomingSmsAutoReplierASMEvent;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.events.csm.SmsSenderEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.incomingsmsautoreplier.IncomingSmsAutoReplierStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.ComponentTypes;

public class InitialState extends IncomingSmsAutoReplierStates {

	private String currInstr = "";
	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(SmsSenderEvent e) {

		AndroidManifest am = Config.getInstance().getAndroidManifest();
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(InitialState.class);

		String currPkgClsInfo = e.getCurrPkgClsName();
		ComponentManifest compInfo = am.findComponentManifest(currPkgClsInfo);
		ArrayList<String> permList = am.getUsedPermStrList();

		SymbolTableEntry recpEntry = (SymbolTableEntry) e.getEventInfo().get("recipientNoEntry");

		if (recpEntry != null && compInfo != null) {
			if (compInfo.getType().equalsIgnoreCase(ComponentTypes.broadcastReceiver.toString())
					&& (ta.getCurrComponentCallback().equalsIgnoreCase("onReceive"))
			) {

				String value = recpEntry.getEntryDetails().getValue();
				if (value != null && (value.contains("getDisplayOriginatingAddress") || value.contains("getOriginatingAddress"))) {

					Hashtable eventInfo = e.getEventInfo();
					ArrayList<String> currCFGPermutation = Config.getInstance().getCurrCFGPermutation();
					String instr = (String) eventInfo.get("instrText");
					String info1 = instr;

					String permStr = Config.getInstance().getCurrCFGPermutationString();

					GenericReport rep = new GenericReport();
					rep.setInstrContainerCls(e.getCurrPkgClsName());
					rep.setInstContainerMthd(e.getCurrMethodName());
					rep.setCompPkgName(ta.getCurrComponentName());
					rep.setCompCallbackMethdName(ta.getCurrComponentCallback());

					rep.setCurrComponentClsName(ta.getCurrComponentPkgName());
					rep.setSinkAPI(info1);
					rep.setPermutationStr(permStr);

					rep.setMessage(" ##### This app can send auto-reply to incoming SMS messages  ######");

					Stack<MethodSignature> funcCallStack = Config.getInstance().getFuncCallStack();
					Stack<MethodSignature> funcCallStackCopy = new Stack<MethodSignature>();

					//Default stack.clone() provides shallow-copy, so I will make deep-copy on my own.
					for (int i = 0; i < funcCallStack.size(); i++) {
						MethodSignature oldMS = funcCallStack.get(i);
						MethodSignature newMS = new MethodSignature(oldMS);
						funcCallStackCopy.add(newMS);
					}
					rep.setFunctionCallStack(funcCallStackCopy);

					if (!AttackReporter.getInstance().checkIfIncomingSmsAutoReplyReportExists(rep)) {
						AttackReporter.getInstance().getIncomingSMSAutoReplyReportList().add(rep);
						rep.printReport();
					}
					generateAndSendASMEvent();
				}
			}
		}
		return this;
	}

	public void generateAndSendASMEvent() {
		EventFactory.getInstance().registerEvent("incomingSmsAutoReplierASMEvent", new IncomingSmsAutoReplierASMEvent());
		Event event = EventFactory.getInstance().createEvent("incomingSmsAutoReplierASMEvent");
		event.setName("incomingSmsAutoReplierASMEvent");

		event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
		event.setCurrComponentName(ta.getCurrComponentName());
		event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
		//		event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);

		ta.setCurrASMEvent(event);
	}
}
