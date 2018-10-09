package handler;



import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import configuration.Config;


public class SputHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private String instrText;
	private ArrayList<Register> involvedRegisters;

	
	public SputHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
		
	}
	
	//0x2aa sput-wide v0, Lcom/geinimi/AdServiceThread;->o J
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

//	0x2aa sput-wide v0, Lcom/geinimi/AdServiceThread;->o J
//  0x1e sput-object v1, Lcom/geinimi/AdServiceThread;->l Ljava/util/Date;	
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
		reg1 = reg1.substring(0, reg1.length()-1);
		

		usedRegisters = new String[1];
		usedRegisters[0] = reg1;
		
		ir.setUsedRegisters(usedRegisters);
		ir.setLineNumber(leftSideSplitBySpace[0]);

//		0x2aa sput-wide v0, Lcom/geinimi/AdServiceThread;->o J		
		Register r1 = new Register();
		r1.setName(reg1); // v0
		
		String methodOrObjectName = rightSideSplitBySpace[0]; 
		ir.setCallerAPIName(calledAPIName);
		ir.setMethodOrObjectName(methodOrObjectName.trim());
		ir.setReturnType(rightSideSplitBySpace[1].trim());
		
		String completeAPIName = calledAPIName.concat("->").concat(methodOrObjectName);
		ir.setQualifiedAPIName(completeAPIName);

		involvedRegisters.add(r1);
		
		return involvedRegisters;
	}
	
}
