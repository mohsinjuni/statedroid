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


public class FileInputStreamReaderDefinedState extends FileReadingStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public FileInputStreamReaderDefinedState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
	}
	
	public FileInputStreamReaderDefinedState(){}
	
//		0x28a invoke-virtual v0, v8, v1, v2, Ljava/io/DataOutputStream;->write([B I I)V
//		v0 must have DataOutputStreamDefined state, and v8 must have FileInputStreamReaderDefinedState. We always go the state of callerEntry (i.e., v0)
	// and then check states of other objects there.

}
