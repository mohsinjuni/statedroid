package patternMatcher.statemachines.csm.filereading.states;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.events.csm.filereading.FileInputStreamReadEvent;
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class FileInputStreamDefinedState extends FileReadingStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public FileInputStreamDefinedState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
	}
	
	public FileInputStreamDefinedState(){}
	
//		0x8 new-instance v9, Ljava/io/FileInputStream;
//		0xc invoke-direct v9, v8, Ljava/io/FileInputStream;-><init>(Ljava/lang/String;)V
//		0x12 new-instance v10, Ljava/io/InputStreamReader;
//		0x16 invoke-direct v10, v9, Ljava/io/InputStreamReader;-><init>(Ljava/io/InputStream;)V
//		0x1c new-instance v6, Ljava/io/BufferedReader;
	
	/*
	 * 
	 * (non-Javadoc)
	 * @see patternMatcher.statemachines.State#update(patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent)
	 */
	@Override
	public State update(InputStreamReaderDefinedEvent e) {
		
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register brReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry isReaderEntry = this.localSymSpace.find(brReg.getName());
			
		if(isReaderEntry != null){
			Hashtable recordFieldList = isReaderEntry.getEntryDetails().getRecordFieldList();
			if(recordFieldList != null && recordFieldList.size() > 0){
				SymbolTableEntry inputStreamEntry = (SymbolTableEntry) recordFieldList.get("inputStreamEntry"); 
				if(inputStreamEntry != null){
					State currState = inputStreamEntry.getEntryDetails().getState();
					if(currState != null && currState instanceof FileInputStreamDefinedState){
						String fileName = inputStreamEntry.getEntryDetails().getValue(); 
						isReaderEntry.getEntryDetails().setValue(fileName);
						State state = new FileOrInputStreaReaderDefinedState(this.ta);
						isReaderEntry.getEntryDetails().setState(state);
					}
				}
			}
		}
		return this;  //This does not matter anymore. Each object maintains its state itself.
	}
	
	@Override
	public State update(FileInputStreamReadEvent e) {
//		0x262 invoke-virtual v14, v8, Ljava/io/FileInputStream;->read([B)I

		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register bufferReg = ir.getInvolvedRegisters().get(1);
		SymbolTableEntry bufferEntry = this.localSymSpace.find(bufferReg.getName());
			
		if(bufferEntry == null){
			// Just in case, it does not exist already.
			bufferEntry = new SymbolTableEntry();
			bufferEntry.setName(bufferReg.getName());
			this.localSymSpace.addEntry(bufferEntry);
			bufferEntry = this.localSymSpace.find(bufferReg.getName()); // just update the reference.
		}
		State state = new FileInputStreamReaderDefinedState(this.ta);
		bufferEntry.getEntryDetails().setState(state);
		return this;  //This does not matter anymore. Each object maintains its state itself.
	}

}
