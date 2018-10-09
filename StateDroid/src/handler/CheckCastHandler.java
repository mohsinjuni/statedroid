package handler;



import java.util.ArrayList;
import java.util.Properties;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import configuration.Config;


public class CheckCastHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private String instrText;
	private ArrayList<Register> involvedRegisters;

	
	public CheckCastHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
	}
	
	public InstructionResponse execute()
	{
		instrText = currInstr.getText();
		ir = new InstructionResponse();
		ir = handleInstruction();
			
		return ir;
	}

	private InstructionResponse  handleInstruction()
	{
		boolean result = false;
				
		involvedRegisters = getInvolvedRegisters();
		
		ir.setInvolvedRegisters(involvedRegisters);
		ir.setInstr(currInstr);

		String apiType = "";
		
		return ir;
	}

// 	0x26 check-cast v7, Landroid/telephony/TelephonyManager;	
	public ArrayList<Register> getInvolvedRegisters()
	{
		ArrayList<Register> involvedRegisters = new ArrayList<Register>();
		
		String[] usedRegisters ;

		String inputLine = this.currInstr.getText();
		String splitBySpace[] = inputLine.split(" ");
		
		String calledAPIName = splitBySpace[3];

		String reg1 = splitBySpace[2];		
		reg1 = reg1.substring(0, reg1.length()-1);
		
		usedRegisters = new String[1];
//		usedRegisters[0] = reg1; 

		ir.setUsedRegisters(usedRegisters);
		ir.setChangedRegister(reg1);
		ir.setLineNumber(splitBySpace[0]);

//	 	0x26 check-cast v7, Landroid/telephony/TelephonyManager;	
		
		Register r1 = new Register();
		r1.setName(reg1); // v7
		r1.setType(splitBySpace[3]);
	
		ir.setCallerAPIName(calledAPIName);
				
		involvedRegisters.add(r1);
		return involvedRegisters;
	}
	
}
