package patternMatcher.statemachines.csm.filereading;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.AttackObserver;
import patternMatcher.events.Event;
import patternMatcher.events.csm.filereading.BufferedOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.BufferedReaderDefinedEvent;
import patternMatcher.events.csm.filereading.BufferedReaderReadDataEvent;
import patternMatcher.events.csm.filereading.CipherDefinedEvent;
import patternMatcher.events.csm.filereading.CipherDoFinalEvent;
import patternMatcher.events.csm.filereading.CipherGetInstanceEvent;
import patternMatcher.events.csm.filereading.CipherInputStreamInitEvent;
import patternMatcher.events.csm.filereading.CipherInputStreamReadEvent;
import patternMatcher.events.csm.filereading.DataOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.FileDefinedEvent;
import patternMatcher.events.csm.filereading.FileInputStreamDefinedEvent;
import patternMatcher.events.csm.filereading.FileInputStreamReadEvent;
import patternMatcher.events.csm.filereading.FileOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.FileReaderDefinedEvent;
import patternMatcher.events.csm.filereading.FileUtilsReadFileToStringEvent;
import patternMatcher.events.csm.filereading.FilesToStringEvent;
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;
import patternMatcher.events.csm.filereading.ScannerDefinedEvent;
import patternMatcher.events.csm.filereading.ScannerReadDataEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.states.InitialState;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class FileReadingObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;
	private Hashtable attackParameters;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();

	public FileReadingObserver (){}

	public FileReadingObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.attackParameters = new Hashtable();
	}
	
	@Override
    public void update(BufferedReaderDefinedEvent e){ 
		State currState = getCurrentState(e,1);
    	if(currState != null){
    		this.state = currState;
        	state = state.update(e);
    	}
	}	
	@Override
    public void update(BufferedReaderReadDataEvent e){ 
		State currState = getCurrentStateFromCallerOfReturnEntry(e,0);
    	if(currState != null){
    		this.state = currState;
        	state = state.update(e);
    	}
	}
	
	@Override
    public void update(FileDefinedEvent e){ 
		this.state = new InitialState(this.taSubject);
    	state = state.update(e);
	}	
	
	@Override
    public void update(FileInputStreamDefinedEvent e){ 
		this.state = new InitialState(this.taSubject);
    	state = state.update(e);
	}
	
	@Override
    public void update(FileReaderDefinedEvent e){ 
		this.state = new InitialState(this.taSubject);
    	state = state.update(e);
 	}	
	
	@Override
    public void update(FilesToStringEvent e){ 
		State currState = getCurrentStateFromCallerOfReturnEntry(e,0);
    	if(currState != null){
    		this.state = currState;
        	state = state.update(e);
    	}
	}
	
	@Override
    public void update(FileUtilsReadFileToStringEvent e){ 
		State currState = getCurrentStateFromCallerOfReturnEntry(e,0);
    	if(currState != null){
    		this.state = currState;
        	state = state.update(e);
    	}
	}	
	@Override
    public void update(InputStreamReaderDefinedEvent e){ 
		State currState = getCurrentState(e,1);
    	if(currState != null){
    		this.state = currState;
        	state = state.update(e);
    	}
	}	
	@Override
    public void update(ScannerDefinedEvent e){ 
		State currState = getCurrentState(e,1);
    	if(currState != null){
    		this.state = currState;
        	state = state.update(e);
    	}
	}	
	
	
	@Override
    public void update(FileInputStreamReadEvent e){ 
//		0x262 invoke-virtual v14, v8, Ljava/io/FileInputStream;->read([B)I

		State currState = getCurrentState(e,0);
    	if(currState != null){
    		this.state = currState;
        	state = state.update(e);
    	}
	}	

	@Override
    public void update(DataOutputStreamWriteEvent e){ 
		State currState = getCurrentState(e,0);
    	if(currState != null){
    		this.state = currState;
        	state = state.update(e);
    	}
	}	

	@Override
    public void update(ScannerReadDataEvent e){ 
		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
  		SymbolTableEntry callerEntry = (SymbolTableEntry) eventInfo.get("callerEntry");
  		
    	if(callerEntry != null){
    		currState = callerEntry.getEntryDetails().getState();
        }
        if(currState != null){
    		this.state = currState;
        	state = state.update(e);
    	}
	}	

	@Override
   public void update(CipherGetInstanceEvent e){ 
		this.state = new InitialState(this.taSubject);
    	state = state.update(e);
   }	
	
	@Override
   public void update(CipherDefinedEvent e){ 
		this.state = getCurrentState(e, 0);
		if(this.state != null){
    	    state = state.update(e);
		}
	}	
	
	@Override
   public void update(CipherDoFinalEvent e){ 
		SymbolTableEntry cipherEntry = (SymbolTableEntry) e.getEventInfo().get("cipherEntry");
		if(cipherEntry != null){
			State state = cipherEntry.getEntryDetails().getState();
			if(state != null){
				this.state = state;
				state = state.update(e);
			}
		}
	}	

	@Override
   public void update(CipherInputStreamInitEvent e){ 
		this.state = new InitialState(this.taSubject);
    	state = state.update(e);
	}
	
	@Override
   public void update(CipherInputStreamReadEvent e){ 
		this.state = getCurrentState(e, 0);
		if(this.state != null){
    	    state = state.update(e);
		}
	}
	
	@Override
   public void update(FileOutputStreamWriteEvent e){ 
		this.state = getCurrentState(e, 1);
		if(this.state != null){
    	    state = state.update(e);
		}
	}

	@Override
   public void update(BufferedOutputStreamWriteEvent e){ 
		this.state = new InitialState(this.taSubject);
    	state = state.update(e);
	}

	
	public State getCurrentState(Event e, int paramNo){
		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
  		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);
  		
  		Register paramReg = ir.getInvolvedRegisters().get(paramNo);  
        SymbolTableEntry paramEntry = localSymSpace.find(paramReg.getName());
        if(paramEntry != null)
        	currState = paramEntry.getEntryDetails().getState();
		
		return currState;
	}

	public State getCurrentStateFromCallerOfReturnEntry(Event e, int paramNo){
		
		State currState = null;
		Hashtable eventInfo = (Hashtable) e.getEventInfo();
  		InstructionResponse ir = (InstructionResponse) eventInfo.get(InstructionResponse.CLASS_NAME);
  		
  		Register paramReg = ir.getInvolvedRegisters().get(paramNo);  
        SymbolTableEntry paramEntry = localSymSpace.find(paramReg.getName());
        if(paramEntry != null){
        	Hashtable recordFieldList = (Hashtable) paramEntry.getEntryDetails().getRecordFieldList();
        	if(recordFieldList != null && recordFieldList.size() > 0){
	        	SymbolTableEntry callerEntry = (SymbolTableEntry) recordFieldList.get("callerEntry");
	        	if(callerEntry != null){
	        		currState = callerEntry.getEntryDetails().getState();
	        	}
        	}
        }
		return currState;
	}
	public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}


}
