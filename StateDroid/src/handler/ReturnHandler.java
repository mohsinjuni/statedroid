package handler;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

public class ReturnHandler  extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private Instruction prevInstr;
	
	public ReturnHandler(Instruction instr, Instruction prev)
	{
		this.prevInstr = prev;
		this.currInstr = instr;
	}
	
	// 0x00 return-void ,0x1c return-object v0, 0x23 return v0
	public InstructionResponse execute()
	{
		ir = new InstructionResponse();

		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
		
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");
		String[] splitInstName = splitInstr[1].split("[-]");
//		ir.set
		
		ir.setLineNumber(splitInstr[0]);
		ir.setInstr(currInstr);
		
		if(splitInstr[1].equalsIgnoreCase("return-void"))
		{
			
		}
		else
		{
			String reg = splitInstr[2]; 
			ir.setChangedRegister(reg); //vAA

			Register r = new Register();
			r.setName(reg);
			involvedRegisters.add(r);

			ir.setInvolvedRegisters(involvedRegisters);
			ir.setInstr(currInstr);
		}
		return ir;

	}

	
	
}
