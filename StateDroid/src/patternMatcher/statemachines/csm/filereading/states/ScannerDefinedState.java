package patternMatcher.statemachines.csm.filereading.states;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.events.csm.filereading.ScannerReadDataEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class ScannerDefinedState extends FileReadingStates {

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public ScannerDefinedState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public ScannerDefinedState() {
	}

	@Override
	public State update(ScannerReadDataEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register brReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry returnEntry = this.localSymSpace.find(brReg.getName());

		if (returnEntry != null) {
			Hashtable eventInfo = (Hashtable) e.getEventInfo();
			SymbolTableEntry scannerEntry = (SymbolTableEntry) eventInfo.get("callerEntry");
			if (scannerEntry != null) {
				State currState = scannerEntry.getEntryDetails().getState();
				if (currState != null && currState instanceof ScannerDefinedState) {
					String fileName = scannerEntry.getEntryDetails().getValue();

					String srcInitial = AttackReporter.getInstance().getNonAPISource();
					ArrayList<SourceInfo> siList = returnEntry.getEntryDetails().getSourceInfoList();
					if (siList == null)
						siList = new ArrayList<SourceInfo>();
					SourceInfo si = new SourceInfo();
					srcInitial += "-FILE";
					if (fileName != null && !fileName.isEmpty()) {
						srcInitial += "(" + fileName + ")";
					}
					si.setSrcAPI(srcInitial);
					siList.add(si);
					returnEntry.getEntryDetails().setSourceInfoList(siList);
					returnEntry.getEntryDetails().setTainted(true);
				}
			}
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}

}
