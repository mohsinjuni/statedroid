package apihandlers.android.content.Intent;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.intent.IntentActionDefinedEvent;
import patternMatcher.events.csm.intent.IntentActionUriDefinedEvent;
import patternMatcher.events.csm.intent.IntentContextClsDefinedEvent;
import patternMatcher.events.csm.intent.IntentDefinedEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class InitAnalyzer extends BaseTaintAnalyzer{
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(InitAnalyzer.class);
	}

// 	Intent(Context packageContext, Class<?> cls)
//  Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telURI));
//	0x1c8 invoke-direct v8, v14, v15, Landroid/content/Intent;-><init>(Ljava/lang/String; Landroid/net/Uri;)V
	
/*
 *  Intent(Intent o)
	Intent(String action)
	Intent(String action, Uri uri)
	Intent(Context packageContext, Class<?> cls)
	Intent(String action, Uri uri, Context packageContext, Class<?> cls)
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	public Object analyzeInstruction(){
		
		//Currently, we are just going to handle only one
		// 	Intent(String action, Uri uri)
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register callerApiReg = involvedRegisters.get(0);
		SymbolTableEntry callerApiEntry = this.localSymSpace.find(callerApiReg.getName());
		
		// caller + 2 input params
		if(involvedRegisters.size() == 3){
			Register param1Reg = involvedRegisters.get(1);
			Register param2Reg = involvedRegisters.get(2);
			SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
			SymbolTableEntry param2Entry = this.localSymSpace.find(param2Reg.getName());

			if(param1Reg.getType().equalsIgnoreCase("Ljava/lang/String;")){
				// Case#1 Intent(String action, Uri uri)
				SymbolTableEntry actionEntry = null;
				SymbolTableEntry uriEntry = null;
				
				if(param1Entry != null){
					actionEntry = new SymbolTableEntry(param1Entry); //deep copy
					actionEntry.setName("actionEntry");
				}
				if(param2Entry != null){
					//We actually don't need this copy. We can just add this into the symboltable.
					uriEntry = (SymbolTableEntry) param2Entry.clone(); //shallow copy
					uriEntry.setName("uriEntry");
				}
				if(callerApiEntry != null){
					Hashtable recordFieldList = callerApiEntry.getEntryDetails().getRecordFieldList();
					
					if(recordFieldList == null) 
						recordFieldList = new Hashtable();
					if(actionEntry != null)
						recordFieldList.put(actionEntry.getName(), actionEntry);
					if(uriEntry != null)
						recordFieldList.put(uriEntry.getName(), uriEntry);
					callerApiEntry.getEntryDetails().setRecordFieldList(recordFieldList);
					
				}else{

					callerApiEntry = new SymbolTableEntry();
					Hashtable recordFieldList = callerApiEntry.getEntryDetails().getRecordFieldList();
					callerApiEntry.setName(callerApiReg.getName());
					callerApiEntry.getEntryDetails().setType(callerApiReg.getType());
					
					if(recordFieldList == null) 
						recordFieldList = new Hashtable();
					if(actionEntry != null)
						recordFieldList.put(actionEntry.getName(), actionEntry);
					if(uriEntry != null)
						recordFieldList.put(uriEntry.getName(), uriEntry);
					callerApiEntry.getEntryDetails().setRecordFieldList(recordFieldList);
					this.localSymSpace.addEntry(callerApiEntry);
				}
				
/*
		  		0xc6 new-instance v2, Landroid/content/Intent;
				0xca const-string v7, 'android.intent.action.CALL'
				0xce invoke-static v5, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
				0xd4 move-result-object v8
				0xd6 invoke-direct v2, v7, v8, Landroid/content/Intent;-><init>(Ljava/lang/String; Landroid/net/Uri;)V
 * 
 */
				EventFactory.getInstance().registerEvent("intentActionUriDefinedEvent", new IntentActionUriDefinedEvent());
				Event intentActionUriDefinedEvent = EventFactory.getInstance().createEvent("intentActionUriDefinedEvent");
				
				intentActionUriDefinedEvent.setName("intentActionUriDefinedEvent");
				intentActionUriDefinedEvent.setCurrMethodName(instr.getCurrMethodName());
				intentActionUriDefinedEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
				
				intentActionUriDefinedEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				intentActionUriDefinedEvent.setCurrComponentName(ta.getCurrComponentName());
				intentActionUriDefinedEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
							
				intentActionUriDefinedEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				ta.setCurrCSMEvent(intentActionUriDefinedEvent);
			}else{
				//Case: Intent(Context packageContext, Class<?> cls)
				// For now, we don't need to consider input parameters.

				if(callerApiEntry == null){
					callerApiEntry = new SymbolTableEntry();
					callerApiEntry.setName(callerApiReg.getName());
					callerApiEntry.getEntryDetails().setType(callerApiReg.getType());
					this.localSymSpace.addEntry(callerApiEntry);
				}
				EventFactory.getInstance().registerEvent("intentContextClsDefinedEvent", new IntentContextClsDefinedEvent());
				Event event = EventFactory.getInstance().createEvent("intentContextClsDefinedEvent");
				
				event.setName("intentContextClsDefinedEvent");
				event.setCurrMethodName(instr.getCurrMethodName());
				event.setCurrPkgClsName(instr.getCurrPkgClassName());
				
				event.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				event.setCurrComponentName(ta.getCurrComponentName());
				event.setCurrComponentPkgName(ta.getCurrComponentPkgName());
							
				event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
				ta.setCurrCSMEvent(event);
			}
		}
		// caller + 1 input param. Either intent or action.
		else if(involvedRegisters.size() == 2) {
		/*
		 * 	 *   	0xa new-instance v0, Landroid/content/Intent;
					0xe const-string v1, 'android.intent.action.CALL'
					0x12 invoke-direct v0, v1, Landroid/content/Intent;-><init>(Ljava/lang/String;)V
					
					0x42 invoke-static v1, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
					0x48 move-result-object v1
					0x4a invoke-virtual v0, v1, Landroid/content/Intent;->setData(Landroid/net/Uri;)Landroid/content/Intent;
		 * 			
		 */
			Register param1Reg = involvedRegisters.get(1);
			SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
			
			if(param1Reg.getType().equalsIgnoreCase("Ljava/lang/String;")){
				SymbolTableEntry actionEntry = null;
				if(param1Entry != null)
				{
					actionEntry = new SymbolTableEntry(param1Entry); //deep copy
					actionEntry.setName("actionEntry");

					if(callerApiEntry != null)
					{
						Hashtable recordFieldList = callerApiEntry.getEntryDetails().getRecordFieldList();
						if(recordFieldList == null) 
							recordFieldList = new Hashtable();
						recordFieldList.put(actionEntry.getName(), actionEntry);
						
						callerApiEntry.getEntryDetails().setRecordFieldList(recordFieldList);
					}
				}
			}
			//This should generate IntentActionDefinedEvent event an intent's state should be set to IntentActionDefined state.
			EventFactory.getInstance().registerEvent("intentActionDefinedEvent", new IntentActionDefinedEvent());
			Event intentActionDefinedEvent = EventFactory.getInstance().createEvent("intentActionDefinedEvent");
			intentActionDefinedEvent.setName("intentActionDefinedEvent");
						
			intentActionDefinedEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir); //Keeping immutable-class objects as keys is always a good idea.
			
			ta.setCurrCSMEvent(intentActionDefinedEvent);
		}else if(involvedRegisters.size() == 1) {
			if(callerApiEntry == null){
				callerApiEntry = new SymbolTableEntry();
				callerApiEntry.setName(callerApiReg.getName());
				callerApiEntry.getEntryDetails().setType(callerApiReg.getType());
				this.localSymSpace.addEntry(callerApiEntry);
			}
			EventFactory.getInstance().registerEvent("intentDefinedEvent", new IntentDefinedEvent());
			Event event = EventFactory.getInstance().createEvent("intentDefinedEvent");
			event.setName("intentDefinedEvent");
			event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir); //Keeping immutable-class objects as keys is always a good idea.
			ta.setCurrCSMEvent(event);
			
		}
        logger.debug("\n intent.InitAnalyzer");
//	       localSymSpace.printSymbolSpace();
	       return null;
	}
}
