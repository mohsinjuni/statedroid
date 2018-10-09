package apihandlers.android.content.pm.PackageManager;

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.appremoval.SetComponentEnabledEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class SetComponentEnabledSettingAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public SetComponentEnabledSettingAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(SetComponentEnabledSettingAnalyzer.class);
	}

//		0x24 invoke-virtual v5, Lcom/example/appRemoval2/MainActivity;->getPackageManager()Landroid/content/pm/PackageManager;
//		0x2a move-result-object v0
//		0x2c invoke-virtual v5, Lcom/example/appRemoval2/MainActivity;->getApplicationContext()Landroid/content/Context;
//		0x32 move-result-object v1
//		0x34 invoke-virtual v1, Landroid/content/Context;->getPackageName()Ljava/lang/String;
//		0x3a move-result-object v1
//		0x3c invoke-virtual v0, v1, v4, v3, Landroid/content/pm/PackageManager;->setApplicationEnabledSetting(Ljava/lang/String; I I)V

	public Object analyzeInstruction()
	{
		SymbolTableEntry returnEntry = new SymbolTableEntry();
		EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
		String text = ir.getInstr().getText();
		
		if(text.contains("0xf0 invoke-interface v8, v1, Landroid/database/Cursor;->getString(I)Ljava/lang/String;")){
//			System.out.println(text);
		}
		String pmRegName = ir.getInvolvedRegisters().get(0).getName();
		SymbolTableEntry pmEntry = localSymSpace.find(pmRegName);
		
		if(pmEntry != null){

			EventFactory.getInstance().registerEvent("setComponentEnabledEvent", new SetComponentEnabledEvent());
			Event event = EventFactory.getInstance().createEvent("setComponentEnabledEvent");
			event.setName("setComponentEnabledEvent");

			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			
			ta.setCurrCSMEvent(event);
		}
		return null;
	}
}
