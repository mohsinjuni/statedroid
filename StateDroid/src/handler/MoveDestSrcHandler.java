package handler;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

public class MoveDestSrcHandler  extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	public MoveDestSrcHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
	}
	
//	00xc move vB vA .. vB is destination and VA is source here. 
	public InstructionResponse execute()
	{
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");
		ir = new InstructionResponse();
		
		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
		
		if(splitInstr != null)
		{
			String reg1 = splitInstr[2];
			ir.setChangedRegister(reg1); //vB
			
			Register r1 = new Register();
			reg1 = reg1.substring(0, reg1.length()-1);
			r1.setName(reg1);
			
			involvedRegisters.add(r1);
			
			Register r2;
			String destReg = splitInstr[3]; // {vA}
			if(destReg != null)
			{
				ir.setUsedRegisters(new String[]{reg1}); //vB
				
				r2 = new Register();
				r2.setName(destReg);
				
				involvedRegisters.add(r2);

			}	
		
		}
		ir.setInvolvedRegisters(involvedRegisters);
		ir.setInstr(currInstr);
		ir.setLineNumber(splitInstr[0]);
		
		return ir;

	}

	
	
}
