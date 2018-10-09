package patternMatcher.statemachines.csm.runtimeexecution.states;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;

import models.cfg.InstructionResponse;
import models.cfg.MethodSignature;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.csm.RuntimeExecutionEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.runtimeexecution.RuntimeExecutionStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class InitialState extends RuntimeExecutionStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public InitialState(TaintAnalyzer taParam){
		this.ta = taParam;
	}
	
	public InitialState(){}
	
	@Override
	public State update(RuntimeExecutionEvent e) {
		
		Logger logger = Logger.getLogger("Initial State");
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		HashMap<String, String> shellInstructions = Config.getInstance().getShellCommands();

		SymbolTableEntry paramEntry = (SymbolTableEntry) e.getEventInfo().get("paramEntry");
		if(paramEntry != null){ //just doing double checking.				
			String paramValue = paramEntry.getEntryDetails().getValue();
			if(paramValue != null 
				&& !(paramValue.trim().isEmpty())
			){
				String message = "";
				String commands = "";
				Iterator itr = shellInstructions.keySet().iterator();
				while(itr.hasNext()){
					String key = (String) itr.next();
					if(paramValue.contains(key)){
						message = shellInstructions.get(key);
						commands += "\n " + key + " ------- " ;
					}
				}
				if(!message.isEmpty()){
					String instr = ir.getInstr().getText();
					String info1 = instr;
					String permStr = Config.getInstance().getCurrCFGPermutationString();
			
					GenericReport rep = new GenericReport();
 					
					rep.setCompPkgName(ta.getCurrComponentPkgName());
					rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
					rep.setCurrComponentClsName(ta.getCurrComponentName());
					
					rep.setInstrContainerCls(ir.getInstr().getCurrPkgClassName());
					rep.setInstContainerMthd(ir.getInstr().getCurrMethodName());

			 		rep.setSinkAPI(info1);
			 		rep.setPermutationStr(permStr);
			 		rep.setMessage(" ##### " + message + commands) ;
					
			 		if(!AttackReporter.getInstance().checkIfShellExecutionReportExists(rep)){
				 			AttackReporter.getInstance().getShellExecutionReportList().add(rep);
				 			rep.printReport();
			 		}
				}
			}
		}
		return this;  //This does not matter anymore. Each object maintains its state itself.
	}
}
