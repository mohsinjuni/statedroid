package patternMatcher.statemachines.csm.filereading.states;
import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.events.csm.filereading.CipherDefinedEvent;
import patternMatcher.events.csm.filereading.CipherInputStreamInitEvent;
import patternMatcher.events.csm.filereading.FileUtilsReadFileToStringEvent;
import patternMatcher.events.csm.filereading.FilesToStringEvent;
import patternMatcher.events.csm.filereading.ScannerDefinedEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class CipherGetInstanceState extends FileReadingStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public CipherGetInstanceState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
	}
	
	public CipherGetInstanceState(){}
	
//	0x60 invoke-virtual v7, v8, v9, v10, Ljavax/crypto/Cipher;->init(I Ljava/security/Key; Ljava/security/spec/AlgorithmParameterSpec;)V

	@Override
	public State update(CipherDefinedEvent e) {
		
		Hashtable eventInfo = e.getEventInfo();
		SymbolTableEntry cipherEntry = (SymbolTableEntry) eventInfo.get("callerEntry");

		if(cipherEntry != null){
			State state = new CipherDefinedState(this.ta);
			cipherEntry.getEntryDetails().setState(state);
		}	
		return this;  //This does not matter anymore. Each object maintains its state itself.
	}
}
