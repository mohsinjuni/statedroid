package controller;

import java.io.File;
import java.util.ArrayList;

import models.cfg.APK;

import org.apache.log4j.Logger;

import analyzer.Analyzer;

import patternMatcher.attackreporter.AttackReporter;
import patternMatcher.events.csm.filereading.BufferedOutputStreamWriteEvent;
import taintanalyzer.TaintAnalyzer;
import configuration.Config;
import decompilation.APKDecompiler;

public class Controller {

	private static Logger logger = Logger.getLogger("TaintAnalyzer");

	public static void main(String args[]) {

		String input_path = args[0]; //"D:\\Artifact Evaluation\\Dataset\\GroundTruthApps\\HandcraftedApps\\DecompiledApps"; 
		String androguardPath = args[1];
		Config.propertiesPath = args[2]; //"D:\\Softwares\\Eclipse Workspace\\StateDroid";
		Config.getInstance().setAndroguardPath(args[1]); //"D:\\Artifact Evaluation\\Tool\\androguard\\");
		Config.getInstance().setPropertiesPath(Config.propertiesPath);
		
		System.out.println(Config.getInstance().getAndroguardPath());
		System.out.println(Config.propertiesPath);
		
		File inputFile = new File(input_path);

		System.out.println("Analysis started");
		logger.fatal("ANALYSISSSSSSSSSS STARTEDDDDDDDD");

		if (inputFile.isDirectory()) {
			for (File file : inputFile.listFiles()) {
				if (file.getAbsolutePath().endsWith(".apk")){
					apkAnalyzer(file);
				}
			}
		}else{
			if (inputFile.getAbsolutePath().endsWith(".apk"))
					apkAnalyzer(inputFile);
		}
		 System.out.println("Analysis finished!!");

	}

	public static void apkAnalyzer(File inputFile) {
		System.out.println("Analysis Started for  " + inputFile.getAbsolutePath());
		if (inputFile.getAbsolutePath().endsWith(".apk")
				) {
			CFGBuilder cfgBlrdr = new CFGBuilder();
			APK apk = cfgBlrdr.getAPKObject(inputFile);

			if (apk != null) {
				TaintAnalyzer a = new TaintAnalyzer();
				apk.accept(a);
				reportAllUniqueWarnings();
				logger.fatal("Analysis Finished for  " + inputFile.getAbsolutePath());
			}else{
				logger.fatal("Decompilation failed for this input file: " + inputFile.getName());
			}
		}
	}
	
	public static void reportAllUniqueWarnings(){
		Config.getInstance().resetDataForNewApp();
		AttackReporter.getInstance().resetAllExistingReports();
	}
}
