package patternMatcher.statemachines.csm.context.states;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.attackreporter.PhoneCallForwardingReport;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.DisplayHomeScreenASMEvent;
import patternMatcher.events.asm.ShowCallScreenWithDialpadASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallForwardingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.events.csm.GetManagerEvent;
import patternMatcher.events.csm.GetPackageManagerDefinedEvent;
import patternMatcher.events.csm.GetPackageNameDefinedEvent;
import patternMatcher.events.csm.GetSystemServiceEvent;
import patternMatcher.events.csm.context.StartActivityIntentEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.audiomanager.states.AudioManagerDefinedState;
import patternMatcher.statemachines.csm.context.ContextStates;
import patternMatcher.statemachines.csm.intent.states.IntentActionCallUriCallFrwrdingDefinedState;
import patternMatcher.statemachines.csm.intent.states.IntentActionCallUriTellDefinedState;
import patternMatcher.statemachines.csm.intent.states.IntentActionMainCatHomeDefinedState;
import patternMatcher.statemachines.csm.keyguardmanager.states.KeyguardManagerDefinedState;
import patternMatcher.statemachines.csm.reflection.states.ConnectivityManagerDefinedState;
import patternMatcher.statemachines.csm.reflection.states.TelephonyManagerDefinedState;
import patternMatcher.statemachines.csm.settingstoggler.states.WifiManagerDefinedState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends ContextStates {

	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	private String currInstr = "";

	//	0x1d2 invoke-virtual v0, v8, Lcom/example/phonecaller/MainActivity;->startActivity(Landroid/content/Intent;)V

	@Override
	public State update(StartActivityIntentEvent e) {
		SymbolTableEntry intentEntry = (SymbolTableEntry) e.getEventInfo().get("intent");

		if (intentEntry != null) {
			State intentState = intentEntry.getEntryDetails().getState();
			if (intentState != null && intentState instanceof IntentActionCallUriTellDefinedState) {
				Hashtable recordFieldList = intentEntry.getEntryDetails().getRecordFieldList();
				if (recordFieldList != null) {
					SymbolTableEntry uriEntry = (SymbolTableEntry) recordFieldList.get("uriEntry");

					if (uriEntry != null) {
						String phoneNo = uriEntry.getEntryDetails().getValue();

						EventFactory.getInstance().registerEvent("phoneCallerEvent", new PhoneCallingASMEvent());

						Event phoneCallerEvent = EventFactory.getInstance().createEvent("phoneCallerEvent");
						phoneCallerEvent.setName("phoneCallerEvent");

						phoneCallerEvent.setCurrComponentName(e.getCurrComponentName());

						phoneCallerEvent.setCurrPkgClsName(e.getCurrPkgClsName());
						phoneCallerEvent.setCurrMethodName(e.getCurrMethodName());
						phoneCallerEvent.setCurrComponentPkgName(e.getCurrComponentPkgName());
						phoneCallerEvent.setCurrCompCallbackMethodName(e.getCurrCompCallbackMethodName());

						phoneCallerEvent.getEventInfo().put("phoneNo", phoneNo);
						phoneCallerEvent.getEventInfo().put(InstructionResponse.CLASS_NAME,
								(InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME));

						ta.setCurrASMEvent(phoneCallerEvent);
					}
				}
			} else if (intentState != null && intentState instanceof IntentActionCallUriCallFrwrdingDefinedState) {
				Hashtable recordFieldList = intentEntry.getEntryDetails().getRecordFieldList();
				if (recordFieldList != null) {
					SymbolTableEntry uriEntry = (SymbolTableEntry) recordFieldList.get("uriEntry");
					if (uriEntry != null) {
						String callForwardingCode = uriEntry.getEntryDetails().getValue();
						EventFactory.getInstance().registerEvent("phoneCallForwardingASMEvent", new PhoneCallForwardingASMEvent());

						Event phoneCallForwardingASMEvent = EventFactory.getInstance().createEvent("phoneCallForwardingASMEvent");
						phoneCallForwardingASMEvent.setName("phoneCallForwardingASMEvent");
						phoneCallForwardingASMEvent.setCurrComponentName(e.getCurrComponentName());

						phoneCallForwardingASMEvent.setCurrPkgClsName(e.getCurrPkgClsName());
						phoneCallForwardingASMEvent.setCurrMethodName(e.getCurrMethodName());
						phoneCallForwardingASMEvent.setCurrComponentPkgName(e.getCurrComponentPkgName());
						phoneCallForwardingASMEvent.setCurrCompCallbackMethodName(e.getCurrCompCallbackMethodName());

						phoneCallForwardingASMEvent.getEventInfo().put("callForwardingCode", callForwardingCode);
						phoneCallForwardingASMEvent.getEventInfo().put(InstructionResponse.CLASS_NAME,
								(InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME));
						ta.setCurrASMEvent(phoneCallForwardingASMEvent);
					}
				}
			} else if (intentState != null && intentState instanceof IntentActionMainCatHomeDefinedState) {

				//Since this contains only one action, we just use that action to report this action. If some other attack uses
				// action and contains more than one action, we can generate respective ASM event for this.
				GenericReport rep = new GenericReport();
				rep.setInstrContainerCls(e.getCurrPkgClsName());
				rep.setInstContainerMthd(e.getCurrMethodName());
				rep.setCompPkgName(ta.getCurrComponentPkgName());
				rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
				rep.setCurrComponentClsName(ta.getCurrComponentName());

				rep.setMessage(" ##### This app contains action that displays HOME screen");

				InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
				rep.setSinkAPI(ir.getInstr().getText());

				if (!AttackReporter.getInstance().checkIfHomeScreenDisplayReportExists(rep)) {
					AttackReporter.getInstance().getHomeScreenDisplayReportList().add(rep);
					rep.printReport();
				}

				EventFactory.getInstance().registerEvent("displayHomeScreenASMEvent", new DisplayHomeScreenASMEvent());
				Event event = EventFactory.getInstance().createEvent("displayHomeScreenASMEvent");
				event.setName("displayHomeScreenASMEvent");
				event.setCurrComponentName(e.getCurrComponentName());

				event.setCurrPkgClsName(e.getCurrPkgClsName());
				event.setCurrMethodName(e.getCurrMethodName());
				event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
				event.setCurrComponentName(ta.getCurrComponentName());
				event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				event.getEventInfo().put(InstructionResponse.CLASS_NAME,
						(InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME));
				ta.setCurrASMEvent(event);
			}
		}
		return this;
	}

	@Override
	public State update(GetSystemServiceEvent e) {
		//		0x12 const-string v7, 'audio'
		//		0x16 invoke-virtual v10, v7, Lcom/example/phonecaller/MainActivity;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;
		//		0x1c move-result-object v0

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry serviceEntry = (SymbolTableEntry) e.getEventInfo().get("entry");
		if (serviceEntry != null) {
			Hashtable recordFieldList = serviceEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList != null && recordFieldList.size() > 0) {
				SymbolTableEntry serviceNameEntry = (SymbolTableEntry) recordFieldList.get("serviceName");
				if (serviceNameEntry != null) {
					String serviceName = serviceNameEntry.getEntryDetails().getValue();
					if (serviceName.contains("audio")) {
						SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
						if (returnMethodEntry != null) {
							State newState = new AudioManagerDefinedState(ta);
							returnMethodEntry.getEntryDetails().setState(newState);
						}
					} else if (serviceName.contains("keyguard")) {
						SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
						if (returnMethodEntry != null) {
							State newState = new KeyguardManagerDefinedState(ta);
							returnMethodEntry.getEntryDetails().setState(newState);
						}
					} else if (serviceName.contains("connectivity")) {
						SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
						if (returnMethodEntry != null) {
							State newState = new ConnectivityManagerDefinedState(ta);
							returnMethodEntry.getEntryDetails().setState(newState);
						}
					} else if (serviceName.contains("phone")) {
						SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
						if (returnMethodEntry != null) {
							State newState = new TelephonyManagerDefinedState(ta);
							returnMethodEntry.getEntryDetails().setState(newState);
						}
					} else if (serviceName.contains("wifi")) {
						SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
						if (returnMethodEntry != null) {
							State newState = new WifiManagerDefinedState(ta);
							returnMethodEntry.getEntryDetails().setState(newState);
						}
					} else if (serviceName.contains("device_policy")) {
						SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
						if (returnMethodEntry != null) {
							State newState = new DevicePolicyManagerDefinedState(ta);
							returnMethodEntry.getEntryDetails().setState(newState);
						}
					}
				}
			}
		}
		return this;
	}

	@Override
	public State update(GetPackageManagerDefinedEvent e) {
		//		0x24 invoke-virtual v5, Lcom/example/appRemoval2/MainActivity;->getPackageManager()Landroid/content/pm/PackageManager;
		//		0x2a move-result-object v0    

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry returnEntry = (SymbolTableEntry) this.localSymSpace.find(moveInstrReg.getName());
		if (returnEntry != null) {
			State newState = new PackageManagerDefinedState();
			returnEntry.getEntryDetails().setState(newState);
		}

		return this;
	}

	@Override
	public State update(GetManagerEvent e) {
		//		0x62 invoke-virtual v9, v10, Lcom/sssp/MyAdmin;->getManager(Landroid/content/Context;)Landroid/app/admin/DevicePolicyManager;
		//		0x68 move-result-object v9

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry returnEntry = (SymbolTableEntry) this.localSymSpace.find(moveInstrReg.getName());
		if (returnEntry != null) {
			State newState = new DevicePolicyManagerDefinedState(ta);
			returnEntry.getEntryDetails().setState(newState);
		}
		return this;
	}

	@Override
	public State update(GetPackageNameDefinedEvent e) {
		//		0x34 invoke-virtual v1, Landroid/content/Context;->getPackageName()Ljava/lang/String;
		//		0x3a move-result-object v1

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry returnEntry = (SymbolTableEntry) this.localSymSpace.find(moveInstrReg.getName());
		if (returnEntry != null) {
			State newState = new PackageNameDefinedState(ta);
			returnEntry.getEntryDetails().setState(newState);
		}
		return this;
	}

}
