package apihandlers.java.net.URL;

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.url.UrlOpenConnectionEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class OpenConnectionAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public OpenConnectionAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(OpenConnectionAnalyzer.class);
	}
//		0x110 invoke-virtual/range v34, Ljava/net/URL;->openConnection()Ljava/net/URLConnection;
//		0x116 move-result-object v35

	public Object analyzeInstruction()
	{
		Register reg0 = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry urlEntry = this.localSymSpace.find(reg0.getName());
		if(urlEntry != null){
			EventFactory.getInstance().registerEvent("urlOpenConnectionEvent", new UrlOpenConnectionEvent());
			Event event = EventFactory.getInstance().createEvent("urlOpenConnectionEvent");
			event.setName("urlOpenConnectionEvent");
			
			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			
			InstructionReturnValue irv = new InstructionReturnValue(urlEntry, event);
			return irv;
		}
       return null;
		
	}
}
