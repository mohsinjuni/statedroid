package handler;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;


public class ThrowHandler  extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	public ThrowHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
	}
	
	//Handles both following items:
	// 		0x34 throw v1
	//		0x36 move-exception v6
	public InstructionResponse execute()
	{
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");
		ir = new InstructionResponse();
		
		ir.setLineNumber(splitInstr[0]);
		ir.setInstr(currInstr);
		
		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
		
		if(splitInstr != null){
			
			String reg = splitInstr[2].trim();
			ir.setUsedRegisters(new String[]{reg}); //vA
			
			Register r = new Register();
			r.setName(reg);
			r.setConstant(false);
			r.setTainted(false);
			involvedRegisters.add(r);
		}
		ir.setInvolvedRegisters(involvedRegisters);
		
		return ir;
	}

	
	
}
