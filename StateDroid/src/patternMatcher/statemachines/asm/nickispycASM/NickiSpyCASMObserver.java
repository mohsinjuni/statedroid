package patternMatcher.statemachines.asm.nickispycASM;
import patternMatcher.AttackObserver;
import patternMatcher.events.asm.DisplayHomeScreenASMEvent;
import patternMatcher.events.asm.ShowCallScreenWithDialpadASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallAnsweringASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOnASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationRestoreASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.statemachines.State;
import patternMatcher.statemachines.asm.nickispycASM.states.InitialState;
import taintanalyzer.TaintAnalyzer;

public class NickiSpyCASMObserver extends AttackObserver {

	private State state;
	private TaintAnalyzer taSubject;

	public NickiSpyCASMObserver (){
		this.state = new InitialState();
	}

	public NickiSpyCASMObserver (TaintAnalyzer taParam){
		this.taSubject = taParam;
		this.state = new InitialState(taSubject);
	}

   public void update(AudioManagerVibrationOffASMEvent e){ 
		state = state.update(e);		
   }

   @Override
   public void update(AudioManagerRingerModeOffASMEvent e){
		this.state = new InitialState(taSubject);
		state = state.update(e);		
   }

   @Override
   public void update(RingerModeSilentASMEvent e){
		state = state.update(e);		
   }

   @Override
   public void update(ShowCallScreenWithDialpadASMEvent e){
		state = state.update(e);		
   }

   @Override
   public void update(PhoneCallBlockingASMEvent e){
		state = state.update(e);		
   }

   @Override
   public void update(AudioManagerVibrationRestoreASMEvent e){ 
		state = state.update(e);		
   }

   @Override
   public void update(AudioManagerRingerModeOnASMEvent e){
		state = state.update(e);		
   }

   @Override
   public void update(ContentProviderDeletionASMEvent e){
		state = state.update(e);		
   }

   @Override
   public void update(PhoneCallAnsweringASMEvent e){
		state = state.update(e);		
   }

   @Override
   public void update(DisplayHomeScreenASMEvent e){
		state = state.update(e);		
   }

   public TaintAnalyzer getTaSubject() {
		return taSubject;
	}

	public void setTaSubject(TaintAnalyzer taSubject) {
		this.taSubject = taSubject;
	}

}
