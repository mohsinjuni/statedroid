package apihandlers.userdefined;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
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
import patternMatcher.events.csm.StartActivityEvent;
import patternMatcher.events.csm.context.StartActivityIntentEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class StartActivityAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public StartActivityAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(StartActivityAnalyzer.class);
	}

/*
 *		0x1d2 invoke-virtual v0, v8, Lcom/example/phonecaller/MainActivity;->startActivity(Landroid/content/Intent;)V
 * 
 * This is an important analyzer class. It basically handles a part of intent-communication, system-defined uris, browsing, 
 * phone calling etc.
 * 
 * 
 * startActivity(Intent intent)
		Same as startActivity(Intent, Bundle) with no options specified.
	startActivity(Intent intent, Bundle options)
		Launch a new activity.
		
		+ others.

 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		int regCount = involvedRegisters.size();
		
		Register callerApiReg = involvedRegisters.get(0);
		
		if(regCount == 2) //caller + intent
		{
			// Just intent is being passed.
			Register intentReg = involvedRegisters.get(1);
			SymbolTableEntry intentEntry = localSymSpace.find(intentReg.getName());
			
			if(intentEntry != null)
			{
				EventFactory.getInstance().registerEvent("startActivityIntentEvent", new StartActivityIntentEvent());
				
				Event startActivityIntentEvent = EventFactory.getInstance().createEvent("startActivityIntentEvent");
				
				startActivityIntentEvent.setName("startActivityIntentEvent");
				startActivityIntentEvent.setCurrMethodName(instr.getCurrMethodName());
				startActivityIntentEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
				
				startActivityIntentEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				startActivityIntentEvent.setCurrComponentName(ta.getCurrComponentName());
				startActivityIntentEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
				
				startActivityIntentEvent.getEventInfo().put("intent", intentEntry );
				startActivityIntentEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);


				
				ta.setCurrCSMEvent(startActivityIntentEvent);
		
				}
			}
			
       logger.debug("\n startActivityAnalyzer");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
