package apihandlers.javax.net.ssl.HttpsURLConnection;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.filereading.FileDefinedEvent;
import patternMatcher.events.csm.filereading.FileReaderDefinedEvent;
import patternMatcher.events.csm.url.UrlGetOutputStreamEvent;
import patternMatcher.events.csm.url.UrlOpenConnectionEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetOutputStreamAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetOutputStreamAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		this.ta = ta;
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetOutputStreamAnalyzer.class);
	}

//		0x1f8 invoke-virtual/range v35, Ljavax/net/ssl/HttpsURLConnection;->getOutputStream()Ljava/io/OutputStream;
//		0x1fe move-result-object v36
	
	public Object analyzeInstruction(){
		Register reg0 = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry urlEntry = this.localSymSpace.find(reg0.getName());
		if(urlEntry != null){
			EventFactory.getInstance().registerEvent("urlGetOutputStreamEvent", new UrlGetOutputStreamEvent());
			Event event = EventFactory.getInstance().createEvent("urlGetOutputStreamEvent");
			event.setName("urlGetOutputStreamEvent");
			
			Hashtable eventInfo = event.getEventInfo();
			eventInfo.put(InstructionResponse.CLASS_NAME, ir);
			
			InstructionReturnValue irv = new InstructionReturnValue(urlEntry, event);
			return irv;
		}
       return null;
		
	}
}
