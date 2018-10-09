package patternMatcher.statemachines.csm.reflection.states;
import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.csm.reflection.FieldGetByKeyEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import patternMatcher.events.csm.reflection.MethodSetAccessibleEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.reflection.ReflectionStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class MServiceFieldSetAccessibleState extends ReflectionStates{

	private TaintAnalyzer ta;
	private static Logger logger;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	public MServiceFieldSetAccessibleState(TaintAnalyzer taParam){
		this.ta = taParam;
		logger =  Logger.getLogger("");
	}
	
	@Override
	public State update(FieldGetByKeyEvent e) {

//		0x40 invoke-virtual v6, v5, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
//		0x46 move-result-object v13
		
		//This event is sent from move-result-object analyzer.
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);
			
		SymbolTableEntry callerEntry = (SymbolTableEntry) e.getEventInfo().get("callerAPIEntry");
		SymbolTableEntry param1Entry = (SymbolTableEntry) e.getEventInfo().get("param1Entry");
  		
		if(callerEntry != null && param1Entry != null){
			State callerState = callerEntry.getEntryDetails().getState();
			State param1State = param1Entry.getEntryDetails().getState();
			if(param1State != null && param1State instanceof ConnectivityManagerDefinedState
					&& callerState != null && callerState instanceof MServiceFieldSetAccessibleState){
				SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
				if(returnMethodEntry != null){
					State newState = new MServiceFieldAccessedState(ta);
					returnMethodEntry.getEntryDetails().setState(newState);
				}
			}
		}
		return this;  
	}
}
