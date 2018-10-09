package handler;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
public class MoveExceptionHandler  extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	public MoveExceptionHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
	}
	
	public InstructionResponse execute()
	{
		ir = new InstructionResponse();
		
		return ir;

	}

	
	
}
