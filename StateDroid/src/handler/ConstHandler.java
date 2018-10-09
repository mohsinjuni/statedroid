package handler;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;


public class ConstHandler  extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	public ConstHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
	}
	
	// Instruction format => 
	// 0x0 const/4 v5, 0
	// 0x12 const v4, 2131165187 # [1.7944584027819245e+38]
	// 0x14 const/high16 v0, 32514 # [1.7279963945203906e+38]
	public InstructionResponse execute()
	{
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");
		ir = new InstructionResponse();
		
		ir.setLineNumber(splitInstr[0]);
		ir.setInstr(currInstr);
		
		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
		
		if(splitInstr != null)
		{
			String reg = splitInstr[2];
			reg = reg.substring(0, reg.length()-1);
			String value = splitInstr[3];
			
			ir.setUsedRegisters(new String[]{reg}); //vA
			
			Register r = new Register();
			r.setName(reg);
			r.setConstant(true);
			r.setTainted(false);
			r.setValue(value);
			
			involvedRegisters.add(r);
		}
		ir.setInvolvedRegisters(involvedRegisters);
		
		return ir;

	}

	
	
}
