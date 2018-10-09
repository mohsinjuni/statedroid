package patternMatcher.statemachines.csm.reflection.states;

import java.util.ArrayList;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import patternMatcher.events.csm.reflection.ClassGetDeclaredMethodEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.reflection.ReflectionStates;
import taintanalyzer.TaintAnalyzer;


public class InitialState extends ReflectionStates{

	private TaintAnalyzer ta;
	private Logger logger;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	/*
	 * 
	 * State transitions for this attack.
	 * 
	 * InitialState -> GetITelephonyMethodState -> GetITelephonyInvokedState -> EndCallMethodState 
	 * 																		 -> SilenceRingerMethodDeclaredState	
	 * ConnectivityManagerDefinedState -> MServiceFieldDeclaredState -> MServiceFieldSetAccessibleState 
	 * 			-> MServiceFieldAccessedState ->  SetMobileDataEnabledMethodDeclaredState -> ASMEvent
	 */
	
	public InitialState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
		logger =  Logger.getLogger("");
	}
	
	public InitialState(){}

	private String currInstr="";
	
	/*
	 * 
	 * 
		0xdc invoke-static v12, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
		0xe2 move-result-object v1
		0xe4 const-string v12, 'getITelephony'
		0xe8 const/4 v13, 0
		0xea new-array v13, v13, [Ljava/lang/Class;
		0xee invoke-virtual v1, v12, v13, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String; [Ljava/lang/Class;)Ljava/lang/reflect/Method;
		0xf4 move-result-object v5
 * 			
 * (non-Javadoc)
	 * @see patternMatcher.statemachines.State#update(patternMatcher.events.csm.ClassGetDeclaredMethodEvent)
	 */
	@Override
	public State update(ClassGetDeclaredMethodEvent e) {
		
//		logger.fatal("ClassGetDeclaredMethodEvent received");
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry callerAPIEntry = (SymbolTableEntry) e.getEventInfo().get("callerAPIEntry");
		SymbolTableEntry methodNameEntry = (SymbolTableEntry) e.getEventInfo().get("methodNameEntry");
		
		if(callerAPIEntry != null && methodNameEntry != null)
		{
			String methodName = methodNameEntry.getEntryDetails().getValue();
			String className = callerAPIEntry.getEntryDetails().getValue();
			
//TODO			//Instead of checking className, the class object should have a state which should be checked. 
			if(methodName.contains("getITelephony") 
					&& ( className.contains("Landroid/telephony/TelephonyManager;") 
						|| callerAPIEntry.getEntryDetails().getState() instanceof TelephonyManagerDefinedState)	
							){
				
				SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
				if(returnMethodEntry != null){
					State state = new GetITelephonyMethodDeclaredState(ta);
					returnMethodEntry.getEntryDetails().setState(state);
				}
			}else if( (methodName.contains("setMobileDataEnabled") || methodName.contains("setDataEnabled")) 
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
