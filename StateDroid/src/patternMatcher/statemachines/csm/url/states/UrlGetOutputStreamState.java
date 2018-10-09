package patternMatcher.statemachines.csm.url.states;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.filereading.DataOutputStreamDefinedEvent;
import patternMatcher.events.csm.url.UrlGetOutputStreamEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.states.DataOutputStreamDefinedState;
import patternMatcher.statemachines.csm.url.urlStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;

public class UrlGetOutputStreamState extends urlStates{
	
	private TaintAnalyzer ta;
	private SymbolSpace localSymbolSpace;
	public UrlGetOutputStreamState(TaintAnalyzer taParam){
		this.ta = taParam;
		this.localSymbolSpace = Config.getInstance().getLocalSymbolSpace();
	}

	public UrlGetOutputStreamState(){}

//		0x208 invoke-direct v0, v1, Ljava/io/DataOutputStream;-><init>(Ljava/io/OutputStream;)V

	@Override
	public State update(DataOutputStreamDefinedEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register reg0 = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry callerEntry = this.localSymbolSpace.find(reg0.getName());
		if(callerEntry != null){
			Register reg1 = ir.getInvolvedRegisters().get(1);
			SymbolTableEntry urlOutputStreamEntry = this.localSymbolSpace.find(reg1.getName());

			//State of urlOutputStreamEntry is already verified through observer, that's why we reached here.
			callerEntry.getEntryDetails().setValue(urlOutputStreamEntry.getEntryDetails().getValue()); //Copying urlValue here.
			DataOutputStreamDefinedState state = new DataOutputStreamDefinedState(this.ta);   //Transferring states to filereading CSM.
			callerEntry.getEntryDetails().setState(state);
		}
		return this;
	}
}
