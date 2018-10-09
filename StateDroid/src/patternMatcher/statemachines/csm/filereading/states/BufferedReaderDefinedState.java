package patternMatcher.statemachines.csm.filereading.states;
import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.events.csm.filereading.BufferedReaderReadDataEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import patternMatcher.statemachines.csm.mediarecorder.MediaRecorderStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class BufferedReaderDefinedState extends FileReadingStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public BufferedReaderDefinedState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
	}
	
	public BufferedReaderDefinedState(){}
	
//		0x1c invoke-virtual v6, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;
//		0x22 move-result-object v10
	
	/*
	 * 
	 * (non-Javadoc)
	 * @see patternMatcher.statemachines.State#update(patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent)
	 */
	@Override
	public State update(BufferedReaderReadDataEvent e) {
		
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register brReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry returnEntry = this.localSymSpace.find(brReg.getName());
			
		if(returnEntry != null){
			Hashtable recordFieldList = returnEntry.getEntryDetails().getRecordFieldList();
			if(recordFieldList != null && recordFieldList.size() > 0){
				SymbolTableEntry brEntry = (SymbolTableEntry) recordFieldList.get("callerEntry"); 
				if(brEntry != null){
					State currState = brEntry.getEntryDetails().getState();
					if(currState != null && currState instanceof BufferedReaderDefinedState){
						String fileName = brEntry.getEntryDetails().getValue(); //BufferedReader should have file name as its value by now.
						
			        	String srcInitial = AttackReporter.getInstance().getNonAPISource();
					   	ArrayList<SourceInfo> siList = returnEntry.getEntryDetails().getSourceInfoList();
				   		if(siList == null)
				   			siList = new ArrayList<SourceInfo>();
				   		SourceInfo si = new SourceInfo();
				   		srcInitial += "-FILE";
				   		if(fileName != null && !fileName.isEmpty()){
				   			srcInitial += "(" + fileName + ")";
				   		}
				   		si.setSrcAPI(srcInitial);
						siList.add(si);
					   	returnEntry.getEntryDetails().setSourceInfoList(siList);
					   	returnEntry.getEntryDetails().setTainted(true);
					}
				}
			}
		}
		return this;  //This does not matter anymore. Each object maintains its state itself.
	}

}
