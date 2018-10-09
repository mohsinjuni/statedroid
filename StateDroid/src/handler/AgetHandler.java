package handler;

import java.util.ArrayList;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

public class AgetHandler  extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	public AgetHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
	}

	//	0xc4 aget-char v0, v9, v8

	/*
	 * aget-object v3, v7, v8

		Value stored at index v8 of array v7 is obtained and stored in v3.
	 * 
	 * 
	 *  aget = AgetTaintAnalyzer
		aget-wide = AgetTaintAnalyzer
		aget-boolean = AgetTaintAnalyzer
		aget-byte = AgetTaintAnalyzer
		aget-char = AgetTaintAnalyzer
		aget-short = AgetTaintAnalyzer


		aget-object = AgetObjectTaintAnalyzer


	 * (non-Javadoc)
	 * @see handler.BaseHandler#execute()
	 */
	public InstructionResponse execute()
	{
		String instrText = this.currInstr.getText();
		String[] splitInstr = instrText.split(" ");
		ir = new InstructionResponse();

		ArrayList<Register> involvedRegisters = new ArrayList<Register>();

		if(splitInstr != null)
		{

			String[] opcodeSplit = splitInstr[1].split("-");
			String instrType = "";

			if(opcodeSplit != null)
			{
				if(opcodeSplit.length ==1)
				{
					instrType = "I";
				}
				else
				{
					String opcodeRightSide = opcodeSplit[1]; 
					if(opcodeRightSide != null && !opcodeRightSide.isEmpty())
					{

						if(opcodeRightSide.equalsIgnoreCase("wide")
								|| opcodeRightSide.equalsIgnoreCase("boolean") 
								|| opcodeRightSide.equalsIgnoreCase("byte") 
								|| opcodeRightSide.equalsIgnoreCase("char") 
								|| opcodeRightSide.equalsIgnoreCase("short") 
								)
							instrType = opcodeRightSide;
					}
				}

			}

			Register r1 = new Register();			
			String reg1 = splitInstr[2];
			reg1 = reg1.substring(0, reg1.length()-1);			
			r1.setName(reg1);
			r1.setType(instrType);

			ir.setReturnType(instrType);

			Register r2 = new Register();
			String reg2 = splitInstr[3];
			reg2 = reg2.substring(0, reg2.length()-1);			
			r2.setName(reg2);

			Register r3 = new Register();
			String reg3 = splitInstr[4];
			r3.setName(reg3);


			ir.setUsedRegisters(new String[]{reg1, reg2, reg3}); //v0

			involvedRegisters.add(r1);
			involvedRegisters.add(r2);
			involvedRegisters.add(r3);

		}
		ir.setInvolvedRegisters(involvedRegisters);
		ir.setInstr(currInstr);
		ir.setLineNumber(splitInstr[0]);

		return ir;

	}



}
