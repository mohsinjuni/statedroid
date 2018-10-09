package patternMatcher.statemachines.csm.reflection.states;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.csm.reflection.ClassGetDeclaredMethodEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.reflection.ReflectionStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class EndCallMethodDeclaredState extends ReflectionStates{

	private TaintAnalyzer ta;
	private static Logger logger;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	public EndCallMethodDeclaredState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
		logger =  Logger.getLogger("");
	}
//		0x104 invoke-virtual v5, v11, v12, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object; [Ljava/lang/Object;)Ljava/lang/Object;
//		0x10a move-result-object v9 
	
	@Override
	public State update(MethodInvokeEvent e) {
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
  		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);
  		
  		Register reg1 = ir.getInvolvedRegisters().get(0);  
  		SymbolTableEntry callerEntry = this.localSymSpace.find(reg1.getName());
  		
		if(callerEntry != null){
			State state = callerEntry.getEntryDetails().getState();
			if(state != null && state instanceof EndCallMethodDeclaredState){
				
				EventFactory.getInstance().registerEvent("phoneBlockingEvent", new PhoneCallBlockingASMEvent());
				
				Event phoneBlockerEvent = EventFactory.getInstance().createEvent("phoneBlockingEvent");
				phoneBlockerEvent.setName("phoneBlockingEvent");
				
				phoneBlockerEvent.setCurrComponentName(e.getCurrComponentName());
				
				phoneBlockerEvent.setCurrPkgClsName(e.getCurrPkgClsName());
				phoneBlockerEvent.setCurrMethodName(e.getCurrMethodName());
				phoneBlockerEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
				phoneBlockerEvent.setCurrComponentName(ta.getCurrComponentName());
				phoneBlockerEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				
				phoneBlockerEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME));
				
				ta.setCurrASMEvent(phoneBlockerEvent);
			}
		}
		return this;  
	}
}
