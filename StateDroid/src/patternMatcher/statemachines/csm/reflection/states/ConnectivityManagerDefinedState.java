package patternMatcher.statemachines.csm.reflection.states;
import java.util.ArrayList;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.csm.reflection.ClassGetDeclaredFieldEvent;
import patternMatcher.events.csm.reflection.ClassGetDeclaredMethodEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.reflection.ReflectionStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class ConnectivityManagerDefinedState extends ReflectionStates{
	
	public ConnectivityManagerDefinedState(){}
	TaintAnalyzer ta=null;
	private Logger logger;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public ConnectivityManagerDefinedState(TaintAnalyzer taParam){
		this.ta = taParam;
		logger =  Logger.getLogger("");
	}
	
//	  ConnectivityManagerDefinedState -> MServiceFieldDeclaredState -> MServiceFieldSetAccessibleState 
//	  			-> MServiceFieldAccessedState ->  SetMobileDataEnabledMethodDeclaredState -> ASMEvent
	
	@Override
	public State update(ClassGetDeclaredFieldEvent e) {
		
//		logger.fatal("ClassGetDeclaredMethodEvent received");
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry callerAPIEntry = (SymbolTableEntry) e.getEventInfo().get("callerAPIEntry");
		SymbolTableEntry fieldNameEntry = (SymbolTableEntry) e.getEventInfo().get("fieldNameEntry");
		
		if(callerAPIEntry != null && fieldNameEntry != null){
			String fieldName = fieldNameEntry.getEntryDetails().getValue();
			State sourceState = callerAPIEntry.getEntryDetails().getState();
			
			if(fieldName.contains("mService") 
				&& sourceState != null && sourceState instanceof ConnectivityManagerDefinedState
			){
				SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
				if(returnMethodEntry != null){
					State state = new MServiceFieldDeclaredState(ta);
					returnMethodEntry.getEntryDetails().setState(state);
				}
			}
		}
		return this;  
	}
	
	@Override    // class;->getMethod()   <----------------> class;->getDeclaredMethod()
	public State update(ClassGetDeclaredMethodEvent e) {
		
//		logger.fatal("ClassGetDeclaredMethodEvent received");
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry callerAPIEntry = (SymbolTableEntry) e.getEventInfo().get("callerAPIEntry");
		SymbolTableEntry methodNameEntry = (SymbolTableEntry) e.getEventInfo().get("methodNameEntry");
		
		if(callerAPIEntry != null && methodNameEntry != null){
			String methodName = methodNameEntry.getEntryDetails().getValue();
			String className = callerAPIEntry.getEntryDetails().getValue();
			
			if( methodName.contains("setMobileDataEnabled") 
					&& ( className.contains("Landroid/net/ConnectivityManager;") 
						|| callerAPIEntry.getEntryDetails().getState() instanceof ConnectivityManagerDefinedState)	
				){
				SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
				if(returnMethodEntry != null){
					State state = new SetMobileDataEnabledMethodDeclaredState(ta);
					returnMethodEntry.getEntryDetails().setState(state);
				}
			}
		}
		return this;  //remain in this state only.
	}


}
