package patternMatcher.statemachines.csm.filereading;

import patternMatcher.events.csm.filereading.BufferedOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.BufferedReaderDefinedEvent;
import patternMatcher.events.csm.filereading.BufferedReaderReadDataEvent;
import patternMatcher.events.csm.filereading.CipherDefinedEvent;
import patternMatcher.events.csm.filereading.CipherDoFinalEvent;
import patternMatcher.events.csm.filereading.CipherGetInstanceEvent;
import patternMatcher.events.csm.filereading.CipherInputStreamInitEvent;
import patternMatcher.events.csm.filereading.CipherInputStreamReadEvent;
import patternMatcher.events.csm.filereading.DataOutputStreamDefinedEvent;
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



public abstract class FileReadingStates extends State{

   public State update(BufferedReaderDefinedEvent e){ return this;};	
   public State update(BufferedReaderReadDataEvent e){ return this;};	
   public State update(FileDefinedEvent e){ return this;};	
   public State update(FileReaderDefinedEvent e){ return this;};	
   public State update(FilesToStringEvent e){ return this;};	
   public State update(FileUtilsReadFileToStringEvent e){ return this;};	
   public State update(InputStreamReaderDefinedEvent e){ return this;};	
   public State update(ScannerDefinedEvent e){ return this;};	
   public State update(ScannerReadDataEvent e){ return this;};	
   
   public State update(FileInputStreamDefinedEvent e){ return this;};	
   public State update(FileInputStreamReadEvent e){ return this;};	
   public State update(DataOutputStreamDefinedEvent e){ return this;};	
   public State update(DataOutputStreamWriteEvent e){ return this;};	
   public State update(BufferedOutputStreamWriteEvent e){ return this;};	
   
   public State update(CipherGetInstanceEvent e){ return this;};	
   public State update(CipherDoFinalEvent e){ return this;};	   
   public State update(CipherDefinedEvent e){ return this;};	
   public State update(CipherInputStreamInitEvent e){ return this;};	
   public State update(CipherInputStreamReadEvent e){ return this;};	
   public State update(FileOutputStreamWriteEvent e){ return this;};	
	
}
