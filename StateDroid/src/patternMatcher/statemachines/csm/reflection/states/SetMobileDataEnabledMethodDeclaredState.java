package patternMatcher.statemachines.csm.reflection.states;
import java.util.ArrayList;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.settings.MobileDataTogglerASMEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.reflection.ReflectionStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class SetMobileDataEnabledMethodDeclaredState extends ReflectionStates{

	private TaintAnalyzer ta;
	private static Logger logger;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	public SetMobileDataEnabledMethodDeclaredState(TaintAnalyzer taParam){
		this.ta = taParam;
		logger =  Logger.getLogger("");
	}
	
	@Override
	public State update(MethodInvokeEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		//For this, we don't care about the input parameters. As long as the setMobileData() method is invoked, we send an ASM event.
		SymbolTableEntry callerEntry = (SymbolTableEntry) e.getEventInfo().get("callerAPIEntry");
  		
		if(callerEntry != null){
			
			State callereEntryState = callerEntry.getEntryDetails().getState();
			if(callereEntryState != null && callereEntryState instanceof SetMobileDataEnabledMethodDeclaredState){
				
				EventFactory.getInstance().registerEvent("mobileDataTogglerASMEvent", new MobileDataTogglerASMEvent());
				
				Event asmEvent = EventFactory.getInstance().createEvent("mobileDataTogglerASMEvent");
				asmEvent.setName("mobileDataTogglerASMEvent");
				
				asmEvent.setCurrComponentName(e.getCurrComponentName());
				
				asmEvent.setCurrPkgClsName(e.getCurrPkgClsName());
				asmEvent.setCurrMethodName(e.getCurrMethodName());
				asmEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
				asmEvent.setCurrComponentName(ta.getCurrComponentName());
				asmEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				
				asmEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME));
				
				ta.setCurrASMEvent(asmEvent);

			}
		}
		return this;  
	}
}
