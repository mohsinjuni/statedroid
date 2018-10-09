package patternMatcher.statemachines.csm.filereading.states;
import java.util.Hashtable;

import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;
import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.attackreporter.GenericReport;
import patternMatcher.events.csm.filereading.DataOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.csm.filereading.FileReadingStates;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;


public class DataOutputStreamDefinedState extends FileReadingStates{

	TaintAnalyzer ta;
	private SymbolSpace localSymSpace = Config.getInstance().getLocalSymbolSpace();
	
	public DataOutputStreamDefinedState(TaintAnalyzer taParam)
	{
		this.ta = taParam;
	}
	
	public DataOutputStreamDefinedState(){}
	
//		0x208 invoke-direct v0, v1, Ljava/io/DataOutputStream;-><init>(Ljava/io/OutputStream;)V
//		Coming from url.states.UrlGetOutputStreamState.

//		0x28a invoke-virtual v0, v8, v1, v2, Ljava/io/DataOutputStream;->write([B I I)V
//		v0 must have DataOutputStreamDefined state, and v8 must have FileInputStreamReaderDefinedState. We always go the state of callerEntry (i.e., v0)
	// and then check states of other objects there.
	
	@Override
	public State update(DataOutputStreamWriteEvent e) {
		
		InstructionResponse ir = (InstructionResponse) e.getEventInfo().get(InstructionResponse.CLASS_NAME);
		Register bufferReg = ir.getInvolvedRegisters().get(1);
		SymbolTableEntry bufferEntry = this.localSymSpace.find(bufferReg.getName());

		Register callerReg = ir.getInvolvedRegisters().get(0);
		SymbolTableEntry callerEntry = this.localSymSpace.find(callerReg.getName());
			
		if(bufferEntry != null){
			State currState = bufferEntry.getEntryDetails().getState();
			if(currState != null && currState instanceof FileInputStreamReaderDefinedState){
				//If needed, we can get fileName from v8 Entry here but since file name may not necessarily be hard-coded, we don't send it to attack report
				
				String urlValue = callerEntry.getEntryDetails().getValue();
				reportAttack(e, ir, urlValue);
				//TODO Report attack here with url value.
			}
		}
		return this;  //This does not matter anymore. Each object maintains its state itself.
	}
	
	public void reportAttack(DataOutputStreamWriteEvent e, InstructionResponse ir, String urlValue){
		String permStr = Config.getInstance().getCurrCFGPermutationString();

		GenericReport rep = new GenericReport();
		rep.setCompPkgName(ta.getCurrComponentPkgName());
		rep.setCompCallbackMethdName(ta.getCurrComponentCallback());
		rep.setCurrComponentClsName(ta.getCurrComponentName());

 		rep.setInstrContainerCls(ir.getInstr().getCurrPkgClassName());
 		rep.setInstContainerMthd(ir.getInstr().getCurrMethodName());
 		rep.setPermutationStr(permStr);
 		
 		String msg = "##### This app reads data from a file or reads the whole file and leaks it out to the URL = " + urlValue + " \n\n";
 		rep.setMessage(msg);
 	    rep.setSinkAPI(ir.getInstr().getText());
 	    
 		if(!AttackReporter.getInstance().checkIfFileReadingAndLeakageReportExists(rep)){
 			AttackReporter.getInstance().getFileReadingAndLeakageReportList().add(rep);
 			rep.printReport();
 		}
	}
}
