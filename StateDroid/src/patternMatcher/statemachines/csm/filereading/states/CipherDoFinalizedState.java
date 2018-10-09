package patternMatcher.statemachines.csm.filereading.states;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.filereading.CipherInputStreamReadEvent;
import patternMatcher.events.csm.filereading.FileInputStreamReadEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class CipherDoFinalizedState extends FileReadingStates {

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public CipherDoFinalizedState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public CipherDoFinalizedState() {
	}

	@Override
	public State update(CipherInputStreamReadEvent e) {
		//		0x166 invoke-virtual/range v20 ... v21, Ljavax/crypto/CipherInputStream;->read([B)I  
		//			eventInfo.put("callerEntry", callerEntry);
		//			eventInfo.put("inputParamEntry", inputParamEntry);

		Hashtable eventInfo = e.getEventInfo();
		SymbolTableEntry callerEntry = (SymbolTableEntry) eventInfo.get("callerEntry");
		SymbolTableEntry inputParamEntry = (SymbolTableEntry) eventInfo.get("inputParamEntry");
		if (callerEntry != null) {
			State state = callerEntry.getEntryDetails().getState();
			if (state != null && state instanceof CipherDoFinalizedState) {
				if (inputParamEntry != null) {
					String value = callerEntry.getEntryDetails().getValue();
					if (value != null) {
						inputParamEntry.getEntryDetails().setValue(value);
					}
					State newState = new CipherInputStreamReadState(this.ta);
					inputParamEntry.getEntryDetails().setState(newState);
				}
			}
		}
		return this;
	}

}
