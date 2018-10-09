package decompilation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import models.cfg.APK;
import models.cfg.BasicBlock;
import models.cfg.CFG;
import models.cfg.CFGComponent;
import models.cfg.ClassObj;
import models.cfg.Instruction;
import models.cfg.MethodSignature;
import models.cfg.Package;
import models.cfg.Parameter;
import models.manifest.AndroidManifest;
import models.manifest.ComponentManifest;

import org.apache.log4j.Logger;

import enums.MethodTypes;

public class ByteCodeParser {

	private static Logger logger;
	//	private Hashtable componentsList;


	public ByteCodeParser()
	{

	}

	public APK getAPKInfo(String apkfilePath)  // Path to input .apk file. The same folder will have .apk.txt, and .apk.xml file
	{
		String byteCodeFilePath = apkfilePath.concat(".txt");
		File f = new File(byteCodeFilePath);
		APK apkObj = null;
		if(f.exists()){
			apkObj = getListOfClasses(byteCodeFilePath);
			ManifestParser manifestParser = new ManifestParser();
			apkObj = manifestParser.getManifestInfo(apkfilePath, apkObj);
		}
		return apkObj;
	}

	public APK getListOfClasses(String inputFilePath)
	{
		CFGComponent apk = new APK();
		logger = Logger.getLogger(ByteCodeParser.class);

		String outputFileName = inputFilePath; // + ".txt";
		BufferedReader br;
		ArrayList<CFGComponent> currntPkgList = apk.getCompCollection();

		CFG currentMethod = null; 
		String currMethodName = "";
		String currClsName = "";
		String currPackgName="";
		String currPkgClsName = "";
		String currParentClassName = "";
		String exceptionThrowerBBKey = null;
		Instruction prevInstr = null;
		Instruction nextInstr = null;

		BasicBlock currentBB = null;
		String line ="";

		try {
			br = new BufferedReader(new FileReader(new File(outputFileName).getAbsolutePath()));
			try 
			{

				line = br.readLine();
				while( line != null && !line.isEmpty())
				{
					//					 System.out.println(line);
					if(line.startsWith("L"))
					{
						currentMethod = null;
						String lineSplits[] = line.split(";");
						String packFileName = lineSplits[0];
						String packName[] = packFileName.split("/");
						int packLen = packName.length;

						currClsName = packName[packLen-1];

						// Lcom/adpooh/AdpoohCertSign/b; a (Ljava/lang/String;)Ljava/lang/String; 5
						currPkgClsName = packFileName;

						currPackgName = "";
						for(int i=0; i < packLen-1; i++)
						{
							currPackgName += packName[i];
							if(i != packLen-2)
								currPackgName += "/";
						}
						//						 System.out.println(packgName);
						CFGComponent currentPackage = null;
						int index= -1;

						boolean isExistingPackage = false;
						if(currntPkgList.size() >  0)
						{
							for (CFGComponent pkg3 :  currntPkgList) 
							{
								index++;
								Package pkg2 = new Package();
								pkg2 = (Package) pkg3;
								if(pkg2 != null && currPackgName.equals(pkg2.getKey()) )
								{
									isExistingPackage = true;
									currentPackage = pkg2;
									break;
								}

							}
						}

						ClassObj cls= null; 

						if(currentPackage == null)
						{
							currentPackage = new Package();
							currentPackage.setKey(currPackgName);
						}

						int clsIndex = -1;
						boolean existingClass = false;
						ArrayList<CFGComponent> clsList = currentPackage.getCompCollection();
						if(clsList.size() > 0)
						{
							for(CFGComponent cls1 : clsList)
							{
								clsIndex++;
								ClassObj cls2 = new ClassObj();

								cls2 = (ClassObj) cls1;
								if(cls2 != null && cls2.getKey().equals(currClsName))
								{
									cls = cls2;
									existingClass = true;

									break;
								}
							}
						}
						if(cls == null)
						{
							cls = new ClassObj(currClsName);
							prevInstr = null; 
						}


						String lineSplitWithSpace[] = line.split(" ");
						String methodName = lineSplitWithSpace[1];

						ArrayList<CFGComponent> mthdList = cls.getCompCollection();
						int mthdIndex = -1;
						boolean isExistingMethod = false;
						if(mthdList!=  null && mthdList.size() > 0)
						{
							for(CFGComponent tempCFG : mthdList)
							{
								mthdIndex++;
								CFG mthd = new CFG();
								mthd = (CFG) tempCFG;
								MethodSignature mSig = (MethodSignature) getMethodSignatureByLine(line);
								if(mthd != null && mthd.getSignature().equals(mSig))
								{
									currentMethod = mthd;
									isExistingMethod = true;
									break;
								}
							}
						}
						if(currentMethod == null)
						{
							String clsType = "";
							currentMethod = new CFG(methodName);
						}
						prevInstr = null; 

						if(!existingClass)
						{
							//							 currentMethod.setType(getMethodType(methodName));
							currentMethod.setCurrPkgClassName(currPkgClsName);
							currentMethod.setCurrClassName(currClsName);

							currMethodName = currentMethod.getKey();
							currentMethod.setSignature(getMethodSignatureByLine(line));

							cls.addItem(currentMethod);
							cls.setCurrPkgClassName(currPkgClsName);
							cls.setParentName(currParentClassName);
							currentPackage.addItem(cls);
						}
						else 
						{
							if( !isExistingMethod)
							{
								currMethodName = currentMethod.getKey();
								//								 currentMethod.setType(getMethodType(methodName));
								currentMethod.setSignature(getMethodSignatureByLine(line));
								currentMethod.setCurrPkgClassName(currPkgClsName);
								currentMethod.setCurrClassName(currClsName);

								cls.addItem(currentMethod);
							}
							currentPackage.setItem(clsIndex, cls);
						}

						if(isExistingPackage)
							apk.setItem(index, currentPackage);
						else
							apk.addItem(currentPackage);

						line = br.readLine();

					}
					//[currClass]=Lcmp/netsentry/backend/Bootstrapper$1; : [parentClass]=Ljava/lang/Object;
					else if (line.startsWith("[currClass"))
					{
						String[] split1 = line.split(":");
						String[] split2 = split1[1].split("=");
						currParentClassName = split2[1]; // we don't check values for super classes that start with Landroid, Ljava etc.
						//						 System.out.println()
						line = br.readLine();
					}
					else  //if(line.startsWith(currMethodName))
					{
						BasicBlock bb = new BasicBlock();

						String splitLine[] = line.split(" ");
						// get next and previous pointers and set predecessors and successors

						String bbKey = generateBBKey(splitLine[0].trim(), currPkgClsName);
						bb.setKey(bbKey);
						setBBPredSuccessorsAndRoot(line, bb, currPkgClsName);

						currentBB = bb;
						String instr="";
						line = br.readLine();

						while( (null != line) && (line.startsWith("\t\t0"))) // \t\t0 is for instruction
						{
							//								 System.out.println(instr);
							Instruction instruct = new Instruction();
							instruct.setText(line);
							instruct.setCurrMethodName(currMethodName);
							instruct.setCurrClassName(currClsName);
							instruct.setCurrPkgName(currPackgName);
							instruct.setCurrBBKey(bbKey);

							String pkgClsName = currPackgName.concat("/").concat(currClsName).concat(";");
							instruct.setCurrPkgClassName(pkgClsName);

							if(line.contains("throw")){
								String[] split1 = line.split(" ");
								if(split1[1].equals("throw")){
									exceptionThrowerBBKey = bb.getKey();
								}
							}
							else if(line.contains("move-exception")){
								bb.getPredecessors().add("exceptionCatcher");

								if(exceptionThrowerBBKey != null && !exceptionThrowerBBKey.isEmpty()){
									bb.setText(exceptionThrowerBBKey);
								}
								exceptionThrowerBBKey = "";
							}
							bb.addItem(instruct);

							if(prevInstr != null){
								instruct.setPrevInstr(prevInstr);
								prevInstr.setNextInstr(instruct);
							}
							prevInstr = instruct;
							line = br.readLine();							 

							//								 System.out.println(instr);

						}
						currentMethod.addItem(bb);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}

		return (APK) apk;

	}

	//TODO Handle over-loaded functions and generate basicblock keys accordingly
	public String generateBBKey(String bbID, String pkgName)
	{
		//		Lcom/test/maliciousactivity/Password; <init> ()V 1
		//		<init>-BB@0x0 0x0 0x10[ NEXT =  ] [ PREV = ] 

		String key = "";

		key = key.concat(pkgName).concat("-").concat(bbID);

		return key;

	}

	public void setBBPredSuccessorsAndRoot(String line, BasicBlock bb, String pkgName)
	{
		// 	function1-BB@0x0 0x0 0x2a[ NEXT = 0x26-0x2a-function1-BB@0x2a 0x26-0x50-function1-BB@0x50  ] [ PREV = ] 

		ArrayList<String> successors = new ArrayList<String>();

		logger.trace(" line -> " + line);
		String nextSplit[] = line.split("[=]");
		String nextSplit2[] = nextSplit[1].split("\\] \\[");


		//		if(nextSplit2.length)
		String nextString = nextSplit2[0];
		String nextPointers[] = nextString.split(" ");

		String funcNameArr1[] = nextSplit[0].split(" ");

		String funcNameArr2[] = funcNameArr1[0].split("-");
		String funcName = funcNameArr2[0];

		if(funcNameArr1[1].equalsIgnoreCase("0x0")) // 0x0
		{
			bb.setBbPosition("root");
		}

		if(nextPointers != null && nextPointers.length > 0)
		{
			for(String str: nextPointers)
			{
				if(!str.trim().isEmpty())
				{
					String nextAddrArr[] = str.split("-");				

					String nextAddr =  pkgName.concat("-").concat(nextAddrArr[2]).concat("-").concat(nextAddrArr[3].trim());
					if(!successors.contains(nextAddr))
						successors.add(nextAddr);
				}
			}
		}

		ArrayList<String> predecessors = new ArrayList<String>();

		//		nextSplit
		//		if(nextSplit2.length)
		String prevString = nextSplit[2];
		String prevPointers[] = prevString.split("\\]");

		String prevPtrStr = prevPointers[0];

		String prevPtrStrArr[] = prevPtrStr.split(",");
		if(prevPtrStrArr != null && prevPtrStrArr.length > 0)
		{
			for(String str: prevPtrStrArr)
			{
				if(! str.trim().isEmpty())
				{
					str = pkgName.concat("-").concat(str.trim());
					if(! predecessors.contains(str))
						predecessors.add(str);
				}
			}
		}
		bb.setPredecessors(predecessors);
		bb.setSuccessors(successors);		
	}

	public MethodSignature getMethodSignatureByLine(String instrLine)
	{
		MethodSignature ms = new MethodSignature();
		//Write instruction parser to generate Method Signature.
		// Lcom/test/maliciousactivity/MainActivity; sendsms (Ljava/lang/String; 
		// Ljava/lang/String; Ljava/lang/String; Landroid/content/Context;)V 11

		ArrayList<Parameter> params = new ArrayList<Parameter>();

		String instrSplitBySpace[] = instrLine.split(" ");
		String instrSplitByRightParanthesis[] = instrLine.split("[)]");
		String rightSideOfParanthesis[] = instrSplitByRightParanthesis[1].split(" ");
		String regCountStr = rightSideOfParanthesis[1];
		int regCount = Integer.parseInt(regCountStr);

		ms.setMaxRegNo(regCount);
		ms.setName(instrSplitBySpace[1]);
		ms.setPkgClsName(instrSplitBySpace[0]);

		String returnType = "";
		if(!instrSplitByRightParanthesis[0].endsWith("("))
		{
			String instrLeftSide[] = instrSplitByRightParanthesis[0].split("[(]");
			String paramArray[] = instrLeftSide[1].split(" ");

			for(int i= 0; i <= paramArray.length-1;  i++)
			{
				Parameter param = new Parameter();
				String regChar = "v";
				String regName = regChar.concat(String.valueOf(regCount));
				param.setName(regName);
				param.setType(paramArray[i]);
				params.add(param);
			}
		}

		returnType = rightSideOfParanthesis[0];
		ms.setReturnType(returnType);

		ms.setParams(params);
		return ms;
	}


}
