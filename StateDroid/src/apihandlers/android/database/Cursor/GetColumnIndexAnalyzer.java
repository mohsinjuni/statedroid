package apihandlers.android.database.Cursor;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class GetColumnIndexAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public GetColumnIndexAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(GetColumnIndexAnalyzer.class);
	}

//	0x180 const-string v9, 'category'
//	0x184 invoke-interface v3, v9, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

// Prototype:
	// public abstract int getColumnIndex (String columnName)

	public Object analyzeInstruction(){
		
		String text = ir.getInstr().getText();
		
		if(text.contains("0xe8 invoke-interface v8, v1, Landroid/database/Cursor;->getColumnIndex(")){
//			System.out.println(text);
		}
		String cursorRegName = ir.getInvolvedRegisters().get(0).getName();
		String inputParamRegName = ir.getInvolvedRegisters().get(1).getName();

		SymbolTableEntry cursorEntry = localSymSpace.find(cursorRegName);
		SymbolTableEntry inputParamEntry = localSymSpace.find(inputParamRegName);

	   String columnName = "";
	   
	   if(inputParamEntry != null){
		   columnName = inputParamEntry.getEntryDetails().getValue();
	   }
		   
	   SymbolTableEntry returnEntry = new SymbolTableEntry();
	   EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
	   returnEntryDetails.setValue(columnName); // TODO need to update it.
	   
	   returnEntry.setEntryDetails(returnEntryDetails);
        logger.debug("\n Cursor.GetColumnIndexAnalyzer");
//	       localSymSpace.printSymbolSpace();
	   return returnEntry;
	}
	
}
