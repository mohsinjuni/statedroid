package analyzer;

import models.cfg.APK;
import models.cfg.BasicBlock;
import models.cfg.CFG;
import models.cfg.ClassObj;
import models.cfg.Instruction;
import models.cfg.Package;
public abstract class Analyzer {

	public void analyze(Instruction ins){ }
	public void analyze(BasicBlock bb){}
	public void analyze(CFG cfg){}
	public void analyze(ClassObj classObj){}
	public void analyze(Package pkg){}
	public void analyze(APK apk){}

}
