package patternMatcher.statemachines.csm.keyguardmanager;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.AttackObserver;
import patternMatcher.events.Event;
import patternMatcher.events.csm.keyguardmanager.KeyguardRestrictedInputModeEvent;
import patternMatcher.statemachines.State;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class KeyguardManagerObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private Hashtable attackParameters;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public KeyguardManagerObserver() {
	}

	public KeyguardManagerObserver(TaintAnalyzer taParam) {
		this.taSubject = taParam;
		this.attackParameters = new Hashtable();
	}

	@Override
	public void update(KeyguardRestrictedInputModeEvent e) {
		//		0x12 check-cast v1, Landroid/app/KeyguardManager;
		//		0x16 invoke-virtual v1, Landroid/app/KeyguardManager;->inKeyguardRestrictedInputMode()Z
		//		0x1c move-result v0

		State currState = getCurrentStateOfCallerEntry(e);
		if (currState != null) {
			this.state = currState;
			state = state.update(e);
		}
	}

	public State getCurrentStateOfCallerEntry(Event e) {
		//		0x16 invoke-virtual v1, Landroid/app/KeyguardManager;->inKeyguardRestrictedInputMode()Z
		//		0x1c move-result v0

		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);

		Register reg1 = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry keyguardMgrEntry = localSymSpace.find(reg1.getName());

		if (keyguardMgrEntry != null)
			currState = keyguardMgrEntry.getEntryDetails().getState();

		return currState;
	}

	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
