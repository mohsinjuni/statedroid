package apihandlers.android.util.Log;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class LoggingAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private SymbolSpace globalSymSpace;
	
	
	public LoggingAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(LoggingAnalyzer.class);
	}

//	0x7c invoke-static v4, v5, Landroid/util/Log;->e(Ljava/lang/String; Ljava/lang/String;)I

// http://forums.androidcentral.com/general-help-how/141073-learn-logcat-like-pro.html	
	
	public Object analyzeInstruction()
	{

		ArrayList<Register> involvedRisters = ir.getInvolvedRegisters();
	
		Register param2Reg = involvedRisters.get(1); // first reg is just a tag
		SymbolTableEntry param2Entry = localSymSpace.find(param2Reg.getName());
		
		
		this.globalSymSpace = Config.getInstance().getGlobalSymbolSpace();
		
		SymbolTableEntry logcatEntry = globalSymSpace.find("logcat"); //Ljava/lang/Runtime;->exec uses this entry.
		
		
		if(logcatEntry != null)
		{
			EntryDetails logcatEntryDetails = logcatEntry.getEntryDetails();
			ArrayList<SourceInfo> logcatSiList = logcatEntryDetails.getSourceInfoList();

			if(param2Entry != null)
			{
				if(param2Entry.getEntryDetails().isTainted())
				{
					logcatEntryDetails.setTainted(true);
					
					ArrayList<SourceInfo> paramSiList = param2Entry.getEntryDetails().getSourceInfoList();
					
					if(paramSiList != null && paramSiList.size()>0)
					{
						if(logcatSiList == null) 
						{
							logcatSiList = new ArrayList<SourceInfo>();
						}
						for(SourceInfo si: paramSiList)
						{
							if(!logcatSiList.contains(si))
							{
								logcatSiList.add(si);
							}
						}
					}
				}
			}
			
			logcatEntryDetails.setSourceInfoList(logcatSiList);
			
			logcatEntryDetails.setType("Landroid/util/Log;"); // Just for sake of it.
			
			logcatEntry.setEntryDetails(logcatEntryDetails);
		}
		else
		{
			logcatEntry = new SymbolTableEntry();
			
			EntryDetails logcatEntryDetails = logcatEntry.getEntryDetails();
			ArrayList<SourceInfo> logcatSiList = logcatEntryDetails.getSourceInfoList();

			if(param2Entry != null)
			{
				if(param2Entry.getEntryDetails().isTainted())
				{
					logcatEntryDetails.setTainted(true);
					
					ArrayList<SourceInfo> paramSiList = param2Entry.getEntryDetails().getSourceInfoList();
					
					if(paramSiList != null && paramSiList.size()> 0)
					{
						if(logcatSiList == null)  //Redundant checks could have been avoided.
						{
							logcatSiList = new ArrayList<SourceInfo>();
						}
						for(SourceInfo si: paramSiList)
						{
							if(!logcatSiList.contains(si))
							{
								logcatSiList.add(si);
							}
						}
					}
				}
			}
			
			logcatEntryDetails.setSourceInfoList(logcatSiList);
			
			logcatEntryDetails.setType("Landroid/util/Log;"); // Just for sake of it.
			
			logcatEntry.setEntryDetails(logcatEntryDetails);
			
			logcatEntry.setName("logcat");
			globalSymSpace.addEntry(logcatEntry);
			
			globalSymSpace.logInfoSymbolSpace();
		}
		
     	logger.debug("\n LoggingAnalyzer");
	    return null;
	}
}
