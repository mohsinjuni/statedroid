package patternMatcher.statemachines.csm.reflection.states;
import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.ShowCallScreenWithDialpadASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallAnsweringASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.events.csm.reflection.ClassGetDeclaredMethodEvent;
import patternMatcher.events.csm.reflection.ITelephonyAnswerRingingCallEvent;
import patternMatcher.events.csm.reflection.ITelephonyEndCallEvent;
import patternMatcher.events.csm.reflection.ITelephonySilenceRingerEvent;
import patternMatcher.events.csm.reflection.ShowCallScreenWithDialpadEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.reflection.ReflectionStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class GetITelephonyMethodInvokedState extends ReflectionStates{

	private TaintAnalyzer ta;
	private static Logger logger;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public GetITelephonyMethodInvokedState(TaintAnalyzer taParam){
		this.ta = taParam;
		logger =  Logger.getLogger("");
	}
	
	public GetITelephonyMethodInvokedState(){}
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
		0x100 new-array v12, v12, [Ljava/lang/Object;

		0x104 invoke-virtual v5, v11, v12, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object; [Ljava/lang/Object;)Ljava/lang/Object;
		0x10a move-result-object v9 
 * 			
 * (non-Javadoc)
	 * @see patternMatcher.statemachines.State#update(patternMatcher.events.csm.ClassGetDeclaredMethodEvent)
	 */
	@Override
	public State update(ClassGetDeclaredMethodEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry callerEntry = (SymbolTableEntry) e.getEventInfo().get("callerAPIEntry");
		SymbolTableEntry methodEntry = (SymbolTableEntry) e.getEventInfo().get("methodNameEntry");
  		
		if(callerEntry != null && methodEntry != null){
			State state = callerEntry.getEntryDetails().getState();
			if(state != null && state instanceof GetITelephonyMethodInvokedState){
				String methodName = methodEntry.getEntryDetails().getValue();
				if(methodName.contains("endCall")){
					SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
					if(returnMethodEntry != null){
						State newState = new EndCallMethodDeclaredState(ta);
						returnMethodEntry.getEntryDetails().setState(newState);
					}
				}else if(methodName.contains("silenceRinger")){
					SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
					if(returnMethodEntry != null){
						State newState = new SilenceRingerMethodDeclaredState(ta);
						returnMethodEntry.getEntryDetails().setState(newState);
					}
				}else if(methodName.contains("answerRingingCall")){
					SymbolTableEntry returnMethodEntry = this.localSymSpace.find(moveInstrReg.getName());
					if(returnMethodEntry != null){
						State newState = new AnswerRingingCallMethodDeclaredState(ta);
						returnMethodEntry.getEntryDetails().setState(newState);
					}
				}
			}
		}
		return this;  //remain in this state only.
	}
	
	//It can directly receive endCall() method if someone is using com.android.internal.itelephony($Stub) to directly call endCall() method.
	@Override
	public State update(ITelephonyEndCallEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry callerEntry = (SymbolTableEntry) e.getEventInfo().get("callerAPIEntry");
  		
		if(callerEntry != null){
			State state = callerEntry.getEntryDetails().getState();
			if(state != null && state instanceof GetITelephonyMethodInvokedState){
				
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
		return this;  //remain in this state only.
	}

	@Override
	public State update(ITelephonySilenceRingerEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry callerEntry = (SymbolTableEntry) e.getEventInfo().get("callerAPIEntry");
  		
		if(callerEntry != null){
			State state = callerEntry.getEntryDetails().getState();
			if(state != null && state instanceof GetITelephonyMethodInvokedState){
				
				EventFactory.getInstance().registerEvent("ringerModeSilentASMEvent", new RingerModeSilentASMEvent());
				
				Event ringerModeSilentEvent = EventFactory.getInstance().createEvent("ringerModeSilentASMEvent");
				ringerModeSilentEvent.setName("ringerModeSilentASMEvent");
				
				ringerModeSilentEvent.setCurrComponentName(e.getCurrComponentName());
				
				ringerModeSilentEvent.setCurrPkgClsName(e.getCurrPkgClsName());
				ringerModeSilentEvent.setCurrMethodName(e.getCurrMethodName());
				ringerModeSilentEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
				ringerModeSilentEvent.setCurrComponentName(ta.getCurrComponentName());
				ringerModeSilentEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				
				ringerModeSilentEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME));
				
				ta.setCurrASMEvent(ringerModeSilentEvent);

			}
		}
		return this;  //remain in this state only.
	}
	
	@Override
	public State update(ITelephonyAnswerRingingCallEvent e) {

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register moveInstrReg = involvedRegisters.get(0);

		SymbolTableEntry callerEntry = (SymbolTableEntry) e.getEventInfo().get("callerAPIEntry");
  		
		if(callerEntry != null){
			State state = callerEntry.getEntryDetails().getState();
			if(state != null && state instanceof GetITelephonyMethodInvokedState){
				
				EventFactory.getInstance().registerEvent("phoneCallAnsweringASMEvent", new PhoneCallAnsweringASMEvent());
				
				Event phoneBlockerEvent = EventFactory.getInstance().createEvent("phoneCallAnsweringASMEvent");
				phoneBlockerEvent.setName("phoneCallAnsweringASMEvent");
				
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
		return this;  //remain in this state only.
	}

	@Override
	public State update(ShowCallScreenWithDialpadEvent e) {
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerReg = involvedRegisters.get(0);

		SymbolTableEntry callerEntry = this.localSymSpace.find(callerReg.getName()); 
		if(callerEntry != null){
			SymbolTableEntry inputEntry = (SymbolTableEntry) e.getEventInfo().get("inputParamEntry");
			if(inputEntry != null && inputEntry.getEntryDetails().getValue().contains("0")){
				EventFactory.getInstance().registerEvent("showCallScreenWithDialpadASMEvent", new ShowCallScreenWithDialpadASMEvent());
				Event event = EventFactory.getInstance().createEvent("showCallScreenWithDialpadASMEvent");
				event.setName("showCallScreenWithDialpadASMEvent");
				event.setCurrComponentName(e.getCurrComponentName());
				
				event.setCurrPkgClsName(e.getCurrPkgClsName());
				event.setCurrMethodName(e.getCurrMethodName());
				event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
				event.setCurrComponentName(ta.getCurrComponentName());
				event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME));
				

				reportShowCallScreenWithDialpadAttack(ir);
				ta.setCurrASMEvent(event);
			}
		}
		return this;  
	}
	
	public void reportShowCallScreenWithDialpadAttack(InstructionResponse ir){
		String permStr = Config.getInstance().getCurrCFGPermutationString();

		GenericReport rep = new GenericReport();
		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

 		rep.setInstrContainerCls(ir.getInstr().getCurrPkgClassName());
 		rep.setInstContainerMthd(ir.getInstr().getCurrMethodName());
 		rep.setPermutationStr(permStr);
 		rep.setMessage("This app can hide call-screen dialpad programmatically!!");
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
    	if(!AttackReporter.getInstance().checkIfShowCallScreenWithDialpadReportExists(rep)){
 			AttackReporter.getInstance().getShowCallScreenWithDialpadReportList().add(rep);
 			rep.printReport();
 		}
	}

}
