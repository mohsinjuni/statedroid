package handler;



import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import configuration.Config;


public class IputHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private String instrText;
	private ArrayList<Register> involvedRegisters;

	
	public IputHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
		
	}
	
	public InstructionResponse execute()
	{
		instrText = currInstr.getText();
		ir = new InstructionResponse();
		
		ir = handleInstruction();
			
		return ir;
	}
	

	private InstructionResponse  handleInstruction()
	{
		boolean result = false;
				
		involvedRegisters = getInvolvedRegisters();
		
		ir.setInvolvedRegisters(involvedRegisters);
		ir.setInstr(currInstr);

		String apiType = "";
		
		return ir;
	}

//	0x26 iput-object v0, v3, Lcom/test/maliciousactivity/MainActivity;->myUser Lcom/test/maliciousactivity/User;
	public ArrayList<Register> getInvolvedRegisters()
	{
		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
		
		String[] usedRegisters ;

		String inputLine = this.currInstr.getText();
		String splitByArrow[] = inputLine.split("->");
		String leftSideSplitBySpace[] = splitByArrow[0].split(" ");
		String rightSideSplitBySpace[] = splitByArrow[1].split(" ");
		
		String calledAPIName = leftSideSplitBySpace[leftSideSplitBySpace.length-1];

		String reg1 = leftSideSplitBySpace[2];
		String reg2 = leftSideSplitBySpace[3];
		
		reg1 = reg1.substring(0, reg1.length()-1);
		reg2 = reg2.substring(0, reg2.length()-1);
		
		usedRegisters = new String[1];
		usedRegisters[0] = reg1;

		ir.setUsedRegisters(usedRegisters);
		ir.setChangedRegister(reg2);
		ir.setLineNumber(leftSideSplitBySpace[0]);


//		0x26 iput-object v0, v3, Lcom/test/maliciousactivity/MainActivity;->myUser Lcom/test/maliciousactivity/User;			
		Register r1 = new Register();
		r1.setName(reg1); // v0

		Register r2 = new Register();
		r2.setName(reg2); // v3
		r2.setReferenceObject(rightSideSplitBySpace[0]);  //myUser. May be, it should be called object instead of reference one.
		r2.setCallerObjectType(calledAPIName);  //Lcom/test/maliciousactivity/MainActivity;
		
		String objectReturnType = rightSideSplitBySpace[1]; 
		r2.setType(objectReturnType);  // Lcom/test/maliciousactivity/User;
		ir.setReturnType(objectReturnType);
		
		// Based on my understanding of caller, reference objects, I will write logic accordingly there. 
		
		ir.setCallerAPIName(calledAPIName);
		ir.setMethodOrObjectName(rightSideSplitBySpace[0]);
		
		String completeAPIName = calledAPIName.concat("->").concat(rightSideSplitBySpace[0]);
		ir.setQualifiedAPIName(completeAPIName);
		
		involvedRegisters.add(r1);
		involvedRegisters.add(r2);
		
		return involvedRegisters;
	}
	
}
