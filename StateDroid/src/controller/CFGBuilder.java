package controller;

import java.io.File;

import models.cfg.APK;

import org.apache.log4j.Logger;

import analyzer.Analyzer;

import decompilation.APKDecompiler;
import decompilation.ByteCodeParser;

import taintanalyzer.TaintAnalyzer;

public class CFGBuilder {
	
	private static Logger logger = Logger.getLogger("CFGBuilder");
	
	public APK getAPKObject( File inputFile)
	{
		APKDecompiler decompiler = new APKDecompiler();
		APK apk=null;
		String inputFilePath = inputFile.getAbsolutePath();

		if(inputFilePath.endsWith(".apk"))
		{
			decompiler.decompileAPK(inputFilePath); 
			
			logger.fatal("@@@@@  " + inputFilePath);
			ByteCodeParser op = new ByteCodeParser();
			apk = op.getAPKInfo(inputFilePath);
		}
		return apk;
	}

}
