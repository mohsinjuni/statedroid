package patternMatcher.statemachines.csm.filereading.states;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.events.csm.filereading.FileUtilsReadFileToStringEvent;
import patternMatcher.events.csm.filereading.FilesToStringEvent;
import patternMatcher.events.csm.filereading.ScannerDefinedEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class FileDefinedState extends FileReadingStates {

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public FileDefinedState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public FileDefinedState() {
	}

	//		0x14 const-string v1, 'c:\\file.txt'
	//		0x18 invoke-direct v0, v1, Ljava/io/File;-><init>(Ljava/lang/String;)V
	//		0x1e invoke-direct v7, v0, Ljava/util/Scanner;-><init>(Ljava/io/File;)V

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see patternMatcher.statemachines.State#update(patternMatcher.events.csm.
	 * audiomanager.SetStreamVolumeEvent)
	 */
	@Override
	public State update(ScannerDefinedEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register brReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry scannerEntry = this.localSymSpace.find(brReg.getName());

		if (scannerEntry != null) {
			Hashtable recordFieldList = scannerEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList != null && recordFieldList.size() > 0) {
				SymbolTableEntry fileEntry = (SymbolTableEntry) recordFieldList.get("fileEntry");
				if (fileEntry != null) {
					State currState = fileEntry.getEntryDetails().getState();
					if (currState != null && currState instanceof FileDefinedState) {
						String fileName = fileEntry.getEntryDetails().getValue();
						scannerEntry.getEntryDetails().setValue(fileName);
						State state = new ScannerDefinedState(this.ta);
						scannerEntry.getEntryDetails().setState(state);
					}
				}
			}
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}

	@Override
	public State update(FileUtilsReadFileToStringEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register brReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry returnEntry = this.localSymSpace.find(brReg.getName());

		if (returnEntry != null) {
			Hashtable recordFieldList = returnEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList != null && recordFieldList.size() > 0) {
				SymbolTableEntry fileUtilEntry = (SymbolTableEntry) recordFieldList.get("callerEntry");
				if (fileUtilEntry != null) {
					State currState = fileUtilEntry.getEntryDetails().getState();
					if (currState != null && currState instanceof FileDefinedState) {
						String fileName = fileUtilEntry.getEntryDetails().getValue();

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
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}

	@Override
	public State update(FilesToStringEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register brReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry returnEntry = this.localSymSpace.find(brReg.getName());

		if (returnEntry != null) {
			Hashtable recordFieldList = returnEntry.getEntryDetails().getRecordFieldList();
			if (recordFieldList != null && recordFieldList.size() > 0) {
				SymbolTableEntry fileUtilEntry = (SymbolTableEntry) recordFieldList.get("callerEntry");
				if (fileUtilEntry != null) {
					State currState = fileUtilEntry.getEntryDetails().getState();
					if (currState != null && currState instanceof FileDefinedState) {
						String fileName = fileUtilEntry.getEntryDetails().getValue();

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
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}
}
