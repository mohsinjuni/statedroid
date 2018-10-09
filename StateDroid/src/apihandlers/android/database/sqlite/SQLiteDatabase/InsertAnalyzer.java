package apihandlers.android.database.sqlite.SQLiteDatabase;

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
import patternMatcher.events.csm.SqlLiteDatabaseInsertEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverDeleteEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverQueryEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class InsertAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InsertAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		this.ta = ta;
		logger = Logger.getLogger(InsertAnalyzer.class);
	}

/*		0x6a invoke-virtual v0, v5, v6, v7, Landroid/database/sqlite/SQLiteDatabase;->insert(Ljava/lang/String; Ljava/lang/String; Landroid/content/ContentValues;)J
		0x70 move-result-wide v2
*/		
	
	//TODO: A lot of redundant code. Need to clean up.
	public Object analyzeInstruction()
	{
		
		//Setting up parameters
		Register dbReg = ir.getInvolvedRegisters().get(0);
		Register contentValuesReg = ir.getInvolvedRegisters().get(1);
		SymbolTableEntry dbEntry = localSymSpace.find(dbReg.getName());
		SymbolTableEntry contentValuesEntry = localSymSpace.find(contentValuesReg.getName());

		if(dbEntry != null){
			if(contentValuesEntry != null && contentValuesEntry.getEntryDetails().isTainted()){
				ArrayList<SourceInfo> siList = dbEntry.getEntryDetails().getSourceInfoList();
				ArrayList<SourceInfo> cvSiList = contentValuesEntry.getEntryDetails().getSourceInfoList();
				
				if(siList == null)
					siList = new ArrayList<SourceInfo>();
				
				for(SourceInfo si: cvSiList){
					if(!siList.contains(si))
						siList.add(si);
				}
				dbEntry.getEntryDetails().setTainted(true);
				dbEntry.getEntryDetails().setSourceInfoList(siList);
				dbEntry.getEntryDetails().setRecord(false);
				
				EventFactory.getInstance().registerEvent("sqlLiteDatabaseInsertEvent", new SqlLiteDatabaseInsertEvent());
				Event contentResolverQueryEvent = EventFactory.getInstance().createEvent("sqlLiteDatabaseInsertEvent");
				contentResolverQueryEvent.setName("sqlLiteDatabaseInsertEvent");
				contentResolverQueryEvent.getEventInfo().put("instrResponse", ir);
				contentResolverQueryEvent.getEventInfo().put("sources", siList); 
				contentResolverQueryEvent.getEventInfo().put("instrText", ir.getInstr().getText());
				
				contentResolverQueryEvent.setCurrMethodName(instr.getCurrMethodName());
				contentResolverQueryEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
				
				contentResolverQueryEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
				contentResolverQueryEvent.setCurrComponentName(ta.getCurrComponentName());
				contentResolverQueryEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
				
				contentResolverQueryEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);

				ta.setCurrCSMEvent(contentResolverQueryEvent);   

			}
		}else{
				
				dbEntry = new SymbolTableEntry();
				//Put this code into a function to avoid redundancy.
				if(contentValuesEntry != null && contentValuesEntry.getEntryDetails().isTainted()){
					ArrayList<SourceInfo> siList = dbEntry.getEntryDetails().getSourceInfoList();
					
					ArrayList<SourceInfo> cvSiList = contentValuesEntry.getEntryDetails().getSourceInfoList();
					
					if(siList == null)
						siList = new ArrayList<SourceInfo>();
					
					for(SourceInfo si: cvSiList){
						if(!siList.contains(si))
							siList.add(si);
					}
					dbEntry.getEntryDetails().setTainted(true);
					dbEntry.getEntryDetails().setSourceInfoList(siList);
					dbEntry.getEntryDetails().setRecord(false);

					EventFactory.getInstance().registerEvent("sqlLiteDatabaseInsertEvent", new SqlLiteDatabaseInsertEvent());
					Event contentResolverQueryEvent = EventFactory.getInstance().createEvent("sqlLiteDatabaseInsertEvent");
					contentResolverQueryEvent.setName("sqlLiteDatabaseInsertEvent");
					contentResolverQueryEvent.getEventInfo().put("instrResponse", ir);
					contentResolverQueryEvent.getEventInfo().put("sources", siList); 
					contentResolverQueryEvent.getEventInfo().put("instrText", ir.getInstr().getText());
					
					contentResolverQueryEvent.setCurrMethodName(instr.getCurrMethodName());
					contentResolverQueryEvent.setCurrPkgClsName(instr.getCurrPkgClassName());
					
					contentResolverQueryEvent.setCurrCompCallbackMethodName(ta.getCurrComponentCallback());
					contentResolverQueryEvent.setCurrComponentName(ta.getCurrComponentName());
					contentResolverQueryEvent.setCurrComponentPkgName(ta.getCurrComponentPkgName());
					
					contentResolverQueryEvent.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
					
					ta.setCurrCSMEvent(contentResolverQueryEvent);   
				}
				localSymSpace.addEntry(dbEntry);
			}
		return null;
	  }
 }
