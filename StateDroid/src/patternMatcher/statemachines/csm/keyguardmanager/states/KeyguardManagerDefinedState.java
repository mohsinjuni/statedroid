package patternMatcher.statemachines.csm.keyguardmanager.states;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerStreamVolumeChangedASMEvent;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;
import patternMatcher.events.csm.keyguardmanager.KeyguardRestrictedInputModeEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.keyguardmanager.KeyguardManagerStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import enums.Constants;

public class KeyguardManagerDefinedState extends KeyguardManagerStates {

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public KeyguardManagerDefinedState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public KeyguardManagerDefinedState() {
	}

	@Override
	public State update(KeyguardRestrictedInputModeEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register keyguardMgrReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry keyguardMgrEntry = this.localSymSpace.find(keyguardMgrReg.getName());

		if (keyguardMgrEntry != null) {
			State currState = keyguardMgrEntry.getEntryDetails().getState();
			if (currState != null && currState instanceof KeyguardManagerDefinedState) {
				EventFactory.getInstance().registerEvent("keyguardLockCheckingEvent", new KeyguardLockCheckingASMEvent());
				Event keyguardLockCheckingEvent = EventFactory.getInstance().createEvent("keyguardLockCheckingEvent");
				keyguardLockCheckingEvent.setName("keyguardLockCheckingEvent");

				keyguardLockCheckingEvent.setCurrMethodName(ir.getInstr().getCurrMethodName());
				keyguardLockCheckingEvent.setCurrPkgClsName(ir.getInstr().getCurrPkgClassName());

				keyguardLockCheckingEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
				keyguardLockCheckingEvent.setCurrComponentName(ta.getCurrComponentName());
				keyguardLockCheckingEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				keyguardLockCheckingEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);

				//First report CSM functionality, then send event to the ASMs. 
				reportAttack(keyguardLockCheckingEvent);
				ta.setCurrASMEvent(keyguardLockCheckingEvent);
			}
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}

	public void reportAttack(Event e) {
		String permStr = Config.getInstance().getCurrCFGPermutationString();

		GenericReport rep = new GenericReport();
		rep.setInstrContainerCls(e.getCurrPkgClsName());
		rep.setInstContainerMthd(e.getCurrMethodName());

		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());
		rep.setPermutationStr(permStr);

		String msg = "##### This app checks if the phone screen keyguard is locked or not. #####";
		rep.setMessage(msg);

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfKeyguardManagerStreamVolumeChangedReportExists(rep)) {
			AttackReporter.getInstance().getKeyguardManagerStreamVolumeChangedReportList().add(rep);
			rep.printReport();
		}
	}
}
