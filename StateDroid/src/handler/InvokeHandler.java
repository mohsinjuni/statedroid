package handler;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import models.cfg.Instruction;
import models.cfg.InstructionResponse;
import models.cfg.Register;

import org.apache.log4j.Logger;

import configuration.Config;



public class InvokeHandler extends BaseHandler {

	private Instruction currInstr;
	private InstructionResponse ir;
	private String instrText;
	private String qualifiedApiName;     //com.android.TelephonyManger->getDeviceId
	private ArrayList<Register> involvedRegisters;
	
	private static Logger logger;

	
	public InvokeHandler(Instruction instr, Instruction prev)
	{
		this.currInstr = instr;
		logger = Logger.getLogger(InvokeHandler.class);
	}
	
	public InstructionResponse execute()
	{
		instrText = currInstr.getText();
		ir = new InstructionResponse();
		
		ir = handleInstruction();

		//write logic here to handle other invokes.
			
		return ir;
	}
	

	private InstructionResponse  handleInstruction()
	{
		involvedRegisters = getInvolvedRegisters();
		ir.setInstr(currInstr);
		ir.setInvolvedRegisters(involvedRegisters);
		
		return ir;
	}

	//0x5a invoke-virtual v2, Ljava/io/File;->list()[Ljava/lang/String; 
	public ArrayList<Register> getInvolvedRegisters()
	{
		
		logger.debug(currInstr.getText());
//		System.out.println(currInstr.getText());
		String inputLine = this.currInstr.getText();
		ArrayList<Register>invokeRegisters = new ArrayList<Register>();
		
		if(!inputLine.contains("invalid_class_nam")
			&& inputLine.contains("->")
				){
		
		
			String[] totalRegisters ;
	
			String twoSidesOfArrow[] = inputLine.split("->");
			String rhsSplitByLeftParanthesis[] = twoSidesOfArrow[1].split("[(]");
			String leftSideOfArrow[] = twoSidesOfArrow[0].split(" ");
			String calledAPIName = leftSideOfArrow[leftSideOfArrow.length-1];
	//		String newCalledName = calledAPIName.substring(1, calledAPIName.length()-1); // L from start and ; from end are removed.
	
			String[] paramsString = rhsSplitByLeftParanthesis[1].split("[)]");
			String[] paramsArray = paramsString[0].split(" ");
			
			this.ir.setReturnType(paramsString[1]);
	
			String calledMethodName = rhsSplitByLeftParanthesis[0];
			
			ir.setLineNumber(leftSideOfArrow[0]);
			ir.setCallerAPIName(calledAPIName);
			ir.setMethodOrObjectName(calledMethodName);
			ir.setCalledMethodNature(  currInstr.getCalledMethodType(calledAPIName));
	
			
			qualifiedApiName = calledAPIName.concat("->").concat(calledMethodName);
			ir.setQualifiedAPIName(qualifiedApiName);
			
			String instType = leftSideOfArrow[1];
			if(instType.equalsIgnoreCase("invoke-virtual/range")
					|| instType.equalsIgnoreCase("invoke-direct/range") 
					|| instType.equalsIgnoreCase("invoke-static/range") 
					|| instType.equalsIgnoreCase("invoke-interface/range") 
					|| instType.equalsIgnoreCase("invoke-super/range") )
			{	
				
				// 0x7c invoke-virtual/range v4 ... v9, Lcom/allen/flashcardsfree/database/LocalCardDbAdapter;->updateCard(
				if(leftSideOfArrow[3].equalsIgnoreCase("...")) 
				{			
	
					String firstRegNoStr = "";
					String lastRegNoStr = "";
	
					String firstRegOfRange = leftSideOfArrow[2]; 
					String lastRegOfRange = leftSideOfArrow[4]; 
					
					// getting register number. It could be v0 or v12 or may be more v123 up to three digit. Maximum is 255, I guess.
	
					firstRegNoStr = firstRegOfRange.substring(1);
					lastRegNoStr = lastRegOfRange.substring(1,lastRegOfRange.length()-1); // last will be v12,
					
	//				System.out.println("Starting register No= " + firstRegNoStr + ", Last Reg No = " + lastRegNoStr  );
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
					
					// Now we extract parameter types. Since first register will always be the caller object, we will start from next to it.
					// 0x10 invoke-virtual/range v0 ... v5, Landroid/support/v4/app/ListFragment;->onListItemClick(Landroid/widget/ListView; Landroid/view/View; I J)V	
					
					// calledAPIName.s
				}
				else // 0x8 invoke-virtual/range v21, Lcom/allen/flashcardsfree/DashboardLayout;->getChildCount()I
				{
					String reg = leftSideOfArrow[2];
					totalRegisters = new String[1];
					totalRegisters[0] = reg.substring(0,reg.length()-1); 
					
				}
				
			}
			else  // 0x28 invoke-interface v0, v1, v2, Ljava/util/Map;->put(Ljava/lang/Object; Ljava/lang/Object;)Ljava/lang/Object;
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
			
			if(paramsArray.length != totalRegisters.length-1 && paramsArray.length > 0)
			{
				
				logger.debug("< totalRegLength>" + totalRegisters.length);
				int paramArrayIndex = 0;
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
						HashSet<String> sixtyFourBitRegisters = Config.getInstance().getSixtyFourBitRegisters();
						String str = paramsArray[paramArrayIndex].trim();
						if(sixtyFourBitRegisters.contains(str))
						{
							Register reg1 = new Register();
							
							reg1.setName(totalRegisters[i]);
							reg1.setType(paramsArray[paramArrayIndex]);
							invokeRegisters.add(reg1);
							i++;
						}
	
						r.setName(totalRegisters[i]);
						r.setType(paramsArray[paramArrayIndex]);
						paramArrayIndex++;
					}
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
					
			logger.debug("[InvokeHandler.java]");
			for(int i=0 ; i < totalRegisters.length; i++)
			{
				logger.trace( totalRegisters[i] + "   ");
			}
		
		}
		return invokeRegisters;
	}
}
