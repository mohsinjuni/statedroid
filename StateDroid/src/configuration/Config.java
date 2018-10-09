package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;

import models.manifest.AndroidManifest;
import models.symboltable.Context;
import models.symboltable.SymbolSpace;

import org.apache.commons.collections.map.MultiValueMap;

import enums.ActivityLCMEvents;
import enums.ComponentTypes;

public class Config {

	private static Config instance =null; 
	private static Properties InstructionHandlersMap;
	private static Properties InstructionTaintAnalyzersMap;
	private static Properties SourceSinkAPIMap;
	private static Properties ApiRulesMap;
	private static Properties ApiDefinedAnalyzers;
	private static Properties eventsMapping;
	private static SymbolSpace localSymbolSpace ;
	private static SymbolSpace globalSymbolSpace;
	private static Context prevMethodContext;
	private static AndroidManifest androidManifest;
	
	
	private static Hashtable activityCallbackList;
	private static Hashtable serviceCallbackList;
	private static Hashtable broadcastReceiverCallbackList;
	private static Hashtable asynchTaskCallbackList;
	private static Hashtable threadCallbackList;
	private static Hashtable immutableObjects;
	private static Hashtable blackListedAPIs;
	private static Hashtable eventSpecificCallbacks;
	private Hashtable<String, String> allComponentTypes;
	private Hashtable<String, ArrayList<String>> additionalCallbacks;
	private HashSet<String> viewGroupCallbacks;
	private HashSet<String> webViewCallbacks;
	private HashSet<String> sixtyFourBitRegisters;
	private HashSet<String> callForwardingCodes;
	private HashMap<String, String> shellCommands;

	//DroidBench Flags
	private boolean isExceptionHandling = false;
	private boolean isResourceHandling = false;
	private boolean isNonManifestCompAnalysis = true;
	private boolean isUniqueWarningEnabled= true;
	private static boolean allPermutationReport = false;
	private static final int permutationOrder = 1;
	private static final boolean isParentChildHandling= false;
	private static final boolean isOnClickedHandlingEnabled=true;

	private boolean isDeepCopyForRecordFieldList = false;
	private ArrayList<String> currCFGPermutation;
	private MultiValueMap funcKeySignatureMap;

	private Stack funcCallStack;
	private Stack compCallStack;
	
	private boolean isAttackReported = false;
	long componentTimeOutInMillis = 120000; //2 minutes.
	long componentCurrDuration=0;
	long componentAnalysisStartTime = 0;

	private String androguardPath;
	public static String propertiesPath="";
	
	//TODO this has become such a bloated class now. :(
	private Config()
	{
		loadInstructionHandlers();
		loadSourceSinkAPIMap();
		loadInstructionTaintAnalyzers();
		loadApiRulesAnalyzers();
		loadApiDefinedAnalyzers();
		loadEventsMapping();
		loadAllComponentTypes();
		loadAdditionalCallbacks();
		loadViewGroupCallbacks();
		loadwebViewCallbacks();
		loadSixtyFourBitRegisters();
		loadShellCommands();
		localSymbolSpace = new SymbolSpace();
		globalSymbolSpace = new SymbolSpace();
		globalSymbolSpace.push(); // We may never remove this hashtable.
		prevMethodContext = new Context();
		setAndroidManifest(new AndroidManifest());
		
		setActivityCallbackList(loadActivityCallbacks());
		setServiceCallbackList(loadServiceCallbacks());
		setBroadcastReceiverCallbackList(loadBroadcastReceiverCallbacks());
		setAsynchTaskCallbackList(loadAsynchTaskCallbacks());
		setThreadCallbackList(loadThreadCallbacks());
		
		setImmutableObjects(loadImmutableObjects());
		
		setBlackListedAPIs(loadBlackListedAPIs());
		setActivityEventCallbacks();
		
		currCFGPermutation = new ArrayList<String>();
	
		
		setFuncCallStack(new Stack());
		setCompCallStack(new Stack());
		setFuncKeySignatureMap(new MultiValueMap());
		loadCallForwardingCodes();
	}
	

	public static boolean isIsonclickedhandlingenabled() {
		return isOnClickedHandlingEnabled;
	}


	public static Config getInstance() 
	{
        if (null == instance) 
        {
            synchronized (Config.class)
            {
            	  if (null == instance) 
	              {
	            	  return instance = new Config();
	            	  
	              }
            }
        }
        return instance;
    }
	
	private void loadSixtyFourBitRegisters(){
		this.sixtyFourBitRegisters = new HashSet<String>();
		sixtyFourBitRegisters.add("J");
		sixtyFourBitRegisters.add("D");
	}
	private void loadShellCommands(){
		shellCommands = new HashMap<String, String>();
		String runTime = "Runtime Execution: ";
		shellCommands.put("/system/bin/su", runTime + "Privilege Escalation Attack");
		shellCommands.put("su", runTime + "Privilege Escalation Attack");
		shellCommands.put("/system/xbin/su", runTime + "Privilege Escalation Attack");
		shellCommands.put("/data/bin/su", runTime + "Privilege Escalation Attack");

		shellCommands.put("am startservice -a android.intent.action.MAIN -n", runTime + "This app runs a service in the background");
		shellCommands.put("startservice", runTime + "This app runs a service in the background");

		shellCommands.put("mount -o remount rw system\nexit\n", runTime + "This app remounts system storage to modify system files");
		shellCommands.put("/system/bin/mount", runTime + "This app remounts system storage to modify system files");
		shellCommands.put("mount", runTime + "This app remounts system storage to modify system files");

		shellCommands.put("/system/bin/chmod", runTime + "This app makes other files executable to be run on adb shell");
		shellCommands.put("/system/xbin/chmod", runTime + "This app makes other files executable to be run on adb shell");
		shellCommands.put("chmod", runTime + "This app makes other files executable to be run on adb shell");
		shellCommands.put("755 /data/data/", runTime + "This app makes other files executable to be run on adb shell");
		shellCommands.put("755", runTime + "This app makes other files executable to be run on adb shell");
		shellCommands.put("777", runTime + "This app makes other files executable to be run on adb shell");
		shellCommands.put("/system/bin/chmod 755", runTime + "This app makes other files executable to be run on adb shell");
		shellCommands.put("chmod 777", runTime + "This app makes other files executable to be run on adb shell");
		shellCommands.put("chmod 7", runTime + "This app makes other files executable to be run on adb shell"); //After that whatever it is, we accept that.

		shellCommands.put("sh", runTime + "This app access device shell to execute commands");
		shellCommands.put("/system/bin/sh", runTime + "This app access device shell to execute commands");
//		shellCommands.put("/install.sh ", runTime + "");
		
		shellCommands.put("adb reboot", runTime + "This app can programmatically reboot the device");

		shellCommands.put("uninstall", runTime + "This app can programmatically uninstall any other application");
		shellCommands.put("adb install", runTime + "This app can programmatically install other applications");
		shellCommands.put("force-stop", runTime + "This app can forcefully stop other applications");
		shellCommands.put("kill", runTime + "This app can programmatically kill other apps");
		shellCommands.put("list packages", runTime + "This app can obtain list of installed applications");
		shellCommands.put("list users", runTime + "This app can programmatically obtain list of all users on the device");
		shellCommands.put("disable", runTime + "This app can programmatically disable or stop other apps");
		shellCommands.put("screencap", runTime + "This app can programmatically take screenshot of the device");

	}
	// https://en.wikipedia.org/wiki/Call_forwarding#Keypad_codes
	private void loadCallForwardingCodes(){
		callForwardingCodes = new HashSet<String>();
		callForwardingCodes.add("*72");
		callForwardingCodes.add("*68");
		callForwardingCodes.add("*63");
		callForwardingCodes.add("*90");
		callForwardingCodes.add("*92");
		
		callForwardingCodes.add("*28");
		callForwardingCodes.add("*74");
		callForwardingCodes.add("*73");
		callForwardingCodes.add("*72");
		
		callForwardingCodes.add("*21*");
		callForwardingCodes.add("*69*");
		callForwardingCodes.add("*67*");
		callForwardingCodes.add("*61*");
		callForwardingCodes.add("*62*");
		
		callForwardingCodes.add("*002*");
		callForwardingCodes.add("*004*");
	}
	
	private void loadViewGroupCallbacks(){
		viewGroupCallbacks = new HashSet<String>();
		viewGroupCallbacks.add("onInterceptHoverEvent");
		viewGroupCallbacks.add("onInterceptTouchEvent");
		viewGroupCallbacks.add("onNestedPreFling");
		viewGroupCallbacks.add("onNestedPrePerformAccessibilityAction");
		viewGroupCallbacks.add("onNestedPreScroll");
		viewGroupCallbacks.add("onNestedScroll");
		viewGroupCallbacks.add("onNestedScrollAccepted");
		viewGroupCallbacks.add("onRequestSendAccessibilityEvent");
		viewGroupCallbacks.add("onStartNestedScroll");
		viewGroupCallbacks.add("onViewAdded");
		viewGroupCallbacks.add("onViewRemoved");
		viewGroupCallbacks.add("onAttachedToWindow");
		viewGroupCallbacks.add("onCreateDrawableState");
		viewGroupCallbacks.add("onDetachedFromWindow");
		viewGroupCallbacks.add("onLayout");
		viewGroupCallbacks.add("onCheckIsTextEditor");
		viewGroupCallbacks.add("onConfigurationChanged");
		viewGroupCallbacks.add("onCreateContextMenu");
		viewGroupCallbacks.add("onRestoreInstanceState");
		viewGroupCallbacks.add("onSaveInstanceState");

	}
	
		private void loadwebViewCallbacks(){
		webViewCallbacks = new HashSet<String>();
		webViewCallbacks.add("onCreateInputConnection");
		webViewCallbacks.add("onFinishTemporaryDetach");
		webViewCallbacks.add("onGenericMotionEvent");
		webViewCallbacks.add("onGlobalFocusChanged");
		webViewCallbacks.add("onHoverEvent");
		webViewCallbacks.add("onPause");
		webViewCallbacks.add("onProvideVirtualStructure");
		webViewCallbacks.add("onResume");
		webViewCallbacks.add("onStartTemporaryDetach");
		webViewCallbacks.add("onWindowFocusChanged");
		webViewCallbacks.add("onAttachedToWindow");
		webViewCallbacks.add("onDraw");
		webViewCallbacks.add("onFocusChanged");
		webViewCallbacks.add("onOverScrolled");
		webViewCallbacks.add("onScrollChanged");
		webViewCallbacks.add("onMeasure");
		webViewCallbacks.add("onSizeChanged");
		webViewCallbacks.add("onWindowVisibilityChanged");
		webViewCallbacks.add("onVisibilityChanged");
	}
	private void loadAdditionalCallbacks(){
		additionalCallbacks = new Hashtable<String, ArrayList<String>>();
		
		ArrayList<String> activityAddCallbacks = new ArrayList<String>();
		activityAddCallbacks.add("attachBaseContext");
		activityAddCallbacks.add("onLowMemory");
		activityAddCallbacks.add("onTrimMemory");
		additionalCallbacks.put(ComponentTypes.activity.toString(), activityAddCallbacks);
		
		//TODO: add more callbacks here.
		ArrayList<String> viewGroupCallbacks = new ArrayList<String>();
		viewGroupCallbacks.add("onInterceptHoverEvent");
		viewGroupCallbacks.add("onInterceptTouchEvent");
		viewGroupCallbacks.add("onNestedPreFling");
		viewGroupCallbacks.add("onNestedPrePerformAccessibilityAction");
		viewGroupCallbacks.add("onNestedPreScroll");
		viewGroupCallbacks.add("onNestedScroll");
		viewGroupCallbacks.add("onNestedScrollAccepted");
		viewGroupCallbacks.add("onRequestSendAccessibilityEvent");
		viewGroupCallbacks.add("onStartNestedScroll");
		viewGroupCallbacks.add("onViewAdded");
		viewGroupCallbacks.add("onViewRemoved");
		viewGroupCallbacks.add("onAttachedToWindow");
		viewGroupCallbacks.add("onCreateDrawableState");
		viewGroupCallbacks.add("onDetachedFromWindow");
		viewGroupCallbacks.add("onLayout");
		viewGroupCallbacks.add("onCheckIsTextEditor");
		viewGroupCallbacks.add("onConfigurationChanged");
		viewGroupCallbacks.add("onCreateContextMenu");
		viewGroupCallbacks.add("onRestoreInstanceState");
		viewGroupCallbacks.add("onSaveInstanceState");
		additionalCallbacks.put(ComponentTypes.viewGroup.toString(), viewGroupCallbacks);
		
		ArrayList<String> webViewCallbacks = new ArrayList<String>();
		webViewCallbacks = new ArrayList<String>();
		webViewCallbacks.add("onCreateInputConnection");
		webViewCallbacks.add("onFinishTemporaryDetach");
		webViewCallbacks.add("onGenericMotionEvent");
		webViewCallbacks.add("onGlobalFocusChanged");
		webViewCallbacks.add("onHoverEvent");
		webViewCallbacks.add("onPause");
		webViewCallbacks.add("onProvideVirtualStructure");
		webViewCallbacks.add("onResume");
		webViewCallbacks.add("onStartTemporaryDetach");
		webViewCallbacks.add("onWindowFocusChanged");
		webViewCallbacks.add("onAttachedToWindow");
		webViewCallbacks.add("onDraw");
		webViewCallbacks.add("onFocusChanged");
		webViewCallbacks.add("onOverScrolled");
		webViewCallbacks.add("onScrollChanged");
		webViewCallbacks.add("onMeasure");
		webViewCallbacks.add("onSizeChanged");
		webViewCallbacks.add("onWindowVisibilityChanged");
		webViewCallbacks.add("onVisibilityChanged");
		additionalCallbacks.put(ComponentTypes.webView.toString(), webViewCallbacks);
	}
	
	private void loadAllComponentTypes(){
		allComponentTypes = new Hashtable<String, String>();
		
		//Activity
		allComponentTypes.put("Landroid/app/Activity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/AccountAuthenticatorActivity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/ActivityGroup;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/AliasActivity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/ExpandableListActivity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/FragmentActivity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/ListActivity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/NativeActivity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/ActionBarActivity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/AppCompatActivity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/LauncherActivity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/PreferenceActivity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/app/TabActivity;", ComponentTypes.activity.toString());
		allComponentTypes.put("Landroid/support/v7/app/ActionBarActivity;", ComponentTypes.activity.toString());
		

		//Service
		allComponentTypes.put("Landroid/app/Service;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/inputmethodservice/AbstractInputMethodService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/accessibilityservice/AccessibilityService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/service/media/CameraPrewarmService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/service/carrier/CarrierMessagingService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/service/carrier/CarrierService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/service/chooser/ChooserTargetService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/telecom/ConnectionService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/support/customtabs/CustomTabsService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/service/dreams/DreamService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/nfc/cardemulation/HostApduService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/telecom/InCallService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/app/IntentService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/app/InputMethodService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/app/job/JobService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/service/media/MediaBrowserService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/support/v7/media/MediaRouteProviderService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/media/midi/MidiDeviceService;", ComponentTypes.service.toString());
		allComponentTypes.put("Landroid/support/v4/app/NotificationCompatSideChannelService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/service/notification/NotificationListenerService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/nfc/cardemulation/OffHostApduService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/printservice/PrintService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/speech/RecognitionService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/widget/RemoteViewsService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/location/SettingInjectorService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/service/textservice/SpellCheckerService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/speech/tts/TextToSpeechService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/media/tv/TvInputService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/service/voice/VoiceInteractionService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/service/voice/VoiceInteractionSessionService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/net/VpnService;", ComponentTypes.service.toString());		
		allComponentTypes.put("Landroid/service/wallpaper/WallpaperService;", ComponentTypes.service.toString());
		
		//BroadcastReceiver
		allComponentTypes.put("Landroid/content/BroadcastReceiver;", ComponentTypes.broadcastReceiver.toString());
		allComponentTypes.put("Landroid/appwidget/AppWidgetProvider;", ComponentTypes.broadcastReceiver.toString());
		allComponentTypes.put("Landroid/app/admin/DeviceAdminReceiver;", ComponentTypes.broadcastReceiver.toString());
		allComponentTypes.put("Landroid/service/restrictions/RestrictionsReceiver;", ComponentTypes.broadcastReceiver.toString());
		allComponentTypes.put("Landroid/support/v4/content/WakefulBroadcastReceiver;", ComponentTypes.broadcastReceiver.toString());
		
		//ContentProvider
		allComponentTypes.put("Landroid/content/ContentProvider;", ComponentTypes.contentProvider.toString());
		allComponentTypes.put("Landroid/provider/DocumentsProvider;", ComponentTypes.contentProvider.toString());
		allComponentTypes.put("Landroid/support/v4/content/FileProvider;", ComponentTypes.contentProvider.toString());
		allComponentTypes.put("Landroid/test/mock/MockContentProvider;", ComponentTypes.contentProvider.toString());
		allComponentTypes.put("Landroid/content/SearchRecentSuggestionsProvider;", ComponentTypes.contentProvider.toString());
		
		//AsyncTask
		allComponentTypes.put("Landroid/os/AsyncTask;", ComponentTypes.task.toString());

	}
	
	public void resetDataForNewApp()
	{
		localSymbolSpace = new SymbolSpace();
		globalSymbolSpace = new SymbolSpace();
		globalSymbolSpace.push(); // We may never remove this hashtable.
		prevMethodContext = new Context();
		setAndroidManifest(new AndroidManifest());
				
		currCFGPermutation = new ArrayList<String>();
		
		setFuncCallStack(new Stack());
	}
	
	public Hashtable loadDontAnalyzeAPIs()
	{
		Hashtable ht = new Hashtable();
		ht.put("Landroid", "");
		ht.put("Ljava","" );
		
		return ht;
		
	}

	private Hashtable loadBlackListedAPIs()
	{
		Hashtable ht = new Hashtable();
		
		ht.put("Landroid/support/v4", "");
		ht.put("Landroid/support/v7", "");
		ht.put("Landroid/webkit", "");
		ht.put("Lcom/google/android/gms", "");

		
		ht.put("Lcom/google/ads", "");
		ht.put("Lcom/google/gson", "");
		ht.put("Landroid/media", "");
		ht.put("Lcom/android/internal", "");
		ht.put("Lcom/android/common", "");

		ht.put("Lcom/actionbarsherlock/", "");
		ht.put("Lcom/facebook/", "");
		ht.put("Lcom/ibm/icu/", "");

		// Whitelist of APIs.
		/*
		 * 
		 * com.google.ads;         infact, all com.google.*
		 * com.google.gson;
		 * android.annotation;     infact, all android.*
		 * android.drm.mobile1;
		 * android.media;
		 * 
		 * 	com.android.internal.telephony;
		 * com.android.common.speech;
		 * 
		 * android.support.v4.*
		   com.actionbarsherlock.
		   com.facebook.
		 * 
		 */		
		return ht;
	}
	public String getCurrCFGPermutationString()
	{
		String permStr= "";
		for(String cfg: currCFGPermutation)
		{
			permStr += cfg + " <<==>> ";
		}
		
		return permStr;
	}
	private Hashtable loadImmutableObjects()
	{
		Hashtable ht = new Hashtable();
		
		ht.put("Ljava/lang/String;", "");
		ht.put("Ljava/lang/Integer;", "");
		ht.put("Ljava/lang/Byte;", "");
		ht.put("Ljava/lang/Character;", "");
		ht.put("Ljava/lang/Short;", "");
		ht.put("Ljava/lang/Boolean;", "");
		ht.put("Ljava/lang/Long;", "");
		ht.put("Ljava/lang/Double;", "");
		ht.put("Ljava/lang/Float;", "");

		ht.put("I", "");
		ht.put("Z", "");
		ht.put("D", "");
		ht.put("F", "");
		ht.put("J", "");
		ht.put("B", "");
		ht.put("C", "");
		ht.put("S", "");
		
		return ht;
	}
	

	private void setActivityEventCallbacks()
	{
		
		this.eventSpecificCallbacks = new Hashtable();
		ArrayList<String> eventCallbackList = new ArrayList<String>();
		
		//createActivity
		eventCallbackList.add("onCreate");
		eventCallbackList.add("onStart");
		eventCallbackList.add("onPostCreate");
		eventCallbackList.add("onResume");
		eventCallbackList.add("onPostResume");
		this.eventSpecificCallbacks.put(ActivityLCMEvents.createActivity.toString(), eventCallbackList);
		
		//stopActivity
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onPause");
		eventCallbackList.add("onCreateDescription");
		eventCallbackList.add("onSaveInstanceState");
		eventCallbackList.add("onStop");
		this.eventSpecificCallbacks.put(ActivityLCMEvents.stopActivity.toString(), eventCallbackList);
		
		//restartActivity
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onRestart");
		eventCallbackList.add("onStart");
		eventCallbackList.add("onResume");
		eventCallbackList.add("onPostResume");
		this.eventSpecificCallbacks.put(ActivityLCMEvents.restartActivity.toString(), eventCallbackList);

		//killProcess
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onDestroy");
		this.eventSpecificCallbacks.put(ActivityLCMEvents.killProcess.toString(), eventCallbackList);


//		//recreateActivity
//		eventCallbackList = new ArrayList<String>();
//		eventCallbackList.add("onCreate");
//		eventCallbackList.add("onStart");
//		eventCallbackList.add("onPostCreate");
//		eventCallbackList.add("onResume");
//		eventCallbackList.add("onPostResume");		
//		this.eventSpecificCallbacks.put(ActivityLCMEvents.recreateActivity.toString(), eventCallbackList);
		
		//backPressed
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onPause");
		eventCallbackList.add("onStop");
		eventCallbackList.add("onDestroy");
		this.eventSpecificCallbacks.put(ActivityLCMEvents.backPressed.toString(), eventCallbackList);

		//overlapActivity
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onUserLeaveHint");
		eventCallbackList.add("onPause");
		eventCallbackList.add("onCreateDescription");
		eventCallbackList.add("onSaveInstanceState");
		eventCallbackList.add("onStop");		
		this.eventSpecificCallbacks.put(ActivityLCMEvents.overlapActivity.toString(), eventCallbackList);

		//hideActivityPartially
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onUserLeaveHint");
		eventCallbackList.add("onPause");
		eventCallbackList.add("onCreateDescription");
		eventCallbackList.add("onSaveInstanceState");
		this.eventSpecificCallbacks.put(ActivityLCMEvents.hideActivityPartially.toString(), eventCallbackList);

		//gotoActivity
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onResume");
		eventCallbackList.add("onPostResume");		
		this.eventSpecificCallbacks.put(ActivityLCMEvents.gotoActivity.toString(), eventCallbackList);

		//savRestart
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onRestart");
		eventCallbackList.add("onStart");		
		this.eventSpecificCallbacks.put(ActivityLCMEvents.savRestart.toString(), eventCallbackList);

		//savStop
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onStop");		
		this.eventSpecificCallbacks.put(ActivityLCMEvents.savStop.toString(), eventCallbackList);
		
		//gotoStop
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onCreateDescription");		
		eventCallbackList.add("onSaveInstanceState");		
		eventCallbackList.add("onStop");		
		this.eventSpecificCallbacks.put(ActivityLCMEvents.gotoStop.toString(), eventCallbackList);
		
		//confChngPR
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onPause");
		eventCallbackList.add("onSaveInstanceState");	
		eventCallbackList.add("onStop");
		eventCallbackList.add("onDestroy");
		eventCallbackList.add("onCreate");
		eventCallbackList.add("onStart");		
		eventCallbackList.add("onRestoreInstanceState");		
		eventCallbackList.add("onPostCreate");		
		eventCallbackList.add("onResume");		
		eventCallbackList.add("onPostResume");		
		this.eventSpecificCallbacks.put(ActivityLCMEvents.confChngPR.toString(), eventCallbackList);
		
		//confChngPAU
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onSaveInstanceState");
		eventCallbackList.add("onStop");
		eventCallbackList.add("onDestroy");
		eventCallbackList.add("onCreate");
		eventCallbackList.add("onStart");		
		eventCallbackList.add("onRestoreInstanceState");		
		eventCallbackList.add("onPostCreate");		
		eventCallbackList.add("onResume");		
		eventCallbackList.add("onPostResume");			
		eventCallbackList.add("onPause");			
		this.eventSpecificCallbacks.put(ActivityLCMEvents.confChngPAU.toString(), eventCallbackList);
		
		//confChngSTP
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onStop");
		eventCallbackList.add("onDestroy");
		eventCallbackList.add("onCreate");
		eventCallbackList.add("onStart");		
		eventCallbackList.add("onRestoreInstanceState");		
		eventCallbackList.add("onPostCreate");		
		eventCallbackList.add("onResume");		
		eventCallbackList.add("onPostResume");			
		eventCallbackList.add("onPause");			
		this.eventSpecificCallbacks.put(ActivityLCMEvents.confChngSTP.toString(), eventCallbackList);
		
		//confChngSTO
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onDestroy");
		eventCallbackList.add("onCreate");
		eventCallbackList.add("onStart");		
		eventCallbackList.add("onRestoreInstanceState");		
		eventCallbackList.add("onPostCreate");		
		eventCallbackList.add("onResume");		
		eventCallbackList.add("onPostResume");			
		eventCallbackList.add("onPause");			
		this.eventSpecificCallbacks.put(ActivityLCMEvents.confChngSTO.toString(), eventCallbackList);
		
		//confChngPOS
		eventCallbackList = new ArrayList<String>();
		eventCallbackList.add("onDestroy");
		eventCallbackList.add("onCreate");
		eventCallbackList.add("onStart");		
		eventCallbackList.add("onRestoreInstanceState");		
		eventCallbackList.add("onPostCreate");		
		eventCallbackList.add("onResume");		
		eventCallbackList.add("onPostResume");			
		this.eventSpecificCallbacks.put(ActivityLCMEvents.confChngPOS.toString(), eventCallbackList);
	}

	private Hashtable loadActivityCallbacks()
	{
		Hashtable ht = new Hashtable();
		
		ht.put("onSaveInstanceState", "");
		ht.put("onStart", "");
		ht.put("onPostCreate", "");
		ht.put("onResume", "");
		ht.put("onPostResume", "");
		ht.put("onUserLeaveHint", "");
		ht.put("onCreateDescription", "");
		ht.put("onCreate", "");
		ht.put("onPause", "");
		ht.put("onStop", "");
		ht.put("onRestart", "");
		ht.put("onRestoreInstanceState", "");
		ht.put("onDestroy", "");
		
		return ht;
	}
	
	private Hashtable loadServiceCallbacks()
	{
		Hashtable ht = new Hashtable();
		ht.put("onCreate", "");
		ht.put("onBind", "");
		ht.put("onUnbind", "");
		ht.put("onDestroy", "");
		ht.put("onRebind", "");
		ht.put("onStartCommand", "");
		ht.put("onHandleIntent", "");
		ht.put("handleMessage", "");
		
		return ht;
	}
	private Hashtable loadBroadcastReceiverCallbacks()
	{
		Hashtable ht = new Hashtable();
		ht.put("onReceive", "");
		
		return ht;
	}
	private Hashtable loadAsynchTaskCallbacks()
	{
		Hashtable ht = new Hashtable();
		
		ht.put("doInBackground", "");
		ht.put("onPostExecute", "");
		ht.put("onPreExecute", "");
		ht.put("onProgressUpdate", "");
		
		return ht;
	}
	
	private Hashtable loadThreadCallbacks()
	{
		Hashtable ht = new Hashtable();
		
		ht.put("run", "");
		
		return ht;
	}
	private void loadInstructionHandlers()
	{
		InstructionHandlersMap = new Properties();
		String name= "InstructionHandlers.properties";
		if(!this.propertiesPath.isEmpty()){
			name = this.propertiesPath + File.separator + name;
		}
		try {
			InstructionHandlersMap.load(new FileInputStream(name));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		System.out.println(InstructionHandlersMap.toString());
		String x = "";
	}
	
	public Properties getInstructionHandlersMap()
	{
		return InstructionHandlersMap;
	}
	
	
	private  void loadSourceSinkAPIMap()
	{
		
		SourceSinkAPIMap = new Properties();
		String name= "SourceSinkAPI.properties";
		if(!this.propertiesPath.isEmpty()){
			name = this.propertiesPath + File.separator + name;
		}
		try {
			SourceSinkAPIMap.load(new FileInputStream(name));

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String x = "";
	}
	public Properties getSourceSinkAPIMap()
	{
		return SourceSinkAPIMap;
	}

	private void loadInstructionTaintAnalyzers()
	{
		InstructionTaintAnalyzersMap = new Properties();
		String name= "InstructionTaintAnalyzers.properties";
		if(!this.propertiesPath.isEmpty()){
			name = this.propertiesPath + File.separator + name;
		}
		try {
			InstructionTaintAnalyzersMap.load(new FileInputStream(name));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		System.out.println(SourceSinkAPIMap.toString());
		String x = "";
	}
	public Properties getInstructionTaintAnalyzersMap()
	{
		return InstructionTaintAnalyzersMap;
	}

	private void loadApiRulesAnalyzers()
	{
		ApiRulesMap = new Properties();
		String name= "ApiRules.properties";
		if(!this.propertiesPath.isEmpty()){
			name = this.propertiesPath + File.separator + name;
		}
		try {
			ApiRulesMap.load(new FileInputStream(name));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		System.out.println(SourceSinkAPIMap.toString());
		String x = "";
	}
	public Properties getApiRulesMap()
	{
		return ApiRulesMap;
	}

	private void loadApiDefinedAnalyzers()
	{
		ApiDefinedAnalyzers = new Properties();
		String name= "ApiDefinedAnalyzers.properties";
		if(!this.propertiesPath.isEmpty()){
			name = this.propertiesPath + File.separator + name;
		}
		try {
			ApiDefinedAnalyzers.load(new FileInputStream(name));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		System.out.println(SourceSinkAPIMap.toString());
		String x = "";
	}
	public Properties getApiDefinedAnalyzers()
	{
		return ApiDefinedAnalyzers;
	}

	private void loadEventsMapping()
	{
		eventsMapping = new Properties();
		String name= "Events.properties";
		if(!this.propertiesPath.isEmpty()){
			name = this.propertiesPath + File.separator + name;
		}
		try {
			eventsMapping.load(new FileInputStream(name));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		eventMapping.l
	}
	public Properties getEventsMapping() {
		return eventsMapping;
	}

	public SymbolSpace getLocalSymbolSpace() {
		return localSymbolSpace;
	}

	public void setLocalSymbolSpace(SymbolSpace localSymbolSpace) {
		Config.localSymbolSpace = localSymbolSpace;
	}

	public Context getPrevMethodContext() {
		return prevMethodContext;
	}

	public void setPrevMethodContext(Context prevMethodContext) {
		Config.prevMethodContext = prevMethodContext;
	}

	public SymbolSpace getGlobalSymbolSpace() {
		return globalSymbolSpace;
	}

	public void setGlobalSymbolSpace(SymbolSpace globalSymbolSpace) {
		Config.globalSymbolSpace = globalSymbolSpace;
	}

	public AndroidManifest getAndroidManifest() {
		return androidManifest;
	}

	public static void setAndroidManifest(AndroidManifest androidManifest) {
		Config.androidManifest = androidManifest;
	}

	public Hashtable getActivityCallbackList() {
		return activityCallbackList;
	}

	public static void setActivityCallbackList(Hashtable activityCallbackList) {
		Config.activityCallbackList = activityCallbackList;
	}

	public Hashtable getServiceCallbackList() {
		return serviceCallbackList;
	}

	public static void setServiceCallbackList(Hashtable serviceCallbackList) {
		Config.serviceCallbackList = serviceCallbackList;
	}

	public Hashtable getBroadcastReceiverCallbackList() {
		return broadcastReceiverCallbackList;
	}

	public static void setBroadcastReceiverCallbackList(
			Hashtable broadcastReceiverCallbackList) {
		Config.broadcastReceiverCallbackList = broadcastReceiverCallbackList;
	}
	public  Hashtable getAsynchTaskCallbackList() {
		return asynchTaskCallbackList;
	}

	public static void setAsynchTaskCallbackList(Hashtable asynchTaskCallbackList) {
		Config.asynchTaskCallbackList = asynchTaskCallbackList;
	}


	public Hashtable getImmutableObjects() {
		return immutableObjects;
	}


	public static void setImmutableObjects(Hashtable immutableObjects) {
		Config.immutableObjects = immutableObjects;
	}


	public ArrayList<String> getCurrCFGPermutation() {
		return currCFGPermutation;
	}


	public void setCurrCFGPermutation(ArrayList<String> currCFGPermutation) {
		this.currCFGPermutation = currCFGPermutation;
	}


	public boolean isAllPermutationReport() {
		return allPermutationReport;
	}


	public void setAllPermutationReport(boolean allPermutationReport) {
		Config.allPermutationReport = allPermutationReport;
	}



	public static Hashtable getBlackListedAPIs() {
		return blackListedAPIs;
	}



	public static void setBlackListedAPIs(Hashtable blackListedAPIs) {
		Config.blackListedAPIs = blackListedAPIs;
	}



	public Stack getFuncCallStack() {
		return funcCallStack;
	}



	public void setFuncCallStack(Stack funcCallStack) {
		this.funcCallStack = funcCallStack;
	}



	public Hashtable getThreadCallbackList() {
		return threadCallbackList;
	}



	public void setThreadCallbackList(Hashtable threadCallbackList) {
		Config.threadCallbackList = threadCallbackList;
	}



	public boolean isAttackReported() {
		return isAttackReported;
	}



	public void setAttackReported(boolean isAttackReported) {
		this.isAttackReported = isAttackReported;
	}



	public Stack getCompCallStack() {
		return compCallStack;
	}



	public void setCompCallStack(Stack compCallStack) {
		this.compCallStack = compCallStack;
	}



	public static Hashtable getEventSpecificCallbacks() {
		return eventSpecificCallbacks;
	}



	public static void setEventSpecificCallbacks(Hashtable eventSpecificCallbacks) {
		Config.eventSpecificCallbacks = eventSpecificCallbacks;
	}



	public static int getPermutationorder() {
		return permutationOrder;
	}



	public static boolean isIsparentchildhandling() {
		return isParentChildHandling;
	}


	public boolean isExceptionHandling() {
		return isExceptionHandling;
	}


	public void setExceptionHandling(boolean isExceptionHandling) {
		this.isExceptionHandling = isExceptionHandling;
	}


	public boolean isResourceHandling() {
		return isResourceHandling;
	}


	public void setResourceHandling(boolean isResourceHandling) {
		this.isResourceHandling = isResourceHandling;
	}

	public Hashtable getAllComponentTypes() {
		return allComponentTypes;
	}


	public void setAllComponentTypes(Hashtable allComponentTypes) {
		this.allComponentTypes = allComponentTypes;
	}


	public void setAdditionalCallbacks(Hashtable additionalCallbacks) {
		this.additionalCallbacks = additionalCallbacks;
	}


	public boolean isUniqueWarningEnabled() {
		return isUniqueWarningEnabled;
	}


	public void setUniqueWarningEnabled(boolean isUniqueWarningEnabled) {
		this.isUniqueWarningEnabled = isUniqueWarningEnabled;
	}


	public HashSet<String> getViewGroupCallbacks() {
		return viewGroupCallbacks;
	}


	public void setViewGroupCallbacks(HashSet<String> viewGroupCallbacks) {
		this.viewGroupCallbacks = viewGroupCallbacks;
	}


	public MultiValueMap getFuncKeySignatureMap() {
		return funcKeySignatureMap;
	}


	public void setFuncKeySignatureMap(MultiValueMap funcKeySignatureMap) {
		this.funcKeySignatureMap = funcKeySignatureMap;
	}
	
	public HashSet<String> getWebViewCallbacks() {
		return webViewCallbacks;
	}


	public void setWebViewCallbacks(HashSet<String> webViewCallbacks) {
		this.webViewCallbacks = webViewCallbacks;
	}


	public HashSet<String> getSixtyFourBitRegisters() {
		return sixtyFourBitRegisters;
	}


	public void setSixtyFourBitRegisters(HashSet<String> sixtyFourBitRegisters) {
		this.sixtyFourBitRegisters = sixtyFourBitRegisters;
	}


	public boolean isNonManifestCompAnalysis() {
		return isNonManifestCompAnalysis;
	}


	public void setNonManifestCompAnalysis(boolean isNonManifestCompAnalysis) {
		this.isNonManifestCompAnalysis = isNonManifestCompAnalysis;
	}
	
	public Hashtable<String, ArrayList<String>> getAdditionalCallbacks() {
		return additionalCallbacks;
	}


	public boolean isDeepCopyForRecordFieldList() {
		return isDeepCopyForRecordFieldList;
	}


	public void setDeepCopyForRecordFieldList(boolean isDeepCopyForRecordFieldList) {
		this.isDeepCopyForRecordFieldList = isDeepCopyForRecordFieldList;
	}


	public HashMap<String, String> getShellCommands() {
		return shellCommands;
	}


	public void setShellCommands(HashMap<String, String> shellCommands) {
		this.shellCommands = shellCommands;
	}


	public HashSet<String> getCallForwardingCodes() {
		return callForwardingCodes;
	}


	public void setCallForwardingCodes(HashSet<String> callForwardingCodes) {
		this.callForwardingCodes = callForwardingCodes;
	}

	public long getComponentTimeOutInMillis() {
		return componentTimeOutInMillis;
	}


	public void setComponentTimeOutInMillis(long componentTimeOutInMillis) {
		this.componentTimeOutInMillis = componentTimeOutInMillis;
	}


	public long getComponentCurrDuration() {
		return componentCurrDuration;
	}


	public void setComponentCurrDuration(long componentCurrDuration) {
		this.componentCurrDuration = componentCurrDuration;
	}

	public long getComponentAnalysisStartTime() {
		return componentAnalysisStartTime;
	}


	public void setComponentAnalysisStartTime(long componentAnalysisStartTime) {
		this.componentAnalysisStartTime = componentAnalysisStartTime;
	}


	public boolean isComponentAnalysisTimedOut() {
		this.componentCurrDuration = System.currentTimeMillis() - this.componentAnalysisStartTime;
		if(this.componentCurrDuration < this.componentTimeOutInMillis){
			return false;
		}
		return true;
	}


	public String getAndroguardPath() {
		return androguardPath;
	}


	public void setAndroguardPath(String androguardPath) {
		this.androguardPath = androguardPath;
	}


	public String getPropertiesPath() {
		return propertiesPath;
	}


	public void setPropertiesPath(String propertiesPath) {
		this.propertiesPath = propertiesPath;
	}

}
