package decompilation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

import configuration.Config;

public class APKDecompiler {


	private String androguardPath = Config.getInstance().getAndroguardPath(); 

	public void decompileAPK(String inputFile){
		runAndroguard(inputFile, ".txt", "cfgAndroFile.py");
		//		System.out.println("Dalvik Byte Code generated for <" + inputFile +">");

		runAndroguard(inputFile, ".xml", "androaxml.py");
		//		System.out.println("XML generated for <" + inputFile +">");

		if(Config.getInstance().isResourceHandling()){
			decompileResourceXMLFiles(inputFile);
			System.out.println("Resource files generated .....");
		}
	}

	public void runAndroguard(String inputFile, String outputFileExtension, String scriptName)
	{

		String outputFile = inputFile.concat(outputFileExtension);
		String scriptPath = androguardPath.concat(scriptName);
		
//		System.out.println(outputFile);
//		System.out.println(scriptPath);

		if(!new File(outputFile).exists())
		{
			Process p;
			ProcessBuilder pb=new ProcessBuilder();

			pb.command("python", scriptPath, "-i", inputFile , "-o", outputFile);

			try{
				p=pb.start();
				p.waitFor(); // wait for process finishes
			}catch (Exception e){
				e.printStackTrace();
				System.out.println("Decompilation error for input file:  " + inputFile);
			}
		}
	}

	public String createAndStoreZipFile(List<String> inputFiles){
		String zippedFilePath = "";
		FileOutputStream fos;
		try {
			fos = new FileOutputStream("atest.zip");
			ZipOutputStream zos = new ZipOutputStream(fos);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return zippedFilePath;
	}
	public void decompileResourceXMLFiles(String inputFile)
	{
		String[] split1 = inputFile.split("[\\\\]"); //   */*/abc.apk
		String dirName = split1[split1.length-1];
		dirName = dirName.substring(0, dirName.length()-3); // get "abc" here and create that folder.

		String outputPath = "";
		for(int i=0; i < split1.length-1; i++)
		{
			outputPath += split1[i];
			outputPath += "\\";
		}
		outputPath += dirName;

		File outputDir = new File(outputPath);
		if(!outputDir.exists())
			outputDir.mkdir();

		String outputFile = outputPath;
		String scriptPath = androguardPath.concat("androaxmlresrc.py");

		Process p;
		ProcessBuilder pb=new ProcessBuilder();

		pb.command("python", scriptPath, "-i", inputFile , "-o", outputFile); // here output is output directory.

		try{
			p=pb.start();
			p.waitFor(); // wait for process finishes
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public String getAndroguardPath() {
		return androguardPath;
	}

	public void setAndroguardPath(String androguardPath) {
		this.androguardPath = androguardPath;
	}

}
