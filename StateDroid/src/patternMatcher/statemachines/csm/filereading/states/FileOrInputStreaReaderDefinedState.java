package patternMatcher.statemachines.csm.filereading.states;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.filereading.BufferedReaderDefinedEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class FileOrInputStreaReaderDefinedState extends FileReadingStates {

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public FileOrInputStreaReaderDefinedState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public FileOrInputStreaReaderDefinedState() {
	}

	@Override
	public State update(BufferedReaderDefinedEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register brReg = ir.getInvolvedRegisters().get(0);
		Register frReg = ir.getInvolvedRegisters().get(1);
		SymbolTableEntry brEntry = this.localSymSpace.find(brReg.getName());
		SymbolTableEntry frEntry = this.localSymSpace.find(frReg.getName());

		if (brEntry != null && frEntry != null) {
			State currState = frEntry.getEntryDetails().getState();
			if (currState != null && currState instanceof FileOrInputStreaReaderDefinedState) {
				String fileName = frEntry.getEntryDetails().getValue();
				brEntry.getEntryDetails().setValue(fileName);
				State nextState = new BufferedReaderDefinedState();
				brEntry.getEntryDetails().setState(nextState);
			}
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}

}
