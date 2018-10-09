package handler;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
// There might be some instructions which do nothing for taint analysis like 'nop' instruction. You can handle all of them here.
public class MiscHandler  extends BaseHandler {

	private Instruction currInstr;
	
	
	public MiscHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
	}
	
	public InstructionResponse execute()
	{
		
		return new InstructionResponse();
	}
	
	
	
}
