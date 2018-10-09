package handler;



import java.util.ArrayList;
import java.util.Properties;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import configuration.Config;


public class GotoHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private String instrText;
	private ArrayList<Register> involvedRegisters;

	
	public GotoHandler(Instruction instr, Instruction prev)
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

//TODO handle goto instruction
//		involvedRegisters = getInvolvedRegisters();
		
		ir.setInvolvedRegisters(involvedRegisters);
		ir.setInstr(currInstr);
		
		return ir;
	}

// 	0x2 sget-object v2, Landroid/os/Build;->DEVICE Ljava/lang/String;	
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

		ir.setUsedRegisters(usedRegisters);
		ir.setChangedRegister(reg1);
		ir.setLineNumber(leftSideSplitBySpace[0]);

//	 	0x2 sget-object v2, Landroid/os/Build;->DEVICE Ljava/lang/String;	
		Register r1 = new Register();
		r1.setName(reg1); // v2
		r1.setType(rightSideSplitBySpace[1]);
	
		ir.setCallerAPIName(calledAPIName);
		ir.setMethodOrObjectName(rightSideSplitBySpace[0]);
				
		involvedRegisters.add(r1);
		return involvedRegisters;
	}
	
}
