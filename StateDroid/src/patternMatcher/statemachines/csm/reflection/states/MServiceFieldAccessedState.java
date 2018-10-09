package patternMatcher.statemachines.csm.reflection.states;
import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.csm.reflection.ClassGetDeclaredMethodEvent;
import patternMatcher.events.csm.reflection.FieldGetByKeyEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import patternMatcher.events.csm.reflection.MethodSetAccessibleEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.reflection.ReflectionStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class MServiceFieldAccessedState extends ReflectionStates{

	private TaintAnalyzer ta;
	private static Logger logger;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	public MServiceFieldAccessedState(TaintAnalyzer taParam){
		this.ta = taParam;
		logger =  Logger.getLogger("");
	}
	
	//Copy-pasted code from Initial state. Just doing it for sake of easier-to-understand coding habit. Otherwise, modification of
	// InitialState would have suffice.
	@Override
	public State update(ClassGetDeclaredMethodEvent e) {
		
//		logger.fatal("ClassGetDeclaredMethodEvent received");
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry callerAPIEntry = (SymbolTableEntry) e.getEventInfo().get("callerAPIEntry");
		SymbolTableEntry methodNameEntry = (SymbolTableEntry) e.getEventInfo().get("methodNameEntry");
		
		if(callerAPIEntry != null && methodNameEntry != null){
			String methodName = methodNameEntry.getEntryDetails().getValue();
			State state = callerAPIEntry.getEntryDetails().getState();
			
			if(methodName.contains("setMobileDataEnabled") 
				&& (state != null && state instanceof MServiceFieldAccessedState)
				){
				SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
				if(returnMethodEntry != null){
					State destState = new SetMobileDataEnabledMethodDeclaredState(ta);
					returnMethodEntry.getEntryDetails().setState(destState);
				}
			}
		}
		return this; //This is handled by objects' states now.
	}
}
