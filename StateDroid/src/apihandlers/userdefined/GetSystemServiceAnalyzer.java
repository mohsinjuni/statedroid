package apihandlers.userdefined;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.GetSystemServiceEvent;
import patternMatcher.events.csm.StartActivityEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetSystemServiceAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetSystemServiceAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(GetSystemServiceAnalyzer.class);
	}

/*
 *		0x12 const-string v7, 'audio'
		0x16 invoke-virtual v10, v7, Lcom/example/phonecaller/MainActivity;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;
		0x1c move-result-object v0
		
		This class is supposed to generate an event with the given input value so that one can get type of systemService output.
		
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		int regCount = involvedRegisters.size();
		
		Register callerApiReg = involvedRegisters.get(0);
		
		if(regCount == 2) //caller + service name.
		{
			Register intentReg = involvedRegisters.get(1);
			SymbolTableEntry serviceNameEntry = localSymSpace.find(intentReg.getName());
			
			if(serviceNameEntry != null){
								
				EventFactory.getInstance().registerEvent("getSystemServiceEvent", new GetSystemServiceEvent());
				Event startActivityEvent = EventFactory.getInstance().createEvent("getSystemServiceEvent");
				startActivityEvent.setName("getSystemServiceEvent");
				
				SymbolTableEntry returnEntry = new SymbolTableEntry();
				Hashtable recordFieldList = returnEntry.getEntryDetails().getRecordFieldList();
				if(recordFieldList == null){
					recordFieldList = new Hashtable();
				}
				recordFieldList.put("serviceName", serviceNameEntry);
				returnEntry.getEntryDetails().setRecordFieldList(recordFieldList);
				
				InstructionReturnValue retValue = new InstructionReturnValue(returnEntry, startActivityEvent);
//				ta.setCurrCSMEvent(startActivityEvent);
				return retValue;
		
				}
			}
       logger.debug("\n GetSystemServiceAnalyzer");
////	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
