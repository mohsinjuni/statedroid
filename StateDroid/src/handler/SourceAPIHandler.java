package handler;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
public class SourceAPIHandler extends BaseHandler {

	private Instruction currInstr;
	
	public SourceAPIHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
	}
	
	public InstructionResponse execute()
	{
		currInstr = this.currInstr; // from super-class
		String instrText = currInstr.getText();
		
		return new InstructionResponse();
	}
	
	
	
}
