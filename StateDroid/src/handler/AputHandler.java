package handler;



import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import configuration.Config;


public class AputHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private String instrText;
	private ArrayList<Register> involvedRegisters;

	// 		0x74 aput-object v0, v5, v1
	public AputHandler(Instruction instr, Instruction prev)
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

//	// 		0x74 aput-object v0, v5, v1   ==> v0=source, v5=destination, v1= index
	public ArrayList<Register> getInvolvedRegisters()
	{
		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
		
		String[] usedRegisters ;

		String inputLine = this.currInstr.getText();
		String splitBySpace[] = inputLine.split(" ");
		
		String calledAPIName = splitBySpace[splitBySpace.length-1];

		String reg1 = splitBySpace[2];
		String reg2 = splitBySpace[3];
		String reg3 = splitBySpace[4];
		
		reg1 = reg1.substring(0, reg1.length()-1); //v0
		reg2 = reg2.substring(0, reg2.length()-1); //v5   ..removes , from the end
		
		
		usedRegisters = new String[2];
		usedRegisters[0] = reg1;  //v0 = src array
		usedRegisters[1] = reg3;  //v1 = index

		ir.setUsedRegisters(usedRegisters);
		ir.setChangedRegister(reg2);
		ir.setLineNumber(splitBySpace[0]);


//		0x74 aput-object v0, v5, v1
		Register r1 = new Register();
		r1.setName(reg1); // v0

		Register r2 = new Register();
		r2.setName(reg2); // v5

		Register r3 = new Register();
		r3.setName(reg3); // v1	
		
		involvedRegisters.add(r1);
		involvedRegisters.add(r2);
		involvedRegisters.add(r3);
		
		return involvedRegisters;
	}
	
}
