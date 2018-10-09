package handler;

import java.util.ArrayList;
import java.util.Hashtable;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

public class NewArrayHandler  extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private Instruction prevInstr;
	
	public NewArrayHandler(Instruction instr, Instruction prev)
	{
		this.prevInstr = prev;
		this.currInstr = instr;
	}
	
	// Instruction format is: 0x6e new-array v0, v4, [Ljava/lang/String;
	
	public InstructionResponse execute()
	{
		ir = new InstructionResponse();

		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
		
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");

		if(splitInstr != null && splitInstr.length == 5)
		{
//			System.out.println(instrText);
			ir.setLineNumber(splitInstr[0]);

			String strArray = splitInstr[2];
			strArray = strArray.substring(0, strArray.length()-1);

			String arrayType = splitInstr[4].trim(); 

			
			Register regArray = new Register();
			regArray.setName(strArray);
			regArray.setType(arrayType);
			regArray.setTainted(false);
			regArray.setConstant(false);

			involvedRegisters.add(regArray);

			String strSize = splitInstr[3];
			strSize = strSize.substring(0, strSize.length()-1);
			
			Register regSize = new Register();
			regSize.setName(strSize);
			regSize.setType("I");
			regSize.setTainted(false);
			regSize.setConstant(false);
			
			ir.setReturnType(arrayType);
			
			involvedRegisters.add(regSize);
		}
		ir.setInstr(currInstr);
		ir.setInvolvedRegisters(involvedRegisters);

		return ir;

	}

	
	
}
