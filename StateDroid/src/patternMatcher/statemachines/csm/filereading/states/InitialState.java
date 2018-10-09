package patternMatcher.statemachines.csm.filereading.states;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.csm.filereading.BufferedOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.CipherGetInstanceEvent;
import patternMatcher.events.csm.filereading.CipherInputStreamInitEvent;
import patternMatcher.events.csm.filereading.FileDefinedEvent;
import patternMatcher.events.csm.filereading.FileInputStreamDefinedEvent;
import patternMatcher.events.csm.filereading.FileOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.FileReaderDefinedEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class InitialState extends FileReadingStates {

	private String currInstr = "";
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	private TaintAnalyzer ta;

	public InitialState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public InitialState() {
	}

	@Override
	public State update(FileReaderDefinedEvent e) {
		SymbolTableEntry fileReaderEntry = null;
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();

		Register fileReaderReg = ir.getInvolvedRegisters().get(0);
		fileReaderEntry = localSymSpace.find(fileReaderReg.getName());

		if (fileReaderEntry != null) {
			Hashtable recordFiedList = fileReaderEntry.getEntryDetails().getRecordFieldList();
			if (recordFiedList != null) {
				SymbolTableEntry fileNameEntry = (SymbolTableEntry) recordFiedList.get("fileEntry");
				if (fileNameEntry != null) {
					String fileName = fileNameEntry.getEntryDetails().getValue();
					fileReaderEntry.getEntryDetails().setValue(fileName);
					State state = new FileOrInputStreaReaderDefinedState();
					fileReaderEntry.getEntryDetails().setState(state);
				}
			}
		}
		return this;
	}

	@Override
	public State update(FileInputStreamDefinedEvent e) {
		SymbolTableEntry fileInputStreamEntry = null;
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);

		Register fileReaderReg = ir.getInvolvedRegisters().get(0);
		fileInputStreamEntry = localSymSpace.find(fileReaderReg.getName());

		if (fileInputStreamEntry != null) {
			Hashtable recordFiedList = fileInputStreamEntry.getEntryDetails().getRecordFieldList();
			if (recordFiedList != null) {
				SymbolTableEntry fileNameEntry = (SymbolTableEntry) recordFiedList.get("fileEntry");
				if (fileNameEntry != null) {
					String fileName = fileNameEntry.getEntryDetails().getValue();
					fileInputStreamEntry.getEntryDetails().setValue(fileName);
					State state = new FileInputStreamDefinedState(this.ta);
					fileInputStreamEntry.getEntryDetails().setState(state);
				}
			}
		}
		return this;
	}

	@Override
	public State update(CipherInputStreamInitEvent e) {
		Hashtable eventInfo = e.getEventInfo();
		SymbolTableEntry fileInputStreamEntry = (SymbolTableEntry) eventInfo.get("fileInputStreamEntry");
		SymbolTableEntry ciperEntry = (SymbolTableEntry) eventInfo.get("cipherEntry");
		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);

		if (fileInputStreamEntry != null && ciperEntry != null) {
			State fisState = fileInputStreamEntry.getEntryDetails().getState();
			State cipherState = ciperEntry.getEntryDetails().getState();
			if (fisState != null && fisState instanceof FileInputStreamDefinedState && cipherState != null
					&& cipherState instanceof CipherDefinedState) {

				Register reg1 = ir.getInvolvedRegisters().get(0);
				SymbolTableEntry cipherInputStreamEntry = localSymSpace.find(reg1.getName());

				if (cipherInputStreamEntry != null) {
					String value = ciperEntry.getEntryDetails().getValue();
					if (value != null && !value.isEmpty()) {
						cipherInputStreamEntry.getEntryDetails().setValue(value);
					}
					State state = new CipherInputStreamDefinedState(this.ta);
					cipherInputStreamEntry.getEntryDetails().setState(state);
				}
			}
		}
		return this;
	}

	@Override
	public State update(FileDefinedEvent e) {
		SymbolTableEntry fileEntry = null;
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();

		Register fileReaderReg = ir.getInvolvedRegisters().get(0);
		fileEntry = localSymSpace.find(fileReaderReg.getName());

		if (fileEntry != null) {
			Hashtable recordFiedList = fileEntry.getEntryDetails().getRecordFieldList();
			if (recordFiedList != null) {
				SymbolTableEntry fileNameEntry = (SymbolTableEntry) recordFiedList.get("fileEntry"); //It should be fileNameEntry though.
				if (fileNameEntry != null) {
					String fileName = fileNameEntry.getEntryDetails().getValue();
					//					if(!fileName.isEmpty()){
					fileEntry.getEntryDetails().setValue(fileName);
					State state = new FileDefinedState(this.ta);
					fileEntry.getEntryDetails().setState(state);
					//					}
				}
			}
		}
		return this;
	}

	@Override
	public State update(CipherGetInstanceEvent e) {
		//		0x4a const-string v7, 'AES/CBC/PKCS5Padding'
		//		0x4e invoke-static v7, Ljavax/crypto/Cipher;->getInstance(Ljava/lang/String;)Ljavax/crypto/Cipher;
		//		0x54 move-result-object v7

		Hashtable eventInfo = e.getEventInfo();
		SymbolTableEntry cipherEntry = (SymbolTableEntry) eventInfo.get("callerEntry");

		if (cipherEntry != null) {
			Hashtable recordFiedList = cipherEntry.getEntryDetails().getRecordFieldList();
			if (recordFiedList != null) {
				SymbolTableEntry encryptionNameEntry = (SymbolTableEntry) recordFiedList.get("encryption"); //It should be fileNameEntry though.
				if (encryptionNameEntry != null) {
					String fileName = encryptionNameEntry.getEntryDetails().getValue();
					cipherEntry.getEntryDetails().setValue(fileName);
					State state = new CipherGetInstanceState(this.ta);
					cipherEntry.getEntryDetails().setState(state);
				}
			}
		}
		return this;
	}

	@Override
	public State update(BufferedOutputStreamWriteEvent e) {
		//		0x1c6 invoke-virtual/range v20 ... v23, Ljava/io/BufferedOutputStream;->write([B I I)V

		//		eventInfo.put("callerEntry", callerEntry);
		//		eventInfo.put("inputParamEntry", inputParamEntry);

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Hashtable eventInfo = e.getEventInfo();
		SymbolTableEntry callerEntry = (SymbolTableEntry) eventInfo.get("callerEntry");
		SymbolTableEntry inputParamEntry = (SymbolTableEntry) eventInfo.get("inputParamEntry");

		if (inputParamEntry != null) {
			State state = inputParamEntry.getEntryDetails().getState();
			if (state != null && state instanceof CipherDoFinalizedState) {
				String encryptionValue = inputParamEntry.getEntryDetails().getValue();
				if (encryptionValue == null) {
					encryptionValue = "";
				}
				reportAttack(e, ir, encryptionValue);
			}
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}

	public void reportAttack(BufferedOutputStreamWriteEvent e, InstructionResponse ir, String encryptionKey) {
		String permStr = Config.getInstance().getCurrCFGPermutationString();

		GenericReport rep = new GenericReport();
		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

		rep.setInstrContainerCls(ir.getInstr().getCurrPkgClassName());
		rep.setInstContainerMthd(ir.getInstr().getCurrMethodName());
		rep.setPermutationStr(permStr);

		String msg = "##### CSM:  This app can encrypt files using encryption = " + encryptionKey + " \n\n";
		rep.setMessage(msg);
		rep.setSinkAPI(ir.getInstr().getText());

		if (!AttackReporter.getInstance().checkIfFilelEncryptionReportExists(rep)) {
			AttackReporter.getInstance().getFilelEncryptionReportList().add(rep);
			rep.printReport();
		}
	}

}
