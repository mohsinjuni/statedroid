package patternMatcher.statemachines.csm.filereading.states;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.events.csm.filereading.CipherDoFinalEvent;
import patternMatcher.events.csm.filereading.FileUtilsReadFileToStringEvent;
import patternMatcher.events.csm.filereading.FilesToStringEvent;
import patternMatcher.events.csm.filereading.ScannerDefinedEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class CipherDefinedState extends FileReadingStates {

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public CipherDefinedState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public CipherDefinedState() {
	}

	//		0x14 const-string v1, 'c:\\file.txt'
	//		0x18 invoke-direct v0, v1, Ljava/io/File;-><init>(Ljava/lang/String;)V
	//		0x1e invoke-direct v7, v0, Ljava/util/Scanner;-><init>(Ljava/io/File;)V

	@Override
	public State update(CipherDoFinalEvent e) {

		InstructionResponse oldIR = (InstructionResponse) e.getEventInfo().get("oldIR");
		Hashtable eventInfo = e.getEventInfo();
		int regCount = oldIR.getInvolvedRegisters().size();
		if (regCount == 2 || regCount == 4) {
			if (eventInfo.containsKey("inputByteEntry")) {
				SymbolTableEntry inputByteEntry = (SymbolTableEntry) eventInfo.get("inputByteEntry");
				State state = inputByteEntry.getEntryDetails().getState();
				if (state != null && state instanceof FileInputStreamReaderDefinedState) {
					if (eventInfo.containsKey("entry")) {
						SymbolTableEntry returnEntry = (SymbolTableEntry) eventInfo.get("entry");
						if (returnEntry != null) {
							State returnState = new CipherDoFinalizedState(this.ta);
							returnEntry.getEntryDetails().setState(returnState);
						}
					}
				}
			}
		} else if (regCount == 5 || regCount == 6) {
			if (eventInfo.containsKey("inputByteEntry")) {
				SymbolTableEntry inputByteEntry = (SymbolTableEntry) eventInfo.get("inputByteEntry");
				State state = inputByteEntry.getEntryDetails().getState();
				if (state != null && state instanceof FileInputStreamReaderDefinedState) {
					if (eventInfo.containsKey("outputByteEntry")) {
						SymbolTableEntry outputByteEntry = (SymbolTableEntry) eventInfo.get("outputByteEntry");
						if (outputByteEntry != null) {
							State outputState = new CipherDoFinalizedState(this.ta);
							outputByteEntry.getEntryDetails().setState(outputState);
						}
					}
				}
			}
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}
}
