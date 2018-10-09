package handler;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import configuration.Config;

public class InvokeStaticHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private String instrText;
	private String qualifiedApiName;     //com.android.TelephonyManger->getDeviceId
	private ArrayList<Register> involvedRegisters;
	
	private static Logger logger;

	
	public InvokeStaticHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
		logger = Logger.getLogger(InvokeStaticHandler.class);
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
	
		involvedRegisters = getInvolvedRegisters();
		
		ir.setInstr(currInstr);
		ir.setInvolvedRegisters(involvedRegisters);
		
		return ir;
	}

	// 0x8e invoke-static v8, v9, v10, v11, Ljava/lang/Math;->pow(D D)D

//	0x2e invoke-static v5, v6, Ljava/lang/String;->valueOf(J)Ljava/lang/String;
	
	//0x13e invoke-static Ljava/util/Locale;->getDefault()Ljava/util/Locale;
	
	public ArrayList<Register> getInvolvedRegisters()
	{
		ArrayList<Register>invokeRegisters = new ArrayList<Register>();
		
		String[] totalRegisters ;

		String inputLine = this.currInstr.getText();
		String twoSidesOfArrow[] = inputLine.split("->");
		String rhsSplitByLeftParanthesis[] = twoSidesOfArrow[1].split("[(]");
		String leftSideOfArrow[] = twoSidesOfArrow[0].split(" ");
		String calledAPIName = leftSideOfArrow[leftSideOfArrow.length-1];
		String newCalledName = calledAPIName.substring(1, calledAPIName.length()-1); // L from start and ; from end are removed.

		String[] paramsString = rhsSplitByLeftParanthesis[1].split("[)]");
		String[] paramsArray = paramsString[0].split(" ");
		
		this.ir.setReturnType(paramsString[1]);

		logger.trace("retType=> " + paramsString[1] + ", rhs => " + rhsSplitByLeftParanthesis[1]);
		String calledMethodName = rhsSplitByLeftParanthesis[0];
		
		ir.setLineNumber(leftSideOfArrow[0]);
		ir.setCallerAPIName(calledAPIName);
		ir.setMethodOrObjectName(calledMethodName);
		ir.setCalledMethodNature(  currInstr.getCalledMethodType(calledAPIName));
		

		qualifiedApiName = calledAPIName.concat("->").concat(calledMethodName);
		ir.setQualifiedAPIName(qualifiedApiName);
		
		String instType = leftSideOfArrow[1];
		if(instType.equalsIgnoreCase("invoke-static/range") )
		{	

			if(leftSideOfArrow[3].equalsIgnoreCase("...")) 
			{			

				String firstRegNoStr = "";
				String lastRegNoStr = "";

				String firstRegOfRange = leftSideOfArrow[2]; 
				String lastRegOfRange = leftSideOfArrow[4]; 

				firstRegNoStr = firstRegOfRange.substring(1);
				lastRegNoStr = lastRegOfRange.substring(1,lastRegOfRange.length()-1); // last will be v12,
				
				int firstRegNo = Integer.parseInt(firstRegNoStr);
				int lastRegNo = Integer.parseInt(lastRegNoStr);
				
				totalRegisters = new String[lastRegNo-firstRegNo+1];
				
				int startingRegNo = firstRegNo;
				//creating range of registers now. v5 ... v11 for example.
				for(int i = 0; i <= lastRegNo-firstRegNo; i++ )
				{
					String reg = String.valueOf('v').concat(String.valueOf(startingRegNo++));
					
					totalRegisters[i] = reg;
				}
			}
			else // 0x8 invoke-virtual/range v21, Lcom/allen/flashcardsfree/DashboardLayout;->getChildCount()I
			{
				String reg = leftSideOfArrow[2];
				totalRegisters = new String[1];
				totalRegisters[0] = reg.substring(0,reg.length()-1); 
				
			}
			
		}
		else  	
		{
			int apiCallIndex = leftSideOfArrow.length-1; // index of Ljava call. 
			int regCount = apiCallIndex - 2; // 5-2 = 3
			totalRegisters = new String[regCount];
			
			int k = 2;
			for(int i=0; i < regCount; i++)
			{
				String register = leftSideOfArrow[k++];
				totalRegisters[i] = register.substring(0,register.length()-1); 
//				System.out.println(totalRegisters[i]);
			}

		}
		
		ir.setUsedRegisters(totalRegisters);
		logger.debug("< totalRegLength>" + totalRegisters.length);
		
// Another special case		0x8e invoke-static v8, v9, v10, v11, Ljava/lang/Math;->pow(D D)D
// 0x16a invoke-static v3, v4, Ljava/lang/String;->valueOf(J)Ljava/lang/String;		
		
		if(paramsArray != null)
		{
			logger.debug("paramsArrayLength>>> " + paramsArray.length);
			if(paramsArray.length != totalRegisters.length && paramsArray.length > 0)
			{
				int paramArrayIndex = 0;
				for(int i=0; i < totalRegisters.length; i++)
				{
					Register r = new Register();
					String type = paramsArray[paramArrayIndex];
					HashSet<String> sixtyFourBitRegisters = Config.getInstance().getSixtyFourBitRegisters();
					if(sixtyFourBitRegisters.contains(type))
					{
						Register reg1 = new Register();
						
						reg1.setName(totalRegisters[i]);
						reg1.setType(type);
						invokeRegisters.add(reg1);
						i++;
					}
	
					r.setName(totalRegisters[i]);
					r.setType(type);
					paramArrayIndex++;
					
					invokeRegisters.add(r);
	
				}
			}
			else  // normal case
			{
				for(int i=0; i < totalRegisters.length; i++)
				{
					Register r = new Register();
					if(i ==0)
					{
						r.setName(totalRegisters[i]); // name is v0 or v1.
						r.setType(calledAPIName); // Ljava/lang/ etc.
					}
					else
					{
						r.setName(totalRegisters[i]);
						r.setType(paramsArray[i-1]);
					}
					invokeRegisters.add(r);
				}
			}
		}
				
		logger.trace("[InvokeStaticHandler.java]");
		for(int i=0 ; i < totalRegisters.length; i++)
		{
			logger.trace( totalRegisters[i] + "   ");
		}

		return invokeRegisters;
	}
	

	
}
