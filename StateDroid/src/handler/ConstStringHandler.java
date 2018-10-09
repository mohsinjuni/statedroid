package handler;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;


public class ConstStringHandler  extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	public ConstStringHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
	}
	
	// Instruction format => '0x18 const-string v1, ', hello' '
	public InstructionResponse execute()
	{
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");
		ir = new InstructionResponse();
		
		ir.setInstr(currInstr);
		ArrayList<Register> involvedRegisters = new ArrayList<Register>();

		
		if(splitInstr != null)
		{
			ir.setLineNumber(splitInstr[0]);
			String reg = splitInstr[2];
			reg = reg.substring(0, reg.length()-1);
			String value = splitInstr[3];

			for(int k=4; k < splitInstr.length; k++)
			{
				value += splitInstr[k];
			}
			
			ir.setUsedRegisters(new String[]{reg}); //vA
			
			Register r = new Register();
			r.setName(reg);
			r.setType("Ljava/lang/String;");
			r.setConstant(true);
			r.setTainted(false);
			r.setValue(value);
			
			involvedRegisters.add(r);
		}
		ir.setInvolvedRegisters(involvedRegisters);
		
		return ir;

	}

	
	
}
