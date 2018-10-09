package handler;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

public class ArrayLengthHandler  extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	public ArrayLengthHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
	}
	
	// 		0x5a array-length v10, v9
	public InstructionResponse execute()
	{
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");
		ir = new InstructionResponse();
		
		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
			
		if(splitInstr != null)
		{
			String reg2 = splitInstr[3];
			ir.setUsedRegisters(new String[]{reg2}); //v9
			
			Register r1 = new Register();
			
			String reg1 = splitInstr[2];
			reg1 = reg1.substring(0, reg1.length()-1);			
			r1.setName(reg1);
			
			Register r2 = new Register();
			r2.setName(reg2);
			
			involvedRegisters.add(r1);
			involvedRegisters.add(r2);
			
		}
		ir.setInvolvedRegisters(involvedRegisters);
		ir.setInstr(currInstr);
		ir.setLineNumber(splitInstr[0]);
		
		return ir;

	}

	
	
}
