
package apihandlers.android.media.MediaRecorder;

import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFileEvent;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;
import configuration.Config;
import enums.Constants.MediaRecorderFields;

public class SetOutputFileAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;
	private final String fieldName = MediaRecorderFields.output_file.toString();

	public SetOutputFileAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(SetOutputFileAnalyzer.class);
	}

//			0x48 invoke-virtual v2, v4, Landroid/media/MediaRecorder;->setOutputFile(Ljava/lang/String;)V

// setOutputFile(String path)

//TODO: other method takes FileDescriptor as input parameter.
	public Object analyzeInstruction()
	{

		Register reg1 = ir.getInvolvedRegisters().get(0);  //v2
		Register reg2 = ir.getInvolvedRegisters().get(1);  //v4
		
        SymbolTableEntry callerEntry = localSymSpace.find(reg1.getName());
        SymbolTableEntry inputParamEntry = localSymSpace.find(reg2.getName());
        

        if(callerEntry != null)
        {
        	Hashtable fieldList = (Hashtable) callerEntry.getEntryDetails().getRecordFieldList();
        	
        	if(inputParamEntry != null)
        	{
        		SymbolTableEntry outputFileEntry=null;

        		outputFileEntry =  new SymbolTableEntry(inputParamEntry); //deep copy
        		
        		if(outputFileEntry != null)
        		{
        			outputFileEntry.setName(fieldName); // We dont need to rename either but I am doing for the sake of it.
        			// rest of the data for this entry be the same.
        			
                	logger.debug("SETOutputFileAnalyzer.java, inputParamValue = " + inputParamEntry.getEntryDetails().getValue());

           			if(fieldList == null)
        				fieldList = new Hashtable();
           			
        			fieldList.put(fieldName, outputFileEntry);
        			callerEntry.getEntryDetails().setRecordFieldList(fieldList);
        			callerEntry.getEntryDetails().setRecord(true);

          	  		EventFactory.getInstance().registerEvent("mediaSetOutputFileEvent", new MediaSetOutputFileEvent());
        			
        			//First, crate event and set it in TaintAnalyzer. Then, do what we do for each source API i.e. return an entry marked as tainted.
        			Event event = EventFactory.getInstance().createEvent("mediaSetOutputFileEvent");
        			event.setName("mediaSetOutputFileEvent");
        			        			
        			Hashtable<String, Object> eventInfo = event.getEventInfo(); 
        			eventInfo.put(InstructionResponse.CLASS_NAME, ir); 
        			event.setEventInfo(eventInfo);
        			
        			ta.setCurrCSMEvent(event);

         		}
        	}
       }
       logger.debug("\n <SetOutputFileAnalyzer>");
//	       localSymSpace.printSymbolSpace();
       return null;
		
	}
}
