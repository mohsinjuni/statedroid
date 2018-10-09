package apihandlers.java.lang.Runtime;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.RuntimeExecutionEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;

public class ExecAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private SymbolSpace globalSymSpace;

	public ExecAnalyzer(TaintAnalyzer ta){
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();
		logger = Logger.getLogger(ExecAnalyzer.class);
		this.ta = ta;
	}


/*
 * 
 * Process	exec(String[] progArray, String[] envp)
	Executes the specified command and its arguments in a separate native process.
   Process	exec(String prog, String[] envp, File directory)
	Executes the specified program in a separate native process.
   Process	exec(String[] progArray, String[] envp, File directory)
	Executes the specified command and its arguments in a separate native process.
   Process	exec(String prog, String[] envp)
	Executes the specified program in a separate native process.
   Process	exec(String prog)
	Executes the specified program in a separate native process.
   Process	exec(String[] progArray)
	Executes the specified command and its arguments in a separate native process.
 * 
 * 
 * TODO: For now, we will handle only last two (most probable usage) prototypes. And in other cases also, first param is always the program name for execution.
 *  Runtime.exec("logcat");
 *  Runtime.exec(args); 
 *
 *	0x22 invoke-virtual v7, v1, Ljava/lang/Runtime;->exec([Ljava/lang/String;)Ljava/lang/Process;
	0x28 move-result-object v4
	
 * 	(non-Javadoc)
 * @see taintanalyzer.instranalyzers.BaseTaintAnalyzer#analyzeInstruction()
 */
	
	
	// http://forums.androidcentral.com/general-help-how/141073-learn-logcat-like-pro.html
	public Object analyzeInstruction(){

		globalSymSpace = Config.getInstance().getGlobalSymbolSpace();
		
		Register callerApiReg = ir.getInvolvedRegisters().get(0);
		Register param1Reg = ir.getInvolvedRegisters().get(1);
     	   
        SymbolTableEntry callerEntry=localSymSpace.find(callerApiReg.getName());
        SymbolTableEntry paramEntry=localSymSpace.find(param1Reg.getName());

        SymbolTableEntry returnEntry = new SymbolTableEntry();
        EntryDetails returnEntryDetails = returnEntry.getEntryDetails();
        
        returnEntry.setName(""); //Actually set by move- instruction
        returnEntry.setLineNumber(ir.getLineNumber());
        returnEntry.setInstrInfo(ir.getInstr().getText());
        
        returnEntryDetails.setType(ir.getReturnType());
        
        ArrayList<SourceInfo> siList = returnEntryDetails.getSourceInfoList();
        
        String regType = ir.getInstr().getCurrPkgClassName();
        
    	boolean isLogcatOperation = false;

    	if(paramEntry != null)
    	{
    		Hashtable recordFieldList = paramEntry.getEntryDetails().getRecordFieldList();
    		if(recordFieldList != null)
    		{
    	
		    	String[] args; // = new String[fieldListSize];
		
		    	String logcatOperation = "";
		    	if(recordFieldList!= null && recordFieldList.size() > 0)
		    	{
		    		int fieldListSize = recordFieldList.size();
		    		args = new String[fieldListSize];
		    		
		    		Enumeration<String> keys = recordFieldList.keys();
		    		int i=0;
		    		while(keys.hasMoreElements())
		    		{
		    			String key = keys.nextElement();
		    			
		    			SymbolTableEntry entry = (SymbolTableEntry) recordFieldList.get(key);
		    			String value = entry.getEntryDetails().getValue();
		    			
		    			if(value != null)
		    			{
		        			args[i] = value;
		        			i++;
		    			}
		    		}
		    		//TODO We don't go to much precise details. If log is written, it is read using
		    		// logcat. For now, I am not going to specifics of .v or .d or .c etc. Will refine it if it is needed laters.
		    		if(args[0].trim().equalsIgnoreCase("logcat") || args[0].trim().startsWith("logcat")
		    				|| args[0].trim().equalsIgnoreCase("'logcat'") || args[0].trim().startsWith("'logcat"))
		    		{
		    			logcatOperation = args[0];
		    			isLogcatOperation = true;
		    		}
		    	}
		    	else{
		    		//May be, the entry itself contains some info.
		    		String value = paramEntry.getEntryDetails().getValue();
		    		
		    		if(value != null && !value.isEmpty()){
		    			if(value.trim().equalsIgnoreCase("logcat") || value.trim().startsWith("logcat")
		    					|| value.trim().equalsIgnoreCase("'logcat'") || value.trim().startsWith("'logcat")){
		    				isLogcatOperation = true;
		    				logcatOperation = value;
		    			}
		    		}
		    	}
		        if(isLogcatOperation){
					SymbolTableEntry logcatEntry = globalSymSpace.find("logcat");
					
					if(logcatEntry != null){
						if(logcatEntry.getEntryDetails().isTainted()){
							ArrayList<SourceInfo> logcatSiList = logcatEntry.getEntryDetails().getSourceInfoList();
							
							if(logcatSiList != null && logcatSiList.size() > 0 )
							{
								if(siList == null)
									siList = new ArrayList<SourceInfo>();
								siList.addAll(logcatSiList); // It's a new entry. addAll is fine here.
								returnEntryDetails.setTainted(true);
							}
						}
					}
		        }
		        returnEntryDetails.setSourceInfoList(siList);
		        returnEntryDetails.setValue(logcatOperation);
		        
		        returnEntry.setEntryDetails(returnEntryDetails);
    		}
    		
			EventFactory.getInstance().registerEvent("runtimeExecutionEvent", new RuntimeExecutionEvent());
			Event event = EventFactory.getInstance().createEvent("runtimeExecutionEvent");
			event.setName("runtimeExecutionEvent");
			
			event.getEventInfo().put("paramEntry", paramEntry);
			event.getEventInfo().put(InstructionResponse.CLASS_NAME, ir);
			
			//Even though there is return value for this API, but we are not interested in its output, as of now,
			// so, we send event from here.
 		   ta.setCurrCSMEvent(event); 
    	}
         
	    logger.debug("\n Runtime.exec analyzer");
        return returnEntry;
	}
}
