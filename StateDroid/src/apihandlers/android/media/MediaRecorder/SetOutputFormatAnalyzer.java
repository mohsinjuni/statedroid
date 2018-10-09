
package apihandlers.android.media.MediaRecorder;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFormatEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetVideoSourceEvent;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants;
import enums.Constants.MediaRecorderFields;

public class SetOutputFormatAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private final String fieldName = MediaRecorderFields.output_format.toString();

	public SetOutputFormatAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(SetOutputFormatAnalyzer.class);
	}

//	0x24 invoke-virtual v2, v6, Landroid/media/MediaRecorder;->setOutputFormat(I)V

// setOutputFormat(int output_format)
	public Object analyzeInstruction()
	{

		Register reg1 = ir.getInvolvedRegisters().get(0);  //v2
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v6
		
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
        

        if(callerEntry != null)
        {
        	Hashtable fieldList = (Hashtable) callerEntry.getEntryDetails().getRecordFieldList();
  
        	if(inputParamEntry != null)
        	{
        		SymbolTableEntry outputFormatEntry=null;
        		
         		String value = inputParamEntry.getEntryDetails().getValue();
        		String outputFormat="";
        		if(value != null)
        		{
        			Hashtable outputFormatConsts = Constants.getInstance().getOutputFormatConstants();
        			
        			if(outputFormatConsts.containsKey(value))
        			{
        				outputFormat = (String) outputFormatConsts.get(value);
        			}
        		}

        		if(inputParamEntry != null)
        		{
 				    outputFormatEntry =  new SymbolTableEntry(inputParamEntry); //deep copy
 				    outputFormatEntry.getEntryDetails().setValue(outputFormat);
        			outputFormatEntry.setName(fieldName); 
        			
           			if(fieldList == null)
        				fieldList = new Hashtable();
           			
        			fieldList.put(fieldName, outputFormatEntry);
        			callerEntry.getEntryDetails().setRecordFieldList(fieldList);
        			callerEntry.getEntryDetails().setRecord(true);
        			
           	  		EventFactory.getInstance().registerEvent("mediaSetOutputFormatEvent", new MediaSetOutputFormatEvent());
        			
        			//First, crate event and set it in TaintAnalyzer. Then, do what we do for each source API i.e. return an entry marked as tainted.
        			Event mediaSetOutputFormatEvent = EventFactory.getInstance().createEvent("mediaSetOutputFormatEvent");
        			mediaSetOutputFormatEvent.setName("mediaSetOutputFormatEvent");
        			        			
        			Hashtable<String, Object> eventInfo = mediaSetOutputFormatEvent.getEventInfo(); 
        			eventInfo.put(InstructionResponse.CLASS_NAME, ir); 
        			mediaSetOutputFormatEvent.setEventInfo(eventInfo);
        			
        			ta.setCurrCSMEvent(mediaSetOutputFormatEvent);

        		}
        	}
            logger.debug("\n <SetOutputFormatAnalyzer>");
        	return callerEntry;
       }
       logger.debug("\n <SetOutputFormatAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
