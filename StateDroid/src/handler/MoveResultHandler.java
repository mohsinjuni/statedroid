package handler;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

public class MoveResultHandler  extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private Instruction prevInstr;
	
	public MoveResultHandler(Instruction instr, Instruction prev)
	{
		this.prevInstr = prev;
		this.currInstr = instr;
	}
	
	public InstructionResponse execute()
	{
		ir = new InstructionResponse();

		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
		
		
//		BaseHandler invokeHandler = new InvokeHandler(prevInstr, null);
		InstructionResponse prevInstrIR = this.prevInstr.getInstResponse();
		
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");

		String reg = splitInstr[2]; 
		//It could be '0x24 move-result vAA' or 'move-result-wide vAA' or 'move-result-object vAA' 
		Register r = new Register();
		
		if(splitInstr != null)
		{
			ir.setChangedRegister(reg); //vAA

			r.setTainted(false);
			ir.setPrevInstrSource(false);
			r.setType(" ");
			r.setValue(" ");
			if(prevInstrIR != null)
			{
				if(prevInstrIR.isSourceAPI())
				{
					ir.setPrevInstrSource(true);
					r.setTainted(true);
				}
				
				// invoke-* case.
				
				String prevInstrTypeBySyntax = prevInstr.getTypeBySyntax();
				
				if(prevInstrTypeBySyntax.startsWith("invoke"))
				{
					r.setType(prevInstrIR.getReturnType()); // retun type given at the end of prev instruction.
	
					String prevInstrText = prevInstr.getText();
					String[] split1 = prevInstrText.split("->");
					String[] split2 = split1[1].split("[(]");
					
					r.setValue(split2[0]);
				}
				else if(prevInstrTypeBySyntax.startsWith("filled")) //filled-new-array
				{
					r.setType(prevInstrIR.getReturnType());
				}

			}
			r.setName(reg);
			involvedRegisters.add(r);
		}
		ir.setInvolvedRegisters(involvedRegisters);

		ir.setInstr(currInstr);
		
		return ir;

	}

	
	
}
