package patternMatcher.statemachines;
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
import patternMatcher.events.csm.mediaRecorder.MediaPreviewDisplayEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetAudioSourceEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetCameraEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetMaxDurationEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFileEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetOutputFormatEvent;
import patternMatcher.events.csm.mediaRecorder.MediaSetPreviewDisplayEvent;
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



public class State {

   public State update(GetExtrasBundleEvent e){ return this;}
   public State update(CreateFromPduEvent e){ return this;}
   
   public State update(AbortBroadcastEvent e){ return this;}
   
// ----------------  CSM states  ------------------------
   
   //fileReading
   public State update(BufferedReaderDefinedEvent e){ return this;};	
   public State update(BufferedReaderReadDataEvent e){ return this;};	
   public State update(FileDefinedEvent e){ return this;};	
   public State update(FileInputStreamDefinedEvent e){ return this;};	
   public State update(FileReaderDefinedEvent e){ return this;};	
   public State update(FileOutputStreamWriteEvent e){ return this;};	
   public State update(CipherInputStreamInitEvent e){ return this;};	
   public State update(CipherInputStreamReadEvent e){ return this;};	
   public State update(FileOutputStreamFlushEvent e){ return this;};	
   public State update(FilesToStringEvent e){ return this;};	
   public State update(FileUtilsReadFileToStringEvent e){ return this;};	
   public State update(InputStreamReaderDefinedEvent e){ return this;};	
   public State update(ScannerDefinedEvent e){ return this;};	
   public State update(ScannerReadDataEvent e){ return this;};	

   public State update(FileInputStreamReadEvent e){ return this;};	
   public State update(DataOutputStreamDefinedEvent e){ return this;};	
   public State update(DataOutputStreamWriteEvent e){ return this;};	
   public State update(BufferedOutputStreamWriteEvent e){ return this;};	
   public State update(CipherGetInstanceEvent e){ return this;};	
   public State update(CipherDoFinalEvent e){ return this;};	
   public State update(CipherDefinedEvent e){ return this;};	    
   
   //Runtime execution
   public State update(RuntimeExecutionEvent e){ return this;};	

   //keyguardManager
   public State update(KeyguardRestrictedInputModeEvent e){ return this;};	
   
   //Intent
   public State update(IntentDefinedEvent e){ return this;};	
   public State update(IntentActionDefinedEvent e){ return this;};	
   public State update(IntentActionUriDefinedEvent e){ return this;};	
   public State update(IntentUriDefinedEvent e){ return this;};	
   public State update(IntentSetDataEvent e){ return this;};	
   public State update(IntentCategoryDefinedEvent e){ return this;};	
	public State update(IntentContextClsDefinedEvent e){ return this;};	
	public State update(IntentSetActionEvent e){ return this;};	
	public State update(IntentPutExtraEvent e){ return this;};	
	public State update(SendBroadcastEvent e){ return this;};	
   
 
   //settings
    public State update(SetWifiEnabledEvent e) { return this;}
    public State update(SettingsSystemPutIntEvent e) { return this;}
    public State update(SettingsGlobalPutStringEvent e) { return this;}

   //Uri
   public State update(UriParsedEvent e){ return this;};	
   
   //Url
   	public State update(UrlInitEvent e){ return this;};	
	public State update(UrlOpenConnectionEvent e){ return this;};	
	public State update(UrlGetOutputStreamEvent e){ return this;};	


   //DevicePolicyManager
   public State update(ResetPasswordEvent e){ return this;};	
    public State update(LockNowEvent e){ return this;};	
    public State update(GetManagerEvent e){ return this;};	

   //context
   public State update(StartActivityIntentEvent e){ return this;};	
   public State update(GetPackageManagerDefinedEvent e){ return this;};	
   public State update(GetPackageNameDefinedEvent e){ return this;};	
   
   //appRemoval
   public State update(SetApplicationEnabledEvent e){ return this;};	
   public State update(SetComponentEnabledEvent e){ return this;};	
   
   //reflection
   public State update(ClassGetDeclaredMethodEvent e){ return this;};	
   public State update(MethodInvokeEvent e){ return this;};	
   public State update(MethodSetAccessibleEvent e){ return this;};	
   public State update(ITelephonyEndCallEvent e){ return this;};	
   public State update(ITelephonySilenceRingerEvent e) { return this;};
   public State update(ITelephonyAnswerRingingCallEvent e) { return this;};
   public State update(ClassGetDeclaredFieldEvent e){ return this;};	
   public State update(FieldSetAccessibleEvent e){ return this;};	
   public State update(FieldGetByKeyEvent e){ return this;};	
   public State update(ShowCallScreenWithDialpadEvent e){ return this;};	
   
   //context
   public State update(StartActivityEvent e){ return this;};	
   public State update(GetSystemServiceEvent e){ return this;};	

   //AudioManager
   public State update(SetStreamVolumeEvent e){ return this;};	
   public State update(SetRingerModeEvent e){ return this;};	
   public State update(AudioMgrSetVibrateSettingEvent e){ return this;};	
   
   //AudioVideoRecorder
   public State update(MediaSetAudioSourceEvent e){ return this;};	
   public State update(MediaSetCameraEvent e){ return this;};	
   public State update(MediaSetMaxDurationEvent e){ return this;};	
   public State update(MediaSetOutputFileEvent e){ return this;};	
   public State update(MediaSetOutputFormatEvent e){ return this;};	
   public State update(MediaSetPreviewDisplayEvent e){ return this;};	
   public State update(MediaSetVideoSourceEvent e){ return this;};	
   public State update(MediaPrepareEvent e){ return this;};	
   public State update(MediaStartEvent e){ return this;};	
   public State update(MediaPreviewDisplayEvent e){ return this;};	

   //ContentResolver
   public State update(ContentResolverDeleteEvent e){ return this;};	
   public State update(ContentResolverUpdateEvent e){return this; }
   public State update(ContentResolverApplyBatchEvent e){ return this;}	
   public State update(ContentResolverQueryEvent e){ return this;}	
   public State update(ContentResolverInsertEvent e){ return this;}	
   public State update(CursorGetStringEvent e){ return this;}	
   
   public State update(SqlLiteDatabaseInsertEvent e){ return this;};	

   //ASM states
    public State update(InformationLeakerASMEvent e){ return this;}
    public State update(SmsSenderEvent e){ return this;}
    public State update(VoiceCallStreamDecreasingASMEvent e){ return this;}	
    public State update(VoiceCallStreamIncreasingASMEvent e){ return this;}	
	public State update(RingerModeNormalASMEvent e){ return this;};	
	public State update(RingerModeSilentASMEvent e){ return this;};	
	public State update(IncomingSmsBlockerASMEvent e){return this; }; 
	public State update(IncomingSmsAutoReplierASMEvent e){return this; }; 
	public State update(SmsSenderASMEvent e){return this; }; 

	public State update(ContentProviderDeletionASMEvent e){ return this;};	
	public State update(ContentProviderUpdateASMEvent e){ return this;};	
	public State update(PhoneCallBlockingASMEvent e){ return this;};	
    public State update(PhoneCallingASMEvent e){ return this;}
	public State update(PhoneCallAnsweringASMEvent e){ return this;};	
	public State update(PhoneCallForwardingASMEvent e){ return this;};	
	
	public State update(AudioRecorderASMEvent e){ return this;};	
	public State update(VideoRecorderASMEvent e){ return this;};	
	public State update(AudioManagerStreamVolumeChangedASMEvent e){ return this;};	
	public State update(AudioManagerRingerModeOffASMEvent e){ return this;};	
    public State update(KeyguardLockCheckingASMEvent e){ return this;};	
    public State update(FileReaderASMEvent e){ return this;};	
    public State update(AppRemovalASMEvent e){ return this;};	
	public State update(WifiTogglerASMEvent e){ return this;};	
	public State update(ContentProviderInsertASMEvent e){ return this;};	
	public State update(DeviceBrightnessModificationASMEvent e){ return this;};	
	public State update(DeviceAirPlaneModeModificationASMEvent e){ return this;};	
	public State update(MobileDataTogglerASMEvent e){ return this;};	
    public State update(ShowCallScreenWithDialpadASMEvent e){ return this;};	

   public State update(AudioManagerVibrationOffASMEvent e){ return this;}
   public State update(AudioManagerVibrationRestoreASMEvent e){ return this;}
   public State update(AudioManagerRingerModeOnASMEvent e){return this;}
   public State update(DisplayHomeScreenASMEvent e){return this;}
   
}
