package patternMatcher.statemachines.csm.filereading.states;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.csm.filereading.DataOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.FileInputStreamReadEvent;
import patternMatcher.events.csm.filereading.FileOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class CipherInputStreamReadState extends FileReadingStates {

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public CipherInputStreamReadState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public CipherInputStreamReadState() {
	}

	@Override
	public State update(FileOutputStreamWriteEvent e) {
		//		0x1c6 invoke-virtual/range v20 ... v23, Ljava/io/FileOutputStream;->write([B I I)V

		//		eventInfo.put("callerEntry", callerEntry);
		//		eventInfo.put("inputParamEntry", inputParamEntry);

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Hashtable eventInfo = e.getEventInfo();
		SymbolTableEntry callerEntry = (SymbolTableEntry) eventInfo.get("callerEntry");
		SymbolTableEntry inputParamEntry = (SymbolTableEntry) eventInfo.get("inputParamEntry");

		if (inputParamEntry != null) {
			State state = inputParamEntry.getEntryDetails().getState();
			if (state != null && state instanceof CipherInputStreamReadState) {
				String encryptionValue = inputParamEntry.getEntryDetails().getValue();
				reportAttack(e, ir, encryptionValue);
			}
		}
		return this; //This does not matter anymore. Each object maintains its state itself.
	}

	public void reportAttack(FileOutputStreamWriteEvent e, InstructionResponse ir, String encryptionKey) {
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
