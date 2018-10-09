package patternMatcher.statemachines.csm.appremoval.states;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.manifest.AndroidManifest;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.appremoval.AppRemovalASMEvent;
import patternMatcher.events.csm.appremoval.SetApplicationEnabledEvent;
import patternMatcher.events.csm.appremoval.SetComponentEnabledEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.appremoval.AppRemovalStates;
import patternMatcher.statemachines.csm.context.states.PackageManagerDefinedState;
import patternMatcher.statemachines.csm.context.states.PackageNameDefinedState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends AppRemovalStates {

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(SetApplicationEnabledEvent e) {

		//		0x3c invoke-virtual v0, v1, v4, v3, Landroid/content/pm/PackageManager;->setApplicationEnabledSetting(Ljava/lang/String; I I)V

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();

		Register pkgMgrReg = involvedRegisters.get(0);
		Register pkgNameReg = involvedRegisters.get(1);
		Register param2Reg = involvedRegisters.get(2);
		Register param3Reg = involvedRegisters.get(3);

		SymbolTableEntry pkgMgrEntry = this.localSymSpace.find(pkgMgrReg.getName());
		SymbolTableEntry pkgNameEntry = this.localSymSpace.find(pkgNameReg.getName());
		SymbolTableEntry param2Entry = this.localSymSpace.find(param2Reg.getName());
		SymbolTableEntry param3Entry = this.localSymSpace.find(param3Reg.getName());

		if (pkgMgrEntry != null && pkgNameEntry != null && param2Entry != null) {

			State pkgMgrState = pkgMgrEntry.getEntryDetails().getState();
			State pkgNameState = pkgNameEntry.getEntryDetails().getState();

			if ((pkgMgrState != null && pkgMgrState instanceof PackageManagerDefinedState)
					&& (pkgNameState != null && pkgNameState instanceof PackageNameDefinedState)) {

				String param2Value = param2Entry.getEntryDetails().getValue();
				int intValue = -1;

				if (!param2Value.isEmpty()) {
					intValue = Integer.parseInt(param2Value.trim());
				}
				if (intValue > 1) {

					EventFactory.getInstance().registerEvent("appRemovalASMEvent", new AppRemovalASMEvent());
					Event event = EventFactory.getInstance().createEvent("appRemovalASMEvent");
					event.setName("appRemovalASMEvent");

					event.setCurrMethodName(ir.getInstr().getCurrMethodName());
					event.setCurrPkgClsName(ir.getInstr().getCurrPkgClassName());

					event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
					event.setCurrComponentName(ta.getCurrComponentName());
					event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());

					event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);

					ta.setCurrASMEvent(event);
				}
			}
		}
		return this;
	}

	@Override
	public State update(SetComponentEnabledEvent e) {
		//		0x58 invoke-virtual v0, v1, v4, v3, Landroid/content/pm/PackageManager;->setComponentEnabledSetting(Landroid/content/ComponentName; I I)V

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();

		Register pkgMgrReg = involvedRegisters.get(0);
		Register compNameReg = involvedRegisters.get(1);
		Register param2Reg = involvedRegisters.get(2);
		Register param3Reg = involvedRegisters.get(3);

		SymbolTableEntry pkgMgrEntry = this.localSymSpace.find(pkgMgrReg.getName());
		SymbolTableEntry compNameEntry = this.localSymSpace.find(compNameReg.getName());
		SymbolTableEntry param2Entry = this.localSymSpace.find(param2Reg.getName());
		SymbolTableEntry param3Entry = this.localSymSpace.find(param3Reg.getName());

		boolean isMainComponent = false;
		if (compNameEntry != null) {
			Hashtable recordFieldList = compNameEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList != null) {
				SymbolTableEntry compEntry = (SymbolTableEntry) recordFieldList.get("compEntry");
				if (compEntry != null) {
					String compName = compEntry.getEntryDetails().getValue();

					AndroidManifest manifest = Config.getInstance().getAndroidManifest();
					isMainComponent = manifest.isLauncherMainComponent(compName);
				}
			}
		}
		if (isMainComponent && pkgMgrEntry != null && param2Entry != null) {
			State pkgMgrState = pkgMgrEntry.getEntryDetails().getState();

			if ((pkgMgrState != null && pkgMgrState instanceof PackageManagerDefinedState)) {
				String param2Value = param2Entry.getEntryDetails().getValue();
				int param2IntValue = -1;

				if (!param2Value.isEmpty()) {
					param2IntValue = Integer.parseInt(param2Value.trim());
				}
				if (param2IntValue > 1) {

					EventFactory.getInstance().registerEvent("appRemovalASMEvent", new AppRemovalASMEvent());
					Event event = EventFactory.getInstance().createEvent("appRemovalASMEvent");
					event.setName("appRemovalASMEvent");

					event.setCurrMethodName(ir.getInstr().getCurrMethodName());
					event.setCurrPkgClsName(ir.getInstr().getCurrPkgClassName());

					event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
					event.setCurrComponentName(ta.getCurrComponentName());
					event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());

					event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);

					ta.setCurrASMEvent(event);
				}
			}
		}
		return this;
	}
}
