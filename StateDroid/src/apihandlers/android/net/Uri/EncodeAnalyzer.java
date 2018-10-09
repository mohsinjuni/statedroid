package apihandlers.android.net.Uri;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.uri.UriParsedEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class EncodeAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public EncodeAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(FromPartsAnalyzer.class);
	}

	/*
	 * 
	 * Uri fromParts (String scheme, 
                String ssp, 
                String fragment)
		Creates an opaque Uri from the given components. Encodes the ssp which means this 
		method cannot be used to create hierarchical URIs.
	 * 
	 * encode(String s, String allow);
	 * encode(String s);
	 * 
	 */
	
	public Object analyzeInstruction(){
		SymbolTableEntry uriStringEntry = null; 
		SymbolTableEntry uriEntry = new SymbolTableEntry();
		Register reg1 = ir.getInvolvedRegisters().get(0);
        SymbolTableEntry stringEntry = localSymSpace.find(reg1.getName());
        
        if(stringEntry != null){
        	String value = stringEntry.getEntryDetails().getValue();
        	
        	if(value != null && !value.isEmpty()){
	        	uriStringEntry = new SymbolTableEntry();
	        	uriStringEntry.setName("uriString");  
	        	uriStringEntry.getEntryDetails().setValue(value);
	        	uriStringEntry.getEntryDetails().setType("Ljava/lang/String;");
	        	
	        	uriEntry.getEntryDetails().setType("Landroid/net/Uri;"); // it should be Landroid/net/Uri or something like that.
	        	uriEntry.getEntryDetails().setRecord(true);
	        	uriEntry.setLineNumber(ir.getLineNumber());
	        	uriEntry.setInstrInfo(ir.getInstr().getText());
	        	
	        	Hashtable recordFieldList = (Hashtable) uriEntry.getEntryDetails().getRecordFieldList();
	        	if(recordFieldList == null)
	        		recordFieldList = new Hashtable();
	        	recordFieldList.put("uriString", uriStringEntry);
	        	uriEntry.getEntryDetails().setRecordFieldList(recordFieldList);
	        	
		  		EventFactory.getInstance().registerEvent("uriParsedEvent", new UriParsedEvent());
				Event uriParsedEvent = EventFactory.getInstance().createEvent("uriParsedEvent");
				uriParsedEvent.setName("uriParsedEvent");
	
				InstructionReturnValue instrAnalysisReponse = new InstructionReturnValue(uriEntry, uriParsedEvent);
				return instrAnalysisReponse;
        	}
       }
       logger.debug("\n Uri.ParseAnalyzer");
       return uriEntry;
	}
}
	