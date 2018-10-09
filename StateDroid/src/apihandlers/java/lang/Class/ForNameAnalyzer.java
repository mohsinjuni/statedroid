package apihandlers.java.lang.Class;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.reflection.ClassForNameEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;


public class ForNameAnalyzer extends BaseTaintAnalyzer{

	
	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public ForNameAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.ta = ta;
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(ForNameAnalyzer.class);
	}

/*
 * 
		0xc8 check-cast v11, Landroid/telephony/TelephonyManager;
		0xcc invoke-virtual v11, Ljava/lang/Object;->getClass()Ljava/lang/Class;
		0xd2 move-result-object v12
		0xd4 invoke-virtual v12, Ljava/lang/Class;->getName()Ljava/lang/String;
		0xda move-result-object v12
		0xdc invoke-static v12, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
		0xe2 move-result-object v1
 * 			
 * 
 * (non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	public Object analyzeInstruction()
	{
	
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		Register param1Reg = involvedRegisters.get(0);
		SymbolTableEntry param1Entry = this.localSymSpace.find(param1Reg.getName());
		
		SymbolTableEntry destEntry = null;
		
		if(param1Entry != null)
		{
			destEntry = new SymbolTableEntry(param1Entry); // deep copy
		   
     	    destEntry.setLineNumber(ir.getLineNumber());
    	    destEntry.getEntryDetails().setType(param1Entry.getEntryDetails().getValue());
    	    destEntry.getEntryDetails().setValue(param1Entry.getEntryDetails().getValue());
    	    destEntry.setInstrInfo(ir.getInstr().getText());
			return destEntry;
 
		}
	   return null;
	}
}
