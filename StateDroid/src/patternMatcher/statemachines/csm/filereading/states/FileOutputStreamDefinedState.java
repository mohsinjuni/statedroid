package patternMatcher.statemachines.csm.filereading.states;

import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.csm.filereading.DataOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.FileOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import sun.text.normalizer.UBiDiProps;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class FileOutputStreamDefinedState extends FileReadingStates {

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public FileOutputStreamDefinedState(TaintAnalyzer taParam) {
		this.ta = taParam;
	}

	public FileOutputStreamDefinedState() {
	}

}
