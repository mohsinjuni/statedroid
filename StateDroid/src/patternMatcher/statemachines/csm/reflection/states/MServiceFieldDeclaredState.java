package patternMatcher.statemachines.csm.reflection.states;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.csm.reflection.FieldSetAccessibleEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.reflection.ReflectionStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class MServiceFieldDeclaredState extends ReflectionStates{

	private TaintAnalyzer ta;
	private static Logger logger;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	public MServiceFieldDeclaredState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
		logger =  Logger.getLogger("");
	}
	
	
	@Override
	public State update(FieldSetAccessibleEvent e) {

//		0x3a invoke-virtual v6, v15, Ljava/lang/reflect/Field;->setAccessible(Z)V
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
  		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);
  		
  		Register reg1 = ir.getInvolvedRegisters().get(0);  
  		Register reg2 = ir.getInvolvedRegisters().get(1);  

  		SymbolTableEntry callerEntry = this.localSymSpace.find(reg1.getName());
  		SymbolTableEntry paramEntry = this.localSymSpace.find(reg2.getName());
  		
		if(callerEntry != null && paramEntry != null){
			String paramValue = paramEntry.getEntryDetails().getValue();
			State prevState = callerEntry.getEntryDetails().getState();
			if(prevState != null && prevState instanceof MServiceFieldDeclaredState
				&& paramValue.equalsIgnoreCase("1")){
					State state = new MServiceFieldSetAccessibleState(ta);
					callerEntry.getEntryDetails().setState(state);
			}
		}
		return this;  
	}
}
