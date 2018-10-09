
package apihandlers.javax.crypto.Cipher;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.InstructionReturnValue;
import models.cfg.Register;
import models.symboltable.SourceInfo;
import models.symboltable.SymbolSpace;
import models.symboltable.SymbolTableEntry;

import org.apache.log4j.Logger;

import patternMatcher.events.Event;
import patternMatcher.events.EventFactory;
import patternMatcher.events.csm.filereading.CipherDefinedEvent;
import patternMatcher.events.csm.filereading.CipherDoFinalEvent;

import configuration.Config;

import apihandlers.android.app.Activity.InitAnalyzer;
import taintanalyzer.TaintAnalyzer;
import taintanalyzer.instranalyzers.BaseTaintAnalyzer;

public class DoFinalAnalyzer extends BaseTaintAnalyzer{

	private InstructionResponse ir;
	private Instruction instr;
	private TaintAnalyzer ta;
	private SymbolSpace localSymSpace;

	public DoFinalAnalyzer(TaintAnalyzer ta)
	{
		ir = ta.getIr();
		instr = ta.getIr().getInstr();
		this.localSymSpace = Config.getInstance().getLocalSymbolSpace();		
		logger = Logger.getLogger(DoFinalAnalyzer.class);
	}

		// v0 could be anything, where as we are interested in v1, so just return it as it is.
//		0x4e invoke-virtual v0, v1, Ljavax/crypto/Cipher;->doFinal([B)[B
//		0x54 move-result-object v7
	public Object analyzeInstruction(){
		ArrayList<Register> involvedRegisters = ir.getInvolvedRegisters();
		int regCount = involvedRegisters.size();
		Register reg0 = involvedRegisters.get(0);
		SymbolTableEntry cipherEntry = this.localSymSpace.find(reg0.getName());
		
		
		EventFactory.getInstance().registerEvent("cipherDoFinalEvent", new CipherDoFinalEvent());
		Event event = EventFactory.getInstance().createEvent("cipherDoFinalEvent");
		event.setName("cipherDoFinalEvent");
		
		Hashtable eventInfo = event.getEventInfo();
		eventInfo.put("oldIR", ir);
		eventInfo.put("cipherEntry", cipherEntry);
//		event.setEventInfo(eventInfo);
		SymbolTableEntry retEntry = new SymbolTableEntry();
			

		if(regCount == 2 || regCount == 4 ){
			//byte[]	doFinal(byte[] input)
			// byte[]	doFinal(byte[] input, int inputOffset, int inputLen)
			SymbolTableEntry inputByteEntry = this.localSymSpace.find(involvedRegisters.get(1).getName());

			if(inputByteEntry != null){
				eventInfo.put("inputByteEntry", inputByteEntry);
	        	retEntry = new SymbolTableEntry(inputByteEntry); //deep copy
			}
			
			retEntry.getEntryDetails().setType("[B");
			
			event.setEventInfo(eventInfo);
			InstructionReturnValue irv = new InstructionReturnValue(retEntry, event);
			return irv;
			
		}else if(regCount == 5 || regCount == 6){
//			int	doFinal(byte[] input, int inputOffset, int inputLen, byte[] output)
			SymbolTableEntry inputByteEntry = this.localSymSpace.find(involvedRegisters.get(1).getName());
			SymbolTableEntry outputByteEntry = this.localSymSpace.find(involvedRegisters.get(4).getName());
			if(inputByteEntry != null){
				eventInfo.put("inputByteEntry", inputByteEntry);
			}
			if(outputByteEntry != null){
				eventInfo.put("outputByteEntry", outputByteEntry);
			}
			event.setEventInfo(eventInfo);
			ta.setCurrCSMEvent(event);
			return null;
		}
		
       return null;
	}

}
