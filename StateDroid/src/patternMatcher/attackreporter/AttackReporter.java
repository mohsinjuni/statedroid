package patternMatcher.attackreporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import models.symboltable.SourceInfo;

public class AttackReporter {

	private ArrayList<InformationLeakerReport> infoLeakReportList;
	private ArrayList<PremiumSmsSenderReport> premiumSmsReportList;
	private ArrayList<PhoneCallerReport> silentPhoneCallerReportList;
	private ArrayList<PhoneCallerReport> phoneCallerReportList;
	private ArrayList<PhoneCallBlockerReport> phoneCallBlockerReportList;
	private ArrayList<SilentPhoneCallBlockerReport> silentPhoneCallBlockerReportList;
	private ArrayList<IncomingSmsBlockerReport> incomingSmsBlockerReportList;
	private ArrayList<ContentProviderDeletionReport> contentProviderDeletionReportList;
	private ArrayList<AudioVideoRecorderReport> audioVideoRecorderReportList;
	private ArrayList<PhoneCallForwardingReport> phoneCallForwardingReportList;

	private ArrayList<GenericReport> ringerModeSilencerReportList;
	private ArrayList<GenericReport> audioManagerStreamVolumeChangedReportList;
	private ArrayList<GenericReport> keyguardManagerStreamVolumeChangedReportList;
	private ArrayList<GenericReport> audioManagerRingerModeOffReportList;
	private ArrayList<GenericReport> audioManagerRingerModeOnReportList;
	private ArrayList<GenericReport> contentProviderUpdateReportList;
	private ArrayList<GenericReport> phoneCallAnswererReportList;
	private ArrayList<GenericReport> appRemovalReportList;
	private ArrayList<GenericReport> wifiToggleReportList;
	private ArrayList<GenericReport> contentProviderInsertReportList;
	private ArrayList<GenericReport> deviceBrightnessReportList;
	private ArrayList<GenericReport> deviceAirPlaneModeReportList;
	private ArrayList<GenericReport> mobileDataTogglerReportList;
	private ArrayList<GenericReport> abortBroadcastReportList;
	private ArrayList<GenericReport> IncomingSMSAutoReplyReportList;
	private ArrayList<GenericReport> shellExecutionReportList;
	private ArrayList<GenericReport> homeScreenDisplayReportList;
	private ArrayList<GenericReport> resetPasswordReportList;
	private ArrayList<GenericReport> installAppShortcutsReportList;

	//Attack
	private ArrayList<GenericReport> smsBlockAutoReplierReportList;
	private ArrayList<GenericReport> smsAutoReplyBlockerReportList;
	private ArrayList<GenericReport> smsSendAndDeleteReportList;
	private ArrayList<GenericReport> smsDeleteAndSendReportList;
	private ArrayList<GenericReport> fileReadingAndLeakageReportList;
	private ArrayList<GenericReport> audioMgrVibrationOnReportList;
	private ArrayList<GenericReport> audioMgrVibrationOffReportList;
	private ArrayList<GenericReport> audioMgrVibrationRestoreReportList;
	private ArrayList<GenericReport> showCallScreenWithDialpadReportList;
	private ArrayList<GenericReport> nickiSpyCASMReportList;
	private ArrayList<GenericReport> phoneCallStartAndEndReportList;
	private ArrayList<GenericReport> silentLockScreenPhoneCallerReportList;
	private ArrayList<GenericReport> filelEncryptionReportList;
	private ArrayList<GenericReport> lockNowDevieReportList;;

	private ArrayList<InformationStoringIntoDBReport> infoStoringIntoDBReportList;

	private String nonAPISource = "nonAPISource";
	//	private String nonAPIContentProviderSource = "nonAPISource-db";

	private static AttackReporter instance = null;

	private AttackReporter() {
		initializeReports();
	}

	public void resetAllExistingReports() {
		initializeReports();
	}

	public void initializeReports() {
		infoLeakReportList = new ArrayList<InformationLeakerReport>();
		premiumSmsReportList = new ArrayList<PremiumSmsSenderReport>();
		silentPhoneCallerReportList = new ArrayList<PhoneCallerReport>();
		phoneCallerReportList = new ArrayList<PhoneCallerReport>();
		setPhoneCallBlockerReportList(new ArrayList<PhoneCallBlockerReport>());
		silentPhoneCallBlockerReportList = new ArrayList<SilentPhoneCallBlockerReport>();
		incomingSmsBlockerReportList = new ArrayList<IncomingSmsBlockerReport>();
		contentProviderDeletionReportList = new ArrayList<ContentProviderDeletionReport>();
		audioVideoRecorderReportList = new ArrayList<AudioVideoRecorderReport>();
		setRingerModeSilencerReportList(new ArrayList<GenericReport>());
		audioManagerStreamVolumeChangedReportList = new ArrayList<GenericReport>();
		phoneCallForwardingReportList = new ArrayList<PhoneCallForwardingReport>();

		this.keyguardManagerStreamVolumeChangedReportList = new ArrayList<GenericReport>();
		this.audioManagerRingerModeOffReportList = new ArrayList<GenericReport>();
		this.audioManagerRingerModeOnReportList = new ArrayList<GenericReport>();
		this.contentProviderUpdateReportList = new ArrayList<GenericReport>();
		this.phoneCallAnswererReportList = new ArrayList<GenericReport>();
		this.appRemovalReportList = new ArrayList<GenericReport>();
		this.infoStoringIntoDBReportList = new ArrayList<InformationStoringIntoDBReport>();
		this.wifiToggleReportList = new ArrayList<GenericReport>();
		this.contentProviderInsertReportList = new ArrayList<GenericReport>();
		this.deviceBrightnessReportList = new ArrayList<GenericReport>();
		this.deviceAirPlaneModeReportList = new ArrayList<GenericReport>();
		this.mobileDataTogglerReportList = new ArrayList<GenericReport>();
		this.abortBroadcastReportList = new ArrayList<GenericReport>();
		this.IncomingSMSAutoReplyReportList = new ArrayList<GenericReport>();
		this.shellExecutionReportList = new ArrayList<GenericReport>();
		this.homeScreenDisplayReportList = new ArrayList<GenericReport>();
		this.resetPasswordReportList = new ArrayList<GenericReport>();
		this.smsBlockAutoReplierReportList = new ArrayList<GenericReport>();
		this.smsAutoReplyBlockerReportList = new ArrayList<GenericReport>();
		this.smsSendAndDeleteReportList = new ArrayList<GenericReport>();
		this.smsDeleteAndSendReportList = new ArrayList<GenericReport>();
		this.fileReadingAndLeakageReportList = new ArrayList<GenericReport>();
		this.installAppShortcutsReportList = new ArrayList<GenericReport>();
		this.audioMgrVibrationOnReportList = new ArrayList<GenericReport>();
		this.audioMgrVibrationOffReportList = new ArrayList<GenericReport>();
		this.audioMgrVibrationRestoreReportList = new ArrayList<GenericReport>();
		this.setShowCallScreenWithDialpadReportList(new ArrayList<GenericReport>());
		this.setPhoneCallStartAndEndReportList(new ArrayList<GenericReport>());
		this.silentLockScreenPhoneCallerReportList = new ArrayList<GenericReport>();
		this.filelEncryptionReportList = new ArrayList<GenericReport>();
		this.lockNowDevieReportList = new ArrayList<GenericReport>();

		this.nickiSpyCASMReportList = new ArrayList<GenericReport>();
	}

	public static AttackReporter getInstance() {
		if (instance == null) {
			synchronized (AttackReporter.class) {
				if (instance == null) {
					return instance = new AttackReporter();
				}
			}
		}
		return instance;
	}

	//For two reports, one's source APIs might be subsumed by another's source API list. We would report only unique ones.
	public void reportAllUniqueInfoLeakWarnings() {
		if (infoLeakReportList != null && infoLeakReportList.size() > 0) {

			Collections.sort(infoLeakReportList, new Comparator<InformationLeakerReport>() {
				@Override
				public int compare(InformationLeakerReport r1, InformationLeakerReport r2) {
					return r1.getSourceInfoList().size() - r2.getSourceInfoList().size();
				}
			});
			int size = infoLeakReportList.size();
			for (int i = 0; i < size; i++) {
				boolean subsumable = false;
				for (int j = i + 1; j < size; j++) {
					if (isOneListSubsumableByOther(infoLeakReportList.get(i).getSourceInfoList(), infoLeakReportList.get(j)
							.getSourceInfoList())
							&& infoLeakReportList.get(i).getSinkAPI().equalsIgnoreCase(infoLeakReportList.get(j).getSinkAPI())) {
						subsumable = true;
						break;
					}
				}
				if (!subsumable) {
					infoLeakReportList.get(i).printReport();
				}
			}
		}
	}

	private boolean isInfoLeakReportSubsumable(InformationLeakerReport input, InformationLeakerReport host) {
		boolean subsumable = false;
		ArrayList<SourceInfo> list1 = input.getSourceInfoList();
		ArrayList<SourceInfo> list2 = host.getSourceInfoList();

		if (list1.size() < list2.size()) {

		}

		return subsumable;
	}

	private boolean isOneListSubsumableByOther(ArrayList<SourceInfo> list1, ArrayList<SourceInfo> list2) {
		boolean subsumable = true;
		for (SourceInfo si : list1) {
			if (!list2.contains(si)) {
				return false;
			}
		}
		return subsumable;
	}

	public boolean checkIfInstallAppShortcutsReportExists(GenericReport report) {
		if (installAppShortcutsReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfLockNowDeviceReportExists(GenericReport report) {
		if (lockNowDevieReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfFilelEncryptionReportExists(GenericReport report) {
		if (filelEncryptionReportList.contains(report))
			return true;
		return false;
	}

	public ArrayList<GenericReport> getFilelEncryptionReportList() {
		return filelEncryptionReportList;
	}

	public void setFilelEncryptionReportList(ArrayList<GenericReport> filelEncryptionReportList) {
		this.filelEncryptionReportList = filelEncryptionReportList;
	}

	public boolean checkIfPhoneCallStartAndEndReportExists(GenericReport report) {
		if (phoneCallStartAndEndReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfHomeScreenDisplayReportExists(GenericReport report) {
		if (homeScreenDisplayReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfNickiSpyCASMReportExists(GenericReport report) {
		if (nickiSpyCASMReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfSilentLockScreenPhoneCallerReportExists(GenericReport report) {
		if (silentLockScreenPhoneCallerReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfShowCallScreenWithDialpadReportExists(GenericReport report) {
		if (showCallScreenWithDialpadReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfAudioManagerRingerModeOffReportExists(GenericReport report) {
		if (audioManagerRingerModeOffReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfAudioManagerRingerModeOnReportExists(GenericReport report) {
		if (audioManagerRingerModeOnReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfSmsSendAndDeleteReportExists(GenericReport report) {
		if (smsSendAndDeleteReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfAudioMgrVibrationOnReportExists(GenericReport report) {
		if (audioMgrVibrationOnReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfAudioMgrVibrationOffReportExists(GenericReport report) {
		if (audioMgrVibrationOffReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfAudioMgrVibrationRestoreReportExists(GenericReport report) {
		if (audioMgrVibrationRestoreReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfFileReadingAndLeakageReportExists(GenericReport report) {
		if (fileReadingAndLeakageReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfSmsDeleteAndSendReportExists(GenericReport report) {
		if (smsDeleteAndSendReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfSmsBlockAutoReplierReportExists(GenericReport report) {
		if (smsBlockAutoReplierReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfSmsAutoReplyBlockerReportExists(GenericReport report) {
		if (smsAutoReplyBlockerReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfResetPasswordReportExists(GenericReport report) {
		if (resetPasswordReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfAudioVideoRecorderReportExists(AudioVideoRecorderReport report) {
		if (audioVideoRecorderReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfPhoneCallForwardingReportExists(PhoneCallForwardingReport report) {
		if (phoneCallForwardingReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfInfoStoringIntoDBReportExists(InformationStoringIntoDBReport report) {
		if (infoStoringIntoDBReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfDeviceAirPlaneModeReporttExists(GenericReport report) {
		if (deviceAirPlaneModeReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfShellExecutionReportExists(GenericReport report) {
		if (shellExecutionReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfAbortBroadcastReporttExists(GenericReport report) {
		if (abortBroadcastReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfWifiToggleReportExists(GenericReport report) {
		if (wifiToggleReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfIncomingSmsAutoReplyReportExists(GenericReport report) {
		if (IncomingSMSAutoReplyReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfInfoLeakExists(InformationLeakerReport report) {
		if (infoLeakReportList.contains(report))
			return true;

		return false;
	}

	public boolean checkIfDeviceBrightnessReportExists(GenericReport report) {
		if (deviceBrightnessReportList.contains(report))
			return true;

		return false;
	}

	public boolean checkIfPhoneCallBlockerExists(PhoneCallBlockerReport report) {
		if (phoneCallBlockerReportList.contains(report))
			return true;

		return false;
	}

	public boolean checkIfMobileDataTogglerReportExists(GenericReport report) {
		if (mobileDataTogglerReportList.contains(report))
			return true;

		return false;
	}

	public boolean checkIfIncomingSmsBlockerExists(IncomingSmsBlockerReport report) {
		if (incomingSmsBlockerReportList.contains(report))
			return true;

		return false;
	}

	public boolean checkIfSilentPhoneCallBlockerExists(SilentPhoneCallBlockerReport report) {
		if (silentPhoneCallBlockerReportList.contains(report))
			return true;

		return false;
	}

	public ArrayList<SilentPhoneCallBlockerReport> getSilentPhoneCallBlockerReportList() {
		return silentPhoneCallBlockerReportList;
	}

	public void setSilentPhoneCallBlockerReportList(ArrayList<SilentPhoneCallBlockerReport> silentPhoneCallBlockerReportList) {
		this.silentPhoneCallBlockerReportList = silentPhoneCallBlockerReportList;
	}

	public ArrayList<GenericReport> getContentProviderInsertReportList() {
		return contentProviderInsertReportList;
	}

	public void setContentProviderInsertReportList(ArrayList<GenericReport> contentProviderInsertReportList) {
		this.contentProviderInsertReportList = contentProviderInsertReportList;
	}

	public boolean checkIfPremiumSmsSenderExists(PremiumSmsSenderReport report) {
		if (premiumSmsReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfSilentPhoneCallerExists(PhoneCallerReport report) {
		if (silentPhoneCallerReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfPhoneCallerReportExists(PhoneCallerReport report) {
		if (phoneCallerReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfContentProviderDeletionReportExists(ContentProviderDeletionReport report) {
		if (contentProviderDeletionReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfContentProviderUpdateReportExists(GenericReport report) {
		if (contentProviderUpdateReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfContentProviderInsertReportExists(GenericReport report) {
		if (contentProviderInsertReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfRingerModeSilencerReportExists(GenericReport report) {
		if (ringerModeSilencerReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfAudioManagerStreamVolumeChangedReportExists(GenericReport report) {
		if (audioManagerStreamVolumeChangedReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfKeyguardManagerStreamVolumeChangedReportExists(GenericReport report) {
		if (keyguardManagerStreamVolumeChangedReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfPhoneCallAnswererReportExists(GenericReport report) {
		if (phoneCallAnswererReportList.contains(report))
			return true;
		return false;
	}

	public boolean checkIfAppRemovalReportExists(GenericReport report) {
		if (appRemovalReportList.contains(report))
			return true;
		return false;
	}

	public ArrayList<InformationLeakerReport> getInfoLeakReportList() {
		return infoLeakReportList;
	}

	public void setInfoLeakReportList(ArrayList<InformationLeakerReport> infoLeakReportList) {
		this.infoLeakReportList = infoLeakReportList;
	}

	public ArrayList<PremiumSmsSenderReport> getPremiumSmsReportList() {
		return premiumSmsReportList;
	}

	public void setPremiumSmsReportList(ArrayList<PremiumSmsSenderReport> premiumSmsReportList) {
		this.premiumSmsReportList = premiumSmsReportList;
	}

	public ArrayList<PhoneCallerReport> getSilentPhoneCallerReportList() {
		return silentPhoneCallerReportList;
	}

	public void setSilentPhoneCallerReportList(ArrayList<PhoneCallerReport> silentPhoneCallerReportList) {
		this.silentPhoneCallerReportList = silentPhoneCallerReportList;
	}

	public ArrayList<PhoneCallerReport> getPhoneCallerReportList() {
		return phoneCallerReportList;
	}

	public void setPhoneCallerReportList(ArrayList<PhoneCallerReport> phoneCallerReportList) {
		this.phoneCallerReportList = phoneCallerReportList;
	}

	public ArrayList<PhoneCallBlockerReport> getPhoneCallBlockerReportList() {
		return phoneCallBlockerReportList;
	}

	public void setPhoneCallBlockerReportList(ArrayList<PhoneCallBlockerReport> phoneBlockerReportList) {
		this.phoneCallBlockerReportList = phoneBlockerReportList;
	}

	public ArrayList<IncomingSmsBlockerReport> getIncomingSmsBlockerReportList() {
		return incomingSmsBlockerReportList;
	}

	public void setIncomingSmsBlockerReportList(ArrayList<IncomingSmsBlockerReport> incomingSmsBlockerReportList) {
		this.incomingSmsBlockerReportList = incomingSmsBlockerReportList;
	}

	public ArrayList<ContentProviderDeletionReport> getContentProviderDeletionReportList() {
		return contentProviderDeletionReportList;
	}

	public void setContentProviderDeletionReportList(ArrayList<ContentProviderDeletionReport> contentProviderDeletionReportList) {
		this.contentProviderDeletionReportList = contentProviderDeletionReportList;
	}

	public ArrayList<AudioVideoRecorderReport> getAudioVideoRecorderReportList() {
		return audioVideoRecorderReportList;
	}

	public void setAudioVideoRecorderReportList(ArrayList<AudioVideoRecorderReport> audioVideoRecorderReportList) {
		this.audioVideoRecorderReportList = audioVideoRecorderReportList;
	}

	public ArrayList<GenericReport> getRingerModeSilencerReportList() {
		return ringerModeSilencerReportList;
	}

	public void setRingerModeSilencerReportList(ArrayList<GenericReport> ringerModeSilencerReportList) {
		this.ringerModeSilencerReportList = ringerModeSilencerReportList;
	}

	public ArrayList<GenericReport> getWifiToggleReportList() {
		return wifiToggleReportList;
	}

	public void setWifiToggleReportList(ArrayList<GenericReport> wifiToggleReportList) {
		this.wifiToggleReportList = wifiToggleReportList;
	}

	public ArrayList<GenericReport> getAudioManagerStreamVolumeChangedReportList() {
		return audioManagerStreamVolumeChangedReportList;
	}

	public ArrayList<GenericReport> getKeyguardManagerStreamVolumeChangedReportList() {
		return keyguardManagerStreamVolumeChangedReportList;
	}

	public ArrayList<GenericReport> getContentProviderUpdateReportList() {
		return contentProviderUpdateReportList;
	}

	public String getNonAPISource() {
		return nonAPISource;
	}

	public ArrayList<GenericReport> getPhoneCallAnswererReportList() {
		return phoneCallAnswererReportList;
	}

	public ArrayList<GenericReport> getAppRemovalReportList() {
		return appRemovalReportList;
	}

	public ArrayList<InformationStoringIntoDBReport> getInfoStoringIntoDBReportList() {
		return infoStoringIntoDBReportList;
	}

	public void setInfoStoringIntoDBReportList(ArrayList<InformationStoringIntoDBReport> infoStoringIntoDBReportList) {
		this.infoStoringIntoDBReportList = infoStoringIntoDBReportList;
	}

	public ArrayList<GenericReport> getDeviceBrightnessReportList() {
		return deviceBrightnessReportList;
	}

	public void setDeviceBrightnessReportList(ArrayList<GenericReport> deviceBrightnessReportList) {
		this.deviceBrightnessReportList = deviceBrightnessReportList;
	}

	public ArrayList<GenericReport> getDeviceAirPlaneModeReportList() {
		return deviceAirPlaneModeReportList;
	}

	public void setDeviceAirPlaneModeReportList(ArrayList<GenericReport> deviceAirPlaneModeReportList) {
		this.deviceAirPlaneModeReportList = deviceAirPlaneModeReportList;
	}

	public ArrayList<GenericReport> getMobileDataTogglerReportList() {
		return mobileDataTogglerReportList;
	}

	public void setMobileDataTogglerReportList(ArrayList<GenericReport> mobileDataTogglerReportList) {
		this.mobileDataTogglerReportList = mobileDataTogglerReportList;
	}

	public ArrayList<GenericReport> getAbortBroadcastReportList() {
		return abortBroadcastReportList;
	}

	public void setAbortBroadcastReportList(ArrayList<GenericReport> abortBroadcastReportList) {
		this.abortBroadcastReportList = abortBroadcastReportList;
	}

	public ArrayList<GenericReport> getIncomingSMSAutoReplyReportList() {
		return IncomingSMSAutoReplyReportList;
	}

	public void setIncomingSMSAutoReplyReportList(ArrayList<GenericReport> incomingSMSAutoReplyReportList) {
		IncomingSMSAutoReplyReportList = incomingSMSAutoReplyReportList;
	}

	public ArrayList<GenericReport> getShellExecutionReportList() {
		return shellExecutionReportList;
	}

	public void setShellExecutionReportList(ArrayList<GenericReport> shellExecutionReportList) {
		this.shellExecutionReportList = shellExecutionReportList;
	}

	public ArrayList<PhoneCallForwardingReport> getPhoneCallForwardingReportList() {
		return phoneCallForwardingReportList;
	}

	public void setPhoneCallForwardingReportList(ArrayList<PhoneCallForwardingReport> phoneCallForwardingReportList) {
		this.phoneCallForwardingReportList = phoneCallForwardingReportList;
	}

	public ArrayList<GenericReport> getHomeScreenDisplayReportList() {
		return homeScreenDisplayReportList;
	}

	public void setHomeScreenDisplayReportList(ArrayList<GenericReport> homeScreenDisplayReportList) {
		this.homeScreenDisplayReportList = homeScreenDisplayReportList;
	}

	public ArrayList<GenericReport> getResetPasswordReportList() {
		return resetPasswordReportList;
	}

	public void setResetPasswordReportList(ArrayList<GenericReport> resetPasswordReportList) {
		this.resetPasswordReportList = resetPasswordReportList;
	}

	public ArrayList<GenericReport> getSmsBlockAutoReplierReportList() {
		return smsBlockAutoReplierReportList;
	}

	public void setSmsBlockAutoReplierReportList(ArrayList<GenericReport> smsBlockAutoReplierReportList) {
		this.smsBlockAutoReplierReportList = smsBlockAutoReplierReportList;
	}

	public ArrayList<GenericReport> getSmsAutoReplyBlockerReportList() {
		return smsAutoReplyBlockerReportList;
	}

	public void setSmsAutoReplyBlockerReportList(ArrayList<GenericReport> smsAutoReplyBlockerReportList) {
		this.smsAutoReplyBlockerReportList = smsAutoReplyBlockerReportList;
	}

	public ArrayList<GenericReport> getSmsSendAndDeleteReportList() {
		return smsSendAndDeleteReportList;
	}

	public void setSmsSendAndDeleteReportList(ArrayList<GenericReport> smsSendAndDeleteReportList) {
		this.smsSendAndDeleteReportList = smsSendAndDeleteReportList;
	}

	public ArrayList<GenericReport> getSmsDeleteAndSendReportList() {
		return smsDeleteAndSendReportList;
	}

	public void setSmsDeleteAndSendReportList(ArrayList<GenericReport> smsDeleteAndSendReportList) {
		this.smsDeleteAndSendReportList = smsDeleteAndSendReportList;
	}

	public ArrayList<GenericReport> getFileReadingAndLeakageReportList() {
		return fileReadingAndLeakageReportList;
	}

	public void setFileReadingAndLeakageReportList(ArrayList<GenericReport> fileReadingAndLeakageReportList) {
		this.fileReadingAndLeakageReportList = fileReadingAndLeakageReportList;
	}

	public ArrayList<GenericReport> getInstallAppShortcutsReportList() {
		return installAppShortcutsReportList;
	}

	public void setInstallAppShortcutsReportList(ArrayList<GenericReport> installAppShortcutsReportList) {
		this.installAppShortcutsReportList = installAppShortcutsReportList;
	}

	public ArrayList<GenericReport> getAudioMgrVibrationOnReportList() {
		return audioMgrVibrationOnReportList;
	}

	public void setAudioMgrVibrationOnReportList(ArrayList<GenericReport> audioMgrVibrationOnReportList) {
		this.audioMgrVibrationOnReportList = audioMgrVibrationOnReportList;
	}

	public ArrayList<GenericReport> getAudioMgrVibrationOffReportList() {
		return audioMgrVibrationOffReportList;
	}

	public void setAudioMgrVibrationOffReportList(ArrayList<GenericReport> audioMgrVibrationOffReportList) {
		this.audioMgrVibrationOffReportList = audioMgrVibrationOffReportList;
	}

	public void setAudioManagerStreamVolumeChangedReportList(ArrayList<GenericReport> audioManagerStreamVolumeChangedReportList) {
		this.audioManagerStreamVolumeChangedReportList = audioManagerStreamVolumeChangedReportList;
	}

	public void setKeyguardManagerStreamVolumeChangedReportList(ArrayList<GenericReport> keyguardManagerStreamVolumeChangedReportList) {
		this.keyguardManagerStreamVolumeChangedReportList = keyguardManagerStreamVolumeChangedReportList;
	}

	public void setContentProviderUpdateReportList(ArrayList<GenericReport> contentProviderUpdateReportList) {
		this.contentProviderUpdateReportList = contentProviderUpdateReportList;
	}

	public void setPhoneCallAnswererReportList(ArrayList<GenericReport> phoneCallAnswererReportList) {
		this.phoneCallAnswererReportList = phoneCallAnswererReportList;
	}

	public void setAppRemovalReportList(ArrayList<GenericReport> appRemovalReportList) {
		this.appRemovalReportList = appRemovalReportList;
	}

	public ArrayList<GenericReport> getAudioMgrVibrationRestoreReportList() {
		return audioMgrVibrationRestoreReportList;
	}

	public void setAudioMgrVibrationRestoreReportList(ArrayList<GenericReport> audioMgrVibrationRestoreReportList) {
		this.audioMgrVibrationRestoreReportList = audioMgrVibrationRestoreReportList;
	}

	public ArrayList<GenericReport> getNickiSpyCASMReportList() {
		return nickiSpyCASMReportList;
	}

	public void setNickiSpyCASMReportList(ArrayList<GenericReport> nickiSpyCASMReportList) {
		this.nickiSpyCASMReportList = nickiSpyCASMReportList;
	}

	public ArrayList<GenericReport> getAudioManagerRingerModeOffReportList() {
		return audioManagerRingerModeOffReportList;
	}

	public void setAudioManagerRingerModeOffReportList(ArrayList<GenericReport> audioManagerRingerModeOffReportList) {
		this.audioManagerRingerModeOffReportList = audioManagerRingerModeOffReportList;
	}

	public ArrayList<GenericReport> getAudioManagerRingerModeOnReportList() {
		return audioManagerRingerModeOnReportList;
	}

	public void setAudioManagerRingerModeOnReportList(ArrayList<GenericReport> audioManagerRingerModeOnReportList) {
		this.audioManagerRingerModeOnReportList = audioManagerRingerModeOnReportList;
	}

	public ArrayList<GenericReport> getShowCallScreenWithDialpadReportList() {
		return showCallScreenWithDialpadReportList;
	}

	public void setShowCallScreenWithDialpadReportList(ArrayList<GenericReport> showCallScreenWithDialpadReportList) {
		this.showCallScreenWithDialpadReportList = showCallScreenWithDialpadReportList;
	}

	public ArrayList<GenericReport> getPhoneCallStartAndEndReportList() {
		return phoneCallStartAndEndReportList;
	}

	public void setPhoneCallStartAndEndReportList(ArrayList<GenericReport> phoneCallStartAndEndReportList) {
		this.phoneCallStartAndEndReportList = phoneCallStartAndEndReportList;
	}

	public ArrayList<GenericReport> getSilentLockScreenPhoneCallerReportList() {
		return silentLockScreenPhoneCallerReportList;
	}

	public void setSilentLockScreenPhoneCallerReportList(ArrayList<GenericReport> silentLockScreenPhoneCallerReportList) {
		this.silentLockScreenPhoneCallerReportList = silentLockScreenPhoneCallerReportList;
	}

	public ArrayList<GenericReport> getLockNowDevieReportList() {
		return lockNowDevieReportList;
	}

	public void setLockNowDevieReportList(ArrayList<GenericReport> lockNowDevieReportList) {
		this.lockNowDevieReportList = lockNowDevieReportList;
	}

}
