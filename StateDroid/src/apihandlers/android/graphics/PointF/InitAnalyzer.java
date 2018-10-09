package apihandlers.android.graphics.PointF;



import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class InitAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(InitAnalyzer.class);
	}
	
//0x62 invoke-direct v4, v0, v1, Landroid/graphics/PointF;-><init>(F F)V
// 		0x7a iget v7, v4, Landroid/graphics/PointF;->x F	
	public Object analyzeInstruction()
	{
		ArrayList<Register> allRegs = ir.getInvolvedRegisters();
		Register destReg = allRegs.get(0); //v2
        SymbolTableEntry destEntry = localSymSpace.find(destReg.getName());
		
		if(allRegs.size() == 3){
	     	Register xReg = allRegs.get(1); //v0
	     	Register yReg = allRegs.get(2); //v1
	     	
	        SymbolTableEntry xEntry = localSymSpace.find(xReg.getName());
	        SymbolTableEntry yEntry = localSymSpace.find(yReg.getName());
	
	        if(destEntry != null)
	        {
	        	EntryDetails details = destEntry.getEntryDetails();
	        	Hashtable recordFieldList = (Hashtable) details.getRecordFieldList();
	        	if(recordFieldList == null){
	        		recordFieldList = new Hashtable();
	        	}
	        	if(xEntry != null){
	        		SymbolTableEntry x1Entry = new SymbolTableEntry(xEntry);
	        		x1Entry.setName("x");
	        		recordFieldList.put(x1Entry.getName(), x1Entry);
	        	}
	        	if(yEntry != null){
	        		SymbolTableEntry y1Entry = new SymbolTableEntry(yEntry);
	        		y1Entry.setName("y");
	        		recordFieldList.put(y1Entry.getName(), y1Entry);
	        	}
	        	details.setRecordFieldList(recordFieldList);
	        	destEntry.setEntryDetails(details);
	        }
	       else
	       {
	    	   destEntry = new SymbolTableEntry();
	    	   
	    	    EntryDetails details = destEntry.getEntryDetails();
	    	    details.setType(destReg.getType());
	    	    destEntry.setName(destReg.getName());
	        	if(xEntry != null){
	        		SymbolTableEntry x1Entry = new SymbolTableEntry(xEntry);
	        		details.getRecordFieldList().put("x", x1Entry);
	        	}
	        	if(yEntry != null){
	        		SymbolTableEntry y1Entry = new SymbolTableEntry(yEntry);
	        		details.getRecordFieldList().put("y", y1Entry);
	        	}
	        	destEntry.setEntryDetails(details);
	        	this.localSymSpace.addEntry(destEntry);
	       }
		}else if(allRegs.size() == 1){
			if(destEntry == null){
				destEntry = new SymbolTableEntry();
	    	    EntryDetails details = destEntry.getEntryDetails();
	    	    details.setType(destReg.getType());
	    	    destEntry.setName(destReg.getName());
	        	destEntry.setEntryDetails(details);
	        	this.localSymSpace.addEntry(destEntry);
			}
		}
        logger.debug("\n PointF -> InitAnalyzer");
       return null;
	}
}
