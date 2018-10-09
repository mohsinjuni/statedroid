package apihandlers.android.media.MediaRecorder;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;
import models.symboltable.EntryDetails;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import configuration.Config;

import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;

public class InitAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public InitAnalyzer(TaintAnalyzer ta)
	{
		this.ta = ta;
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();	
		logger = Logger.getLogger(InitAnalyzer.class);
	}
	
/*      mrec = new MediaRecorder();  // Works well
    mrec.setVideoSource(MediaRecorder.VideoSource.CAMERA);
    mrec.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
    mrec.setOutputFile("/sdcard/zzzzz.3gp"); 
    mrec.prepare();
    mrec.start();	
*/
	
//	0x1a new-instance v2, Landroid/media/MediaRecorder;
//	0x1e invoke-direct v2, Landroid/media/MediaRecorder;-><init>()V
	
	public Object analyzeInstruction()
	{
		Register destReg = ir.getInvolvedRegisters().get(0); //v2
     	   
        SymbolTableEntry entry = localSymSpace.find(destReg.getName());

//      0x1a new-instance v2, Landroid/media/MediaRecorder;
//		0x1e invoke-direct v2, Landroid/media/MediaRecorder;-><init>()V
        
        if(entry == null)
        {
        	//Everything should have been handled in NewInstanceAnalyzer class. But if it's still null, create a new entry.
        	entry = new SymbolTableEntry();
	   		EntryDetails entryDetails = entry.getEntryDetails();
	
		    entry.setName(destReg.getName());
		    entry.setLineNumber(ir.getLineNumber());
		    entry.setInstrInfo(ir.getInstr().getText());
		    entryDetails.setType("Landroid/media/MediaRecorder;");
		    
		    //Rest of the elements are set to the default.
	 	    entry.setEntryDetails(entryDetails);
	 	   
		   localSymSpace.addEntry(entry);
        	
        }
    	logger.debug("\n MediaRecorder.InitAnalyzer");
       return null;
	}
}
