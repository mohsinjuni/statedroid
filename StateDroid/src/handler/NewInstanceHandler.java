package handler;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

public class NewInstanceHandler  extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private Instruction prevInstr;
	
	public NewInstanceHandler(Instruction instr, Instruction prev)
	{
		this.prevInstr = prev;
		this.currInstr = instr;
	}
	
	// Instruction format is '0x14 new-instance v0, Lcom/test/maliciousactivity/User;'
	public InstructionResponse execute()
	{
		ir = new InstructionResponse();

		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
		
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");

		ir.setLineNumber(splitInstr[0]);

		String reg = splitInstr[2];
		reg = reg.substring(0, reg.length()-1);
		
		String instanceType = splitInstr[3]; //Lcom/test/maliciousactivity/User;

		if(splitInstr != null)
		{
			Register r = new Register();
			r.setName(reg);
			r.setType(instanceType);
			r.setTainted(false);
			r.setConstant(false);

			ir.setChangedRegister(reg); //vAA
			ir.setInstr(currInstr);
			
			involvedRegisters.add(r);
		}
		ir.setInvolvedRegisters(involvedRegisters);

		return ir;

	}

	
	
}
