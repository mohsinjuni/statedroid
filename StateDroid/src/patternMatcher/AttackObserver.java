package patternMatcher;
import patternMatcher.events.asm.DisplayHomeScreenASMEvent;
import patternMatcher.events.asm.FileReaderASMEvent;
import patternMatcher.events.asm.IncomingSmsAutoReplierASMEvent;
import patternMatcher.events.asm.IncomingSmsBlockerASMEvent;
import patternMatcher.events.asm.InformationLeakerASMEvent;
import patternMatcher.events.asm.KeyguardLockCheckingASMEvent;
import patternMatcher.events.asm.ShowCallScreenWithDialpadASMEvent;
import patternMatcher.events.asm.SmsSenderASMEvent;
import patternMatcher.events.asm.appremoval.AppRemovalASMEvent;
import patternMatcher.events.asm.audiovideo.AudioRecorderASMEvent;
import patternMatcher.events.asm.audiovideo.VideoRecorderASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderDeletionASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderInsertASMEvent;
import patternMatcher.events.asm.contentprovider.ContentProviderUpdateASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallAnsweringASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallBlockingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallForwardingASMEvent;
import patternMatcher.events.asm.phonecall.PhoneCallingASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerRingerModeOnASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerStreamVolumeChangedASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationOffASMEvent;
import patternMatcher.events.asm.phonevolume.AudioManagerVibrationRestoreASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeNormalASMEvent;
import patternMatcher.events.asm.phonevolume.RingerModeSilentASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamDecreasingASMEvent;
import patternMatcher.events.asm.phonevolume.VoiceCallStreamIncreasingASMEvent;
import patternMatcher.events.asm.settings.DeviceAirPlaneModeModificationASMEvent;
import patternMatcher.events.asm.settings.DeviceBrightnessModificationASMEvent;
import patternMatcher.events.asm.settings.MobileDataTogglerASMEvent;
import patternMatcher.events.asm.settings.WifiTogglerASMEvent;
import patternMatcher.events.csm.AbortBroadcastEvent;
import patternMatcher.events.csm.CreateFromPduEvent;
import patternMatcher.events.csm.GetExtrasBundleEvent;
import patternMatcher.events.csm.GetManagerEvent;
import patternMatcher.events.csm.GetPackageManagerDefinedEvent;
import patternMatcher.events.csm.GetPackageNameDefinedEvent;
import patternMatcher.events.csm.GetSystemServiceEvent;
import patternMatcher.events.csm.LockNowEvent;
import patternMatcher.events.csm.ResetPasswordEvent;
import patternMatcher.events.csm.RuntimeExecutionEvent;
import patternMatcher.events.csm.SendBroadcastEvent;
import patternMatcher.events.csm.SetRingerModeEvent;
import patternMatcher.events.csm.SmsSenderEvent;
import patternMatcher.events.csm.SqlLiteDatabaseInsertEvent;
import patternMatcher.events.csm.StartActivityEvent;
import patternMatcher.events.csm.appremoval.SetApplicationEnabledEvent;
import patternMatcher.events.csm.appremoval.SetComponentEnabledEvent;
import patternMatcher.events.csm.audiomanager.AudioMgrSetVibrateSettingEvent;
import patternMatcher.events.csm.audiomanager.SetStreamVolumeEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverApplyBatchEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverDeleteEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverInsertEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverQueryEvent;
import patternMatcher.events.csm.contentresolver.ContentResolverUpdateEvent;
import patternMatcher.events.csm.context.StartActivityIntentEvent;
import patternMatcher.events.csm.cursor.CursorGetStringEvent;
import patternMatcher.events.csm.filereading.BufferedOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.BufferedReaderDefinedEvent;
import patternMatcher.events.csm.filereading.BufferedReaderReadDataEvent;
import patternMatcher.events.csm.filereading.CipherDefinedEvent;
import patternMatcher.events.csm.filereading.CipherDoFinalEvent;
import patternMatcher.events.csm.filereading.CipherGetInstanceEvent;
import patternMatcher.events.csm.filereading.CipherInputStreamInitEvent;
import patternMatcher.events.csm.filereading.CipherInputStreamReadEvent;
import patternMatcher.events.csm.filereading.DataOutputStreamDefinedEvent;
import patternMatcher.events.csm.filereading.DataOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.FileDefinedEvent;
import patternMatcher.events.csm.filereading.FileInputStreamDefinedEvent;
import patternMatcher.events.csm.filereading.FileInputStreamReadEvent;
import patternMatcher.events.csm.filereading.FileOutputStreamFlushEvent;
import patternMatcher.events.csm.filereading.FileOutputStreamWriteEvent;
import patternMatcher.events.csm.filereading.FileReaderDefinedEvent;
import patternMatcher.events.csm.filereading.FileUtilsReadFileToStringEvent;
import patternMatcher.events.csm.filereading.FilesToStringEvent;
import patternMatcher.events.csm.filereading.InputStreamReaderDefinedEvent;
import patternMatcher.events.csm.filereading.ScannerDefinedEvent;
import patternMatcher.events.csm.filereading.ScannerReadDataEvent;
import patternMatcher.events.csm.intent.IntentActionDefinedEvent;
import patternMatcher.events.csm.intent.IntentActionUriDefinedEvent;
import patternMatcher.events.csm.intent.IntentCategoryDefinedEvent;
import patternMatcher.events.csm.intent.IntentContextClsDefinedEvent;
import patternMatcher.events.csm.intent.IntentDefinedEvent;
import patternMatcher.events.csm.intent.IntentPutExtraEvent;
import patternMatcher.events.csm.intent.IntentSetActionEvent;
import patternMatcher.events.csm.intent.IntentSetDataEvent;
import patternMatcher.events.csm.intent.IntentUriDefinedEvent;
import patternMatcher.events.csm.keyguardmanager.KeyguardRestrictedInputModeEvent;
import patternMatcher.events.csm.mediaRecorder.MediaPrepareEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetAudioSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFileEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFormatEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetVideoSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaStartEvent;
import patternMatcher.events.csm.reflection.ClassGetDeclaredFieldEvent;
import patternMatcher.events.csm.reflection.ClassGetDeclaredMethodEvent;
import patternMatcher.events.csm.reflection.FieldGetByKeyEvent;
import patternMatcher.events.csm.reflection.FieldSetAccessibleEvent;
import patternMatcher.events.csm.reflection.ITelephonyAnswerRingingCallEvent;
import patternMatcher.events.csm.reflection.ITelephonyEndCallEvent;
import patternMatcher.events.csm.reflection.ITelephonySilenceRingerEvent;
import patternMatcher.events.csm.reflection.MethodInvokeEvent;
import patternMatcher.events.csm.reflection.MethodSetAccessibleEvent;
import patternMatcher.events.csm.reflection.ShowCallScreenWithDialpadEvent;
import patternMatcher.events.csm.settings.SetWifiEnabledEvent;
import patternMatcher.events.csm.settings.SettingsGlobalPutStringEvent;
import patternMatcher.events.csm.settings.SettingsSystemPutIntEvent;
import patternMatcher.events.csm.uri.UriParsedEvent;
import patternMatcher.events.csm.url.UrlGetOutputStreamEvent;
import patternMatcher.events.csm.url.UrlInitEvent;
import patternMatcher.events.csm.url.UrlOpenConnectionEvent;
import taintanalyzer.TaintAnalyzer;



public abstract class AttackObserver {


	public void registerObserver (TaintAnalyzer taParam){}
	
    public void update(GetExtrasBundleEvent e){return; }
    public void update(CreateFromPduEvent e){return; }    
    public void update(AbortBroadcastEvent e){return; }
    public void update(VoiceCallStreamDecreasingASMEvent e){return; }
    public void update(VoiceCallStreamIncreasingASMEvent e){return; }
    public void update(InformationLeakerASMEvent e){ return;};	
    public void update(SmsSenderEvent e){ return ;};	
    
   //fileReading
   public void update(BufferedReaderDefinedEvent e){ return ;};	
   public void update(BufferedReaderReadDataEvent e){ return ;};	
   public void update(FileDefinedEvent e){ return ;};	
   public void update(FileInputStreamDefinedEvent e){ return ;};	
   public void update(FileOutputStreamWriteEvent e){ return ;};	
   public void update(FileOutputStreamFlushEvent e){ return ;};	
   public void update(FileReaderDefinedEvent e){ return ;};	
   public void update(FilesToStringEvent e){ return ;};	
   public void update(FileUtilsReadFileToStringEvent e){ return ;};	
   public void update(InputStreamReaderDefinedEvent e){ return ;};	
   public void update(ScannerDefinedEvent e){ return ;};	
   public void update(ScannerReadDataEvent e){ return ;};	

   public void update(FileInputStreamReadEvent e){ return;};	
   public void update(DataOutputStreamDefinedEvent e){ return;};	
   public void update(DataOutputStreamWriteEvent e){ return;};	
   public void update(BufferedOutputStreamWriteEvent e){ return;};	

      
    //keyguardManager
    public void update(KeyguardRestrictedInputModeEvent e){ return ;};	

    //DevicePolicyManager
    public void update(ResetPasswordEvent e){ return;};	
    public void update(LockNowEvent e){ return;};	
    public void update(GetManagerEvent e){ return;};	
    

    //Runtime execution
    public void update(RuntimeExecutionEvent e){ return ;};	

    //intent
    public void update(IntentActionUriDefinedEvent e){ return;};	
    public void update(IntentActionDefinedEvent e){ return;};	
    public void update(IntentUriDefinedEvent e){ return;};	
    public void update(IntentDefinedEvent e){ return;};	
	public void update(IntentContextClsDefinedEvent e){ return ;};	
	public void update(IntentSetActionEvent e){ return ;};	
	public void update(IntentPutExtraEvent e){ return ;};
	public void update(SendBroadcastEvent e){ return ;};	
    public void update(IntentSetDataEvent e){ return;};	
    public void update(IntentCategoryDefinedEvent e){ return;};	

    //context
    public void update(StartActivityIntentEvent e){ return;};	
    public void update(GetPackageManagerDefinedEvent e){ return;};	
    public void update(GetPackageNameDefinedEvent e){ return;};	

    //Url
	public void update(UrlInitEvent e){ return;};	
	public void update(UrlOpenConnectionEvent e){ return;};	
	public void update(UrlGetOutputStreamEvent e){ return;};	

    //app-removal
    public void update(SetApplicationEnabledEvent e){ return;};	
    public void update(SetComponentEnabledEvent e){ return;};	
    
    //Mixed
    public void update(UriParsedEvent e){ return;};	
    public void update(StartActivityEvent e){ return ;};	
    public void update(SetStreamVolumeEvent e){ return ;};	
    public void update(GetSystemServiceEvent e){ return;};	
    public void update(SetRingerModeEvent e){ return;};	
    public void update(AudioMgrSetVibrateSettingEvent e){ return;};	
    
    //audioVideoRecorder
    public void update(MediaSetAudioSourceEvent e){ return;};	
    public void update(MediaSetVideoSourceEvent e){ return;};	
    public void update(MediaSetOutputFormatEvent e){ return;};	
    public void update(MediaSetOutputFileEvent e){ return;};	
    public void update(MediaPrepareEvent e){ return;};	
    public void update(MediaStartEvent e){ return;};	

    //reflection
    public void update(ClassGetDeclaredMethodEvent e){ return;};	
	public void update(MethodInvokeEvent e){ return;};	
	public void update(MethodSetAccessibleEvent e){ return;};	
    public void update(ITelephonyEndCallEvent e){ return;}	
    public void update(ITelephonySilenceRingerEvent e) { return;}
    public void update(ITelephonyAnswerRingingCallEvent e) { return;}
    public void update(ClassGetDeclaredFieldEvent e){ return;};	
	public void update(FieldSetAccessibleEvent e){ return;};	
	public void update(FieldGetByKeyEvent e){ return;};	
    public void update(ShowCallScreenWithDialpadEvent e){ return;};	

    //settings
    public void update(SetWifiEnabledEvent e) { return;}
    public void update(SettingsSystemPutIntEvent e) { return;}
    public void update(SettingsGlobalPutStringEvent e) { return;}

    //contentResolver
    public void update(ContentResolverDeleteEvent e){ return;};	
    public void update(ContentResolverUpdateEvent e){return; }
    public void update(ContentResolverApplyBatchEvent e){ return;};	
    public void update(ContentResolverInsertEvent e){ return;};	
    public void update(ContentResolverQueryEvent e){ return;};	
    public void update(CursorGetStringEvent e){ return;};	

    public void update(SqlLiteDatabaseInsertEvent e){ return;};	
    public void update(CipherInputStreamInitEvent e){ return;};	
    public void update(CipherInputStreamReadEvent e){ return;};	    
    public void update(CipherGetInstanceEvent e){ return;};	
    public void update(CipherDoFinalEvent e){ return;};	
    public void update(CipherDefinedEvent e){ return;};	    

	//ASM
	public void update(RingerModeNormalASMEvent e){ return;};	
	public void update(RingerModeSilentASMEvent e){ return;};	
	public void update(IncomingSmsBlockerASMEvent e){return; }; 
	public void update(IncomingSmsAutoReplierASMEvent e){return; }; 
	public void update(SmsSenderASMEvent e){return; }; 

	public void update(ContentProviderDeletionASMEvent e){ return;};	
	public void update(ContentProviderUpdateASMEvent e){ return;};	
	public void update(ContentProviderInsertASMEvent e){ return;};	
	public void update(AudioRecorderASMEvent e){ return;};	
	public void update(VideoRecorderASMEvent e){ return;};	
	public void update(AudioManagerStreamVolumeChangedASMEvent e){ return;};	
	public void update(AudioManagerRingerModeOffASMEvent e){ return;};	
    public void update(KeyguardLockCheckingASMEvent e){ return ;};		
    public void update(FileReaderASMEvent e){ return;};	
	public void update(PhoneCallBlockingASMEvent e){ return;};	
	public void update(PhoneCallForwardingASMEvent e){ return;};	
	public void update(PhoneCallAnsweringASMEvent e){ return;};	
	public void update(PhoneCallingASMEvent e){ return;};	
	public void update(AppRemovalASMEvent e){ return;};	
	public void update(WifiTogglerASMEvent e){ return;};	
	public void update(DeviceBrightnessModificationASMEvent e){ return;};	
	public void update(DeviceAirPlaneModeModificationASMEvent e){ return;};	
	public void update(MobileDataTogglerASMEvent e){ return;};	
    public void update(ShowCallScreenWithDialpadASMEvent e){ return;};	
    
    public void update(AudioManagerVibrationOffASMEvent e){ return;}
    public void update(AudioManagerVibrationRestoreASMEvent e){ return;}
    public void update(AudioManagerRingerModeOnASMEvent e){return;}
    public void update(DisplayHomeScreenASMEvent e){return;}

    
}
