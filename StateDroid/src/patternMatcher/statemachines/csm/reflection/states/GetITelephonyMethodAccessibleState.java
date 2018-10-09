package patternMatcher.statemachines.csm.reflection.states;
import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.reflection.ReflectionStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class GetITelephonyMethodAccessibleState extends ReflectionStates{

	private TaintAnalyzer ta;
	private static Logger logger;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public GetITelephonyMethodAccessibleState(TaintAnalyzer taParam){
		this.ta = taParam;
		logger =  Logger.getLogger("");
	}
	
	public GetITelephonyMethodAccessibleState(){}

	private String currInstr="";
	/*
	 * 
	 * 
		0x104 invoke-virtual v5, v11, v12, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object; [Ljava/lang/Object;)Ljava/lang/Object;
		0x10a move-result-object v9 
 * 			
 * (non-Javadoc)
	 * @see patternMatcher.statemachines.State#update(patternMatcher.events.csm.ClassGetDeclaredMethodEvent)
	 */
	@Override
	public State update(MethodInvokeEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry callerEntry = (SymbolTableEntry) e.getEventInfo().get("callerAPIEntry");
		SymbolTableEntry methodEntry = (SymbolTableEntry) e.getEventInfo().get("methodNameEntry");
  		
		if(callerEntry != null && methodEntry != null){
			State callereEntryState = callerEntry.getEntryDetails().getState();
			if(callereEntryState != null && callereEntryState instanceof GetITelephonyMethodAccessibleState){
				SymbolTableEntry returnEntry = this.localSymSpace.find(moveInstrReg.getName());
				if(returnEntry != null){
					State state = new GetITelephonyMethodInvokedState(ta);
					returnEntry.getEntryDetails().setState(state);
				}
			}
		}
		return this;  
	}

}
