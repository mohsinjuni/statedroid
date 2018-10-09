
package enums;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class Constants {
	
	
	private Hashtable audioSourceConstants = new Hashtable();
	private Hashtable outputFormatConstants = new Hashtable();
	private Hashtable audioEncoderConstants = new Hashtable();
	private Hashtable videoEncoderConstants = new Hashtable();
	private Hashtable videoSourceConstants = new Hashtable();
	private Hashtable sensitiveDbUris = new Hashtable();
	private Hashtable sensitiveURIsURLs = new Hashtable();
	
	private Hashtable audioManagerStreamTypes = new Hashtable();
	private Hashtable audioManagerStreamActions = new Hashtable();
	private Hashtable ringerModeConstants = new Hashtable();

	private static Constants instance=null;
	
	
	private Constants()
	{
		loadAudioSourceConstants();	
		loadOutputFormatConstants();
		loadAudioEncoderConstants();

		loadVideoSourceConstants();
		loadSensitiveDbUris();
		loadVideoEncoderConstants();
		loadAudioManagerStreamTypes();
		loadAudioManagerStreamActions();
		loadRingerModeConstants();
		loadSensitiveURIsURLs();
	}
	
	public static Constants getInstance()
	{
		if(null == instance)
		{
			synchronized (Constants.class){
				if(instance ==null)
				{
				  instance = new Constants();
				}
			}
		}
		return instance;
	}
	public static enum MediaRecorderFields{
		audio_encoder,
		video_encoder,
		audio_source,
		max_duration_ms,
		output_file,
		output_format,
		video_source,
		video_frame_rate,
		camcorder_profile
	}
	
	private void loadSensitiveURIsURLs()
	{
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Sms$Outbox;->CONTENT_URI", "android.permission.RECEIVE_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Sms$Sent;->CONTENT_URI", "android.permission.RECEIVE_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Sms$Conversations;->CONTENT_URI", "android.permission.RECEIVE_SMS");
		sensitiveURIsURLs.put("Lcom/android/internal/telephony/SMSDispatcher;->mRawUri", "android.permission.RECEIVE_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Sms$Draft;->CONTENT_URI", "android.permission.RECEIVE_SMS");
		sensitiveURIsURLs.put("Lcom/android/providers/telephony/SmsProvider;->ICC_URI", "android.permission.RECEIVE_SMS");
		sensitiveURIsURLs.put("Lcom/android/mms/transaction/MessageStatusService;->STATUS_URI", "android.permission.RECEIVE_SMS");
		sensitiveURIsURLs.put("Lcom/android/providers/telephony/SmsProvider;->NOTIFICATION_URI", "android.permission.RECEIVE_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Sms;->CONTENT_URI", "android.permission.RECEIVE_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Sms$Inbox;->CONTENT_URI", "android.permission.RECEIVE_SMS");
		sensitiveURIsURLs.put("Lcom/android/mms/ui/ManageSimMessages;->ICC_URI", "android.permission.RECEIVE_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Mms;->CONTENT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$MmsSms;->CONTENT_DRAFT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Lcom/android/mms/TempFileProvider;->SCRAP_CONTENT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Threads;->OBSOLETE_THREADS_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$MmsSms;->CONTENT_UNDELIVERED_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Mms$Inbox;->CONTENT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$MmsSms;->CONTENT_LOCKED_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$MmsSms;->CONTENT_FILTER_BYPHONE_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$MmsSms;->CONTENT_CONVERSATIONS_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Mms$Draft;->CONTENT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Lcom/android/mms/data/Conversation;->sAllThreadsUri", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Mms;->REPORT_REQUEST_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Threads;->CONTENT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Mms$Outbox;->CONTENT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Mms$Rate;->CONTENT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Lcom/android/mms/data/RecipientIdCache;->sAllCanonical", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Mms;->REPORT_STATUS_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$MmsSms$PendingMessages;->CONTENT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Lcom/android/mms/data/RecipientIdCache;->sSingleCanonicalAddressUri", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Mms$Sent;->CONTENT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$MmsSms;->CONTENT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$Threads;->THREAD_ID_CONTENT_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Lcom/android/mms/transaction/MessagingNotification;->UNDELIVERED_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$MmsSms;->SEARCH_URI", "android.permission.READ_SMS");
		sensitiveURIsURLs.put("Landroid/provider/Downloads$Impl;->ALL_DOWNLOADS_CONTENT_URI", "android.permission.INTERNET");
		sensitiveURIsURLs.put("Landroid/provider/Downloads$Impl;->PUBLICLY_ACCESSIBLE_DOWNLOADS_URI", "android.permission.INTERNET");
		sensitiveURIsURLs.put("Landroid/provider/Downloads$Impl;->CONTENT_URI", "android.permission.INTERNET");
		sensitiveURIsURLs.put("Lcom/android/launcher2/LauncherProvider;->CONTENT_APPWIDGET_RESET_URI", "com.android.launcher.permission.WRITE_SETTINGS");
		sensitiveURIsURLs.put("Lcom/android/launcher2/LauncherSettings$Favorites;->CONTENT_URI_NO_NOTIFICATION", "com.android.launcher.permission.WRITE_SETTINGS");
		sensitiveURIsURLs.put("Lcom/android/launcher2/LauncherSettings$Favorites;->CONTENT_URI", "com.android.launcher.permission.WRITE_SETTINGS");
		sensitiveURIsURLs.put("Landroid/provider/BrowserContract$SyncState;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Lcom/android/browser/provider/BrowserProvider2$OmniboxSuggestions;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/Browser;->SEARCHES_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/Browser;->BOOKMARKS_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/BrowserContract;->AUTHORITY_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Lcom/android/browser/provider/BrowserProvider2$Thumbnails;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/BrowserContract$Settings;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/BrowserContract$Combined;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Lcom/android/browser/provider/SnapshotProvider$Snapshots;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/BrowserContract$Searches;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/BrowserContract$History;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Lcom/android/browser/provider/SnapshotProvider;->AUTHORITY_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/BrowserContract$Accounts;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/BrowserContract$Bookmarks;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/BrowserContract$ImageMappings;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/BrowserContract$Images;->CONTENT_URI", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/BrowserContract$Bookmarks;->CONTENT_URI_DEFAULT_FOLDER", "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$Presence;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Contacts;->CONTENT_LOOKUP_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/mms/data/Contact$ContactsCache;->PHONES_WITH_PRESENCE_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Profile;->CONTENT_VCARD_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$ContactMethods;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Data;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$Settings;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$People;->DELETED_CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/contacts/model/AccountTypeWithDataSet;->RAW_CONTACTS_URI_LIMIT_1", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$DataUsageFeedback;->FEEDBACK_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$RawContactsEntity;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Contacts;->CONTENT_STREQUENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$Groups;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$CommonDataKinds$Email;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$StreamItems;->CONTENT_PHOTO_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$StreamItems;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$CommonDataKinds$Callable;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/common/contacts/BaseEmailAddressAdapter$DirectoryListQuery;->URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Contacts;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Settings;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Contacts;->CONTENT_MULTI_VCARD_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$StatusUpdates;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$StreamItems;->CONTENT_LIMIT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Groups;->CONTENT_SUMMARY_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$DisplayPhoto;->CONTENT_MAX_DIMENSIONS_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/providers/contacts/LegacyApiSupport;->LIVE_FOLDERS_CONTACTS_WITH_PHONES_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$GroupMembership;->RAW_CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$GroupMembership;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$ContactMethods;->CONTENT_EMAIL_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$CommonDataKinds$Email;->CONTENT_FILTER_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$RawContacts;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$DisplayPhoto;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$RawContactsEntity;->PROFILE_CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$Extensions;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$AggregationExceptions;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$StatusUpdates;->PROFILE_CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Profile;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$People;->WITH_EMAIL_OR_IM_FILTER_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/contacts/model/AccountWithDataSet;->RAW_CONTACTS_URI_LIMIT_1", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/email/activity/setup/AccountSetupNames;->PROFILE_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$SyncState;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/contacts/list/DirectoryListLoader$DirectoryQuery;->URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Contacts;->CONTENT_FREQUENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$ProviderStatus;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Contacts;->CONTENT_FILTER_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/nfc/P2pLinkManager;->PROFILE_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/providers/contacts/LegacyApiSupport;->LIVE_FOLDERS_CONTACTS_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$People;->CONTENT_FILTER_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Profile;->CONTENT_RAW_CONTACTS_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$Groups;->DELETED_CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/contacts/GroupListLoader;->GROUP_LIST_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/common/contacts/DataUsageStatUpdater$DataUsageFeedback;->FEEDBACK_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/providers/contacts/LegacyApiSupport;->LIVE_FOLDERS_CONTACTS_FAVORITES_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Directory;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$Photos;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Groups;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$ProfileSyncState;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/mms/data/Contact$ContactsCache;->EMAIL_WITH_PRESENCE_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$People;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Contacts;->CONTENT_STREQUENT_FILTER_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$CommonDataKinds$Phone;->CONTENT_FILTER_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$Phones;->CONTENT_FILTER_URL", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Contacts;->CONTENT_GROUP_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$Contacts;->CONTENT_VCARD_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$Organizations;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/ex/chips/BaseRecipientAdapter$DirectoryListQuery;->URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$CommonDataKinds$StructuredPostal;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/Contacts$Phones;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$CommonDataKinds$Email;->CONTENT_LOOKUP_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$DataUsageFeedback;->DELETE_USAGE_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$PhoneLookup;->CONTENT_FILTER_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract;->AUTHORITY_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$CommonDataKinds$Callable;->CONTENT_FILTER_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Landroid/provider/ContactsContract$CommonDataKinds$Phone;->CONTENT_URI", "android.permission.WRITE_CONTACTS");
		sensitiveURIsURLs.put("Lcom/android/providers/media/MediaProvider;->ALBUMART_URI", "android.permission.WRITE_EXTERNAL_STORAGE");
		sensitiveURIsURLs.put("Lcom/android/music/MusicUtils;->sArtworkUri", "android.permission.WRITE_EXTERNAL_STORAGE");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$Attendees;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$Colors;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$Calendars;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$Reminders;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$ExtendedProperties;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$Instances;->CONTENT_SEARCH_BY_DAY_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$Instances;->CONTENT_BY_DAY_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Lcom/android/providers/calendar/CalendarAlarmManager;->SCHEDULE_ALARM_REMOVE_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$EventsEntity;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$CalendarEntity;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$Events;->CONTENT_EXCEPTION_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$CalendarCache;->URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$Events;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$SyncState;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$Instances;->CONTENT_SEARCH_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$Instances;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$CalendarAlerts;->CONTENT_URI_BY_INSTANCE", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$EventDays;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Lcom/android/providers/calendar/CalendarAlarmManager;->SCHEDULE_ALARM_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/CalendarContract$CalendarAlerts;->CONTENT_URI", "android.permission.WRITE_CALENDAR");
		sensitiveURIsURLs.put("Landroid/provider/VoicemailContract$Status;->CONTENT_URI", "com.android.voicemail.permission.ADD_VOICEMAIL");
		sensitiveURIsURLs.put("Landroid/provider/VoicemailContract$Voicemails;->CONTENT_URI", "com.android.voicemail.permission.ADD_VOICEMAIL");
		sensitiveURIsURLs.put("Landroid/provider/CallLog$Calls;->CONTENT_FILTER_URI", "com.android.voicemail.permission.ADD_VOICEMAIL");
		sensitiveURIsURLs.put("Landroid/provider/CallLog$Calls;->CONTENT_URI_WITH_VOICEMAIL", "com.android.voicemail.permission.ADD_VOICEMAIL");
		sensitiveURIsURLs.put("Landroid/provider/CallLog$Calls;->CONTENT_URI", "com.android.voicemail.permission.ADD_VOICEMAIL");
		sensitiveURIsURLs.put("Landroid/provider/CallLog;->CONTENT_URI", "com.android.voicemail.permission.ADD_VOICEMAIL");
		sensitiveURIsURLs.put("Landroid/provider/Telephony$CellBroadcasts;->CONTENT_URI", "android.permission.READ_CELL_BROADCASTS");
		sensitiveURIsURLs.put("Lcom/android/email/provider/EmailProvider;->FOLDER_STATUS_URI", "com.android.email.permission.ACCESS_PROVIDER");
		sensitiveURIsURLs.put("Lcom/android/emailcommon/provider/EmailContent;->MAILBOX_NOTIFICATION_URI", "com.android.email.permission.ACCESS_PROVIDER");
		sensitiveURIsURLs.put("Lcom/android/email/provider/EmailProvider;->INTEGRITY_CHECK_URI", "com.android.email.permission.ACCESS_PROVIDER");
		sensitiveURIsURLs.put("Lcom/android/emailcommon/provider/EmailContent;->CONTENT_URI", "com.android.email.permission.ACCESS_PROVIDER");
		sensitiveURIsURLs.put("Lcom/android/emailcommon/provider/EmailContent;->CONTENT_NOTIFIER_URI", "com.android.email.permission.ACCESS_PROVIDER");
		sensitiveURIsURLs.put("Lcom/android/email/provider/EmailProvider;->ACCOUNT_BACKUP_URI", "com.android.email.permission.ACCESS_PROVIDER");
		sensitiveURIsURLs.put("Lcom/android/email/provider/EmailProvider;->FOLDER_REFRESH_URI", "com.android.email.permission.ACCESS_PROVIDER");
		sensitiveURIsURLs.put("Lcom/android/emailcommon/provider/EmailContent;->MAILBOX_MOST_RECENT_MESSAGE_URI", "com.android.email.permission.ACCESS_PROVIDER");
		sensitiveURIsURLs.put("Lcom/android/emailcommon/utility/AttachmentUtilities;->CONTENT_URI", "com.android.email.permission.READ_ATTACHMENT");
		sensitiveURIsURLs.put("Lcom/android/bluetooth/opp/BluetoothShare;->CONTENT_URI", "android.permission.ACCESS_BLUETOOTH_SHARE");
		sensitiveURIsURLs.put("Lcom/android/providers/userdictionary/UserDictionaryProvider;->CONTENT_URI", "android.permission.READ_USER_DICTIONARY");
		sensitiveURIsURLs.put("Landroid/provider/UserDictionary;->CONTENT_URI", "android.permission.READ_USER_DICTIONARY");
		sensitiveURIsURLs.put("Landroid/provider/UserDictionary$Words;->CONTENT_URI", "android.permission.READ_USER_DICTIONARY");
		sensitiveURIsURLs.put("Landroid/provider/Settings$Secure;->CONTENT_URI", "android.permission.WRITE_SETTINGS");
		sensitiveURIsURLs.put("Landroid/provider/Settings$Bookmarks;->CONTENT_URI", "android.permission.WRITE_SETTINGS");
		sensitiveURIsURLs.put("Landroid/provider/Settings$System;->CONTENT_URI", "android.permission.WRITE_SETTINGS");

	}
	private void loadSensitiveDbUris()
	{
		//sms
		sensitiveDbUris.put("'content://sms/inbox'", "");
		sensitiveDbUris.put("content://sms/inbox", "");
		sensitiveDbUris.put("'content://sms/conversations'", "");
		sensitiveDbUris.put("content://sms/conversations", "");
		sensitiveDbUris.put("'content://sms/draft'", "");
		sensitiveDbUris.put("content://sms/draft", "");
		sensitiveDbUris.put("'content://sms/outbox'", "");
		sensitiveDbUris.put("content://sms/outbox", "");
		sensitiveDbUris.put("'content://sms/sent'", "");
		sensitiveDbUris.put("content://sms/sent", "");

		//contacts
		sensitiveDbUris.put("Landroid/provider/ContactsContract$Data;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$RawContacts;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$Contacts;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$StatusUpdates;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$RawContactsEntity;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$Groups;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$CommonDataKinds$Email;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$CommonDataKinds$Nickname;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$CommonDataKinds$Organization;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$CommonDataKinds$Phone;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$CommonDataKinds$Photo;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$CommonDataKinds$Relation;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$CommonDataKinds$Website;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$CommonDataKinds$StructuredName;->CONTENT_URI", "");
		sensitiveDbUris.put("Landroid/provider/ContactsContract$CommonDataKinds$StructuredPostal;->CONTENT_URI", "");
		//TODO ContactsContract.Contacts.CONTENT_LOOKUP_URI
		

	////Browser
		sensitiveDbUris.put("content://com.android.browser/bookmarks", "");
		sensitiveDbUris.put("Landroid/provider/Browser;->BOOKMARKS_URI", "");
		sensitiveDbUris.put("content://com.android.browser/accounts", "");
		sensitiveDbUris.put("content://com.android.browser/history", "");
		sensitiveDbUris.put("content://com.android.browser/images", "");
		sensitiveDbUris.put("content://com.android.browser/searches", "");
		sensitiveDbUris.put("content://com.android.browser/syncstate", "");
		sensitiveDbUris.put("content://com.android.browser/ image_mappings", ""); 
		sensitiveDbUris.put("content://com.android.browser/combined", "");
		sensitiveDbUris.put("content://com.android.browser/settings", "");


		////Chrome Browser
		sensitiveDbUris.put("content://com.android.chrome.browser/bookmarks", "");
		
		////VoicemailContract
		sensitiveDbUris.put("content://com.android.voicemail/voicemail", "");
		sensitiveDbUris.put("content://com.android.voicemail/status", "");

		////UserDictionary
		sensitiveDbUris.put("content://user_dictionary", "");
		sensitiveDbUris.put("content://user_dictionary/words", "");

		////ContactsContract
		sensitiveDbUris.put("content://com.android.contact", "");
		///directories”

		sensitiveDbUris.put("content://com.android.contact", "");

		///syncstate”
		sensitiveDbUris.put("content://com.android.contacts/profile/syncstate", "");
		sensitiveDbUris.put("content://com.android.contact", "");

		///contacts”
		sensitiveDbUris.put("content://com.android.contact", "");

		///profile”
		sensitiveDbUris.put("content://com.android.contact", "");

		///deleted_contacts”
		sensitiveDbUris.put("content://com.android.contact", "");

		///raw_contacts”

		sensitiveDbUris.put("content://com.android.contact", "");
		///stream_items”

		sensitiveDbUris.put("content://com.android.contact", "");
		///data”
		sensitiveDbUris.put("content://com.android.contact", "");
		///raw_contact_entities”

		sensitiveDbUris.put("content://com.android.contact", "");
		
		///status_updates”
		sensitiveDbUris.put("content://com.android.contacts/data/phones", "");
		sensitiveDbUris.put("content://com.android.contacts/data/emails", "");
		sensitiveDbUris.put("content://com.android.contacts/data/postals", "");
		sensitiveDbUris.put("content://com.android.contacts/data/callables", "");
		sensitiveDbUris.put("content://com.android.contacts/data/contactables", "");

		sensitiveDbUris.put("content://com.android.contact", "");
		///groups”

		sensitiveDbUris.put("content://com.android.contact", "");
		///settings”
		sensitiveDbUris.put("content://com.android.contact", "");
		///provider_status”

		sensitiveDbUris.put("content://com.android.contact", "");
		///display_photo”


		//CalendarContract
		sensitiveDbUris.put("content://com.android.calendar", "");


		sensitiveDbUris.put("content://com.android.calendar/calendar_entities", "");

		sensitiveDbUris.put("content://com.android.calendar/calendars", "");

		sensitiveDbUris.put("content://com.android.calendar/attendees", "");

		sensitiveDbUris.put("content://com.android.calendar/event_entities", "");

		sensitiveDbUris.put("content://com.android.calendar/events", "");

		sensitiveDbUris.put("content://com.android.calendar/instances/when", "");

		sensitiveDbUris.put("content://com.android.calendar/instances/groupbyday", "");

		sensitiveDbUris.put("content://com.android.calendar/reminders", "");

		sensitiveDbUris.put("content://com.android.calendar/calendar_alerts", "");

		sensitiveDbUris.put("content://com.android.calendar/colors", "");

		sensitiveDbUris.put("content://com.android.calendar/extendedproperties", "");


		sensitiveDbUris.put("content://com.android.calendar/syncstate", "");
		//CallLog
		sensitiveDbUris.put("content://call_log", "");
		sensitiveDbUris.put("content://call_log/calls", "");

		//Contacts (deprecated interface but still can be used to access data)
		sensitiveDbUris.put("content://contacts", "");
		sensitiveDbUris.put("content://contacts/settings", "");

		sensitiveDbUris.put("content://contacts/people", "");

		sensitiveDbUris.put("content://contacts/groups", "");


		sensitiveDbUris.put("content://contacts/phones", "");

		sensitiveDbUris.put("content://contacts/groupmembership", "");

		sensitiveDbUris.put("content://contacts/contact_methods", "");

		sensitiveDbUris.put("content://contacts/presence", "");

		sensitiveDbUris.put("content://contacts/organizations", "");

		sensitiveDbUris.put("content://contacts/photos", "");

		sensitiveDbUris.put("content://contacts/extensions", "");

		//Downloads
		sensitiveDbUris.put("content://downloads/my_downloads", "");

		//Telephony
		sensitiveDbUris.put("content://sms", "");

		sensitiveDbUris.put("content://sms/inbox", "");

		sensitiveDbUris.put("content://sms/sent", "");

		sensitiveDbUris.put("content://sms/draft", "");

		sensitiveDbUris.put("content://sms/outbox", "");

		sensitiveDbUris.put("content://sms/conversations", "");

		sensitiveDbUris.put("content://mms-sms/conversations", "");

		sensitiveDbUris.put("content://mms", "");

		sensitiveDbUris.put("content://mms/inbox", "");

		sensitiveDbUris.put("content://mms/sent", "");

		sensitiveDbUris.put("content://mms/drafts", "");

		sensitiveDbUris.put("content://mms/outbox", "");

		sensitiveDbUris.put("content://mms/rate", "");

		sensitiveDbUris.put("content://mms-sms", "");

		sensitiveDbUris.put("content://mms-sms/pending", "");

		sensitiveDbUris.put("content://telephony/carriers", "");

		sensitiveDbUris.put("content://cellbroadcasts", "");

		//Settings
		sensitiveDbUris.put("content://settings/system", "");
		sensitiveDbUris.put("content://settings/secure", "");

		sensitiveDbUris.put("content://settings/global", "");
		sensitiveDbUris.put("content://settings/bookmarks", "");

		

		//contacts
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$Data;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$RawContacts;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$Contacts;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$StatusUpdates;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$RawContactsEntity;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$Groups;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$CommonDataKinds$Email;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$CommonDataKinds$Nickname;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$CommonDataKinds$Organization;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$CommonDataKinds$Phone;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$CommonDataKinds$Photo;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$CommonDataKinds$Relation;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$CommonDataKinds$Website;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$CommonDataKinds$StructuredName;->CONTENT_URI'", "");
		sensitiveDbUris.put("'Landroid/provider/ContactsContract$CommonDataKinds$StructuredPostal;->CONTENT_URI'", "");
		//TODO ContactsContract.Contacts.CONTENT_LOOKUP_URI
		

	////Browser
		sensitiveDbUris.put("'content://com.android.browser/bookmarks'", "");
		sensitiveDbUris.put("'Landroid/provider/Browser;->BOOKMARKS_URI'", "");
		sensitiveDbUris.put("'content://com.android.browser/accounts'", "");
		sensitiveDbUris.put("'content://com.android.browser/history'", "");
		sensitiveDbUris.put("'content://com.android.browser/images'", "");
		sensitiveDbUris.put("'content://com.android.browser/searches'", "");
		sensitiveDbUris.put("'content://com.android.browser/syncstate'", "");
		sensitiveDbUris.put("'content://com.android.browser/image_mappings'", ""); 
		sensitiveDbUris.put("'content://com.android.browser/combined'", "");
		sensitiveDbUris.put("'content://com.android.browser/settings'", "");


		////Chrome Browser
		sensitiveDbUris.put("'content://com.android.chrome.browser/bookmarks'", "");

		////VoicemailContract
		sensitiveDbUris.put("'content://com.android.voicemail/voicemail'", "");
		sensitiveDbUris.put("'content://com.android.voicemail/status'", "");

		////UserDictionary
		sensitiveDbUris.put("'content://user_dictionary'", "");
		sensitiveDbUris.put("'content://user_dictionary/words'", "");

		////ContactsContract
		sensitiveDbUris.put("'content://com.android.contact'", "");
		///directories”

		sensitiveDbUris.put("'content://com.android.contact'", "");

		///syncstate”
		sensitiveDbUris.put("'content://com.android.contacts/profile/syncstate'", "");
		sensitiveDbUris.put("'content://com.android.contact'", "");

		///contacts”
		sensitiveDbUris.put("'content://com.android.contact'", "");

		///profile”
		sensitiveDbUris.put("'content://com.android.contact'", "");

		///deleted_contacts”
		sensitiveDbUris.put("'content://com.android.contact'", "");

		///raw_contacts”

		sensitiveDbUris.put("'content://com.android.contact'", "");
		///stream_items”

		sensitiveDbUris.put("'content://com.android.contact'", "");
		///data”
		sensitiveDbUris.put("'content://com.android.contact'", "");
		///raw_contact_entities”

		sensitiveDbUris.put("'content://com.android.contact'", "");
		
		///status_updates”
		sensitiveDbUris.put("'content://com.android.contacts/data/phones'", "");
		sensitiveDbUris.put("'content://com.android.contacts/data/emails'", "");
		sensitiveDbUris.put("'content://com.android.contacts/data/postals'", "");
		sensitiveDbUris.put("'content://com.android.contacts/data/callables'", "");
		sensitiveDbUris.put("'content://com.android.contacts/data/contactables'", "");

		sensitiveDbUris.put("'content://com.android.contact'", "");
		///groups”

		sensitiveDbUris.put("'content://com.android.contact'", "");
		///settings”
		sensitiveDbUris.put("'content://com.android.contact'", "");
		///provider_status”

		sensitiveDbUris.put("'content://com.android.contact'", "");
		///display_photo”

		//CalendarContract
		sensitiveDbUris.put("'content://com.android.calendar'", "");

		//Sim Contacts
		sensitiveDbUris.put("'content://icc/adn'", "simContacts");

		sensitiveDbUris.put("'content://com.android.calendar/calendar_entities'", "");

		sensitiveDbUris.put("'content://com.android.calendar/calendars'", "");

		sensitiveDbUris.put("'content://com.android.calendar/attendees'", "");

		sensitiveDbUris.put("'content://com.android.calendar/event_entities'", "");

		sensitiveDbUris.put("'content://com.android.calendar/events'", "");

		sensitiveDbUris.put("'content://com.android.calendar/instances/when'", "");

		sensitiveDbUris.put("'content://com.android.calendar/instances/groupbyday'", "");

		sensitiveDbUris.put("'content://com.android.calendar/reminders'", "");

		sensitiveDbUris.put("'content://com.android.calendar/calendar_alerts'", "");

		sensitiveDbUris.put("'content://com.android.calendar/colors'", "");

		sensitiveDbUris.put("'content://com.android.calendar/extendedproperties'", "");


		sensitiveDbUris.put("'content://com.android.calendar/syncstate'", "");
		//CallLog
		sensitiveDbUris.put("'content://call_log'", "");
		sensitiveDbUris.put("'content://call_log/calls'", "");

		//Contacts (deprecated interface but still can be used to access data)
		sensitiveDbUris.put("'content://contacts'", "");
		sensitiveDbUris.put("'content://contacts/settings'", "");

		sensitiveDbUris.put("'content://contacts/people'", "");

		sensitiveDbUris.put("'content://contacts/groups'", "");


		sensitiveDbUris.put("'content://contacts/phones'", "");

		sensitiveDbUris.put("'content://contacts/groupmembership'", "");

		sensitiveDbUris.put("'content://contacts/contact_methods'", "");

		sensitiveDbUris.put("'content://contacts/presence'", "");

		sensitiveDbUris.put("'content://contacts/organizations'", "");

		sensitiveDbUris.put("'content://contacts/photos'", "");

		sensitiveDbUris.put("'content://contacts/extensions'", "");

		//Downloads
		sensitiveDbUris.put("'content://downloads/my_downloads'", "");

		//Telephony
		sensitiveDbUris.put("'content://sms'", "");

		sensitiveDbUris.put("'content://sms/inbox'", "");

		sensitiveDbUris.put("'content://sms/sent'", "");

		sensitiveDbUris.put("'content://sms/draft'", "");

		sensitiveDbUris.put("'content://sms/outbox'", "");

		sensitiveDbUris.put("'content://sms/conversations'", "");

		sensitiveDbUris.put("'content://mms-sms/conversations'", "");

		sensitiveDbUris.put("'content://mms'", "");

		sensitiveDbUris.put("'content://mms/inbox'", "");

		sensitiveDbUris.put("'content://mms/sent'", "");

		sensitiveDbUris.put("'content://mms/drafts'", "");

		sensitiveDbUris.put("'content://mms/outbox'", "");

		sensitiveDbUris.put("'content://mms/rate'", "");

		sensitiveDbUris.put("'content://mms-sms'", "");

		sensitiveDbUris.put("'content://mms-sms/pending'", "");

		sensitiveDbUris.put("'content://telephony/carriers'", "");

		sensitiveDbUris.put("'content://cellbroadcasts'", "");

		//Settings
		sensitiveDbUris.put("'content://settings/system'", "");
		sensitiveDbUris.put("'content://settings/secure'", "");

		sensitiveDbUris.put("'content://settings/global'", "");
		sensitiveDbUris.put("'content://settings/bookmarks'", "");
		
		getURIsFromPScout();
	}
	
	private void getURIsFromPScout(){
		
		sensitiveDbUris.put("content://browser/bookmarks/search_suggest_query", "");
		sensitiveDbUris.put("'content://browser/bookmarks/search_suggest_query'", "");
		sensitiveDbUris.put("content://browser", "");
		sensitiveDbUris.put("'content://browser'", "");
		sensitiveDbUris.put("content://browser", "");
		sensitiveDbUris.put("'content://browser'", "");
		sensitiveDbUris.put("content://com.android.bluetooth.opp/btopp", "");
		sensitiveDbUris.put("'content://com.android.bluetooth.opp/btopp'", "");
		sensitiveDbUris.put("content://com.android.bluetooth.opp/btopp", "");
		sensitiveDbUris.put("'content://com.android.bluetooth.opp/btopp'", "");
		sensitiveDbUris.put("content://com.android.browser/bookmarks/search_suggest_query", "");
		sensitiveDbUris.put("'content://com.android.browser/bookmarks/search_suggest_query'", "");
		sensitiveDbUris.put("content://com.android.browser.home", "");
		sensitiveDbUris.put("'content://com.android.browser.home'", "");
		sensitiveDbUris.put("content://com.android.browser", "");
		sensitiveDbUris.put("'content://com.android.browser'", "");
		sensitiveDbUris.put("content://com.android.browser", "");
		sensitiveDbUris.put("'content://com.android.browser'", "");
		sensitiveDbUris.put("content://com.android.contacts/contacts/.*/photo", "");
		sensitiveDbUris.put("'content://com.android.contacts/contacts/.*/photo'", "");
		sensitiveDbUris.put("content://com.android.contacts.*", "");
		sensitiveDbUris.put("'content://com.android.contacts.*'", "");
		sensitiveDbUris.put("content://com.android.contacts", "");
		sensitiveDbUris.put("'content://com.android.contacts'", "");
		sensitiveDbUris.put("content://com.android.contacts/search_suggest_query", "");
		sensitiveDbUris.put("'content://com.android.contacts/search_suggest_query'", "");
		sensitiveDbUris.put("content://com.android.contacts/search_suggest_shortcut", "");
		sensitiveDbUris.put("'content://com.android.contacts/search_suggest_shortcut'", "");
		sensitiveDbUris.put("content://com.android.contacts", "");
		sensitiveDbUris.put("'content://com.android.contacts'", "");
		sensitiveDbUris.put("content://com.android.email.attachmentprovider", "");
		sensitiveDbUris.put("'content://com.android.email.attachmentprovider'", "");
		sensitiveDbUris.put("content://com.android.email.notifier", "");
		sensitiveDbUris.put("'content://com.android.email.notifier'", "");
		sensitiveDbUris.put("content://com.android.email.notifier", "");
		sensitiveDbUris.put("'content://com.android.email.notifier'", "");
		sensitiveDbUris.put("content://com.android.email.provider", "");
		sensitiveDbUris.put("'content://com.android.email.provider'", "");
		sensitiveDbUris.put("content://com.android.email.provider", "");
		sensitiveDbUris.put("'content://com.android.email.provider'", "");
		sensitiveDbUris.put("content://com.android.exchange.directory.provider", "");
		sensitiveDbUris.put("'content://com.android.exchange.directory.provider'", "");
		sensitiveDbUris.put("content://com.android.launcher2.settings", "");
		sensitiveDbUris.put("'content://com.android.launcher2.settings'", "");
		sensitiveDbUris.put("content://com.android.launcher2.settings", "");
		sensitiveDbUris.put("'content://com.android.launcher2.settings'", "");
		sensitiveDbUris.put("content://com.android.mms.SuggestionsProvider", "");
		sensitiveDbUris.put("'content://com.android.mms.SuggestionsProvider'", "");
		sensitiveDbUris.put("content://com.android.mms.SuggestionsProvider/search_suggest_query", "");
		sensitiveDbUris.put("'content://com.android.mms.SuggestionsProvider/search_suggest_query'", "");
		sensitiveDbUris.put("content://com.android.mms.SuggestionsProvider/search_suggest_shortcut", "");
		sensitiveDbUris.put("'content://com.android.mms.SuggestionsProvider/search_suggest_shortcut'", "");
		sensitiveDbUris.put("content://com.android.voicemail", "");
		sensitiveDbUris.put("'content://com.android.voicemail'", "");
		sensitiveDbUris.put("content://com.android.voicemail", "");
		sensitiveDbUris.put("'content://com.android.voicemail'", "");
		sensitiveDbUris.put("content://com.google.provider.NotePad.*", "");
		sensitiveDbUris.put("'content://com.google.provider.NotePad.*'", "");
		sensitiveDbUris.put("content://contacts/search_suggest_query", "");
		sensitiveDbUris.put("'content://contacts/search_suggest_query'", "");
		sensitiveDbUris.put("content://contacts/search_suggest_shortcut", "");
		sensitiveDbUris.put("'content://contacts/search_suggest_shortcut'", "");
		sensitiveDbUris.put("content://downloads/all_downloads/", "");
		sensitiveDbUris.put("'content://downloads/all_downloads/'", "");
		sensitiveDbUris.put("content://downloads/all_downloads", "");
		sensitiveDbUris.put("'content://downloads/all_downloads'", "");
		sensitiveDbUris.put("content://downloads/all_downloads", "");
		sensitiveDbUris.put("'content://downloads/all_downloads'", "");
		sensitiveDbUris.put("content://downloads/download", "");
		sensitiveDbUris.put("'content://downloads/download'", "");
		sensitiveDbUris.put("content://downloads/download", "");
		sensitiveDbUris.put("'content://downloads/download'", "");
		sensitiveDbUris.put("content://icc", "");
		sensitiveDbUris.put("'content://icc'", "");
		sensitiveDbUris.put("content://icc", "");
		sensitiveDbUris.put("'content://icc'", "");
		sensitiveDbUris.put("content://media/external/", "");
		sensitiveDbUris.put("'content://media/external/'", "");
		sensitiveDbUris.put("content://media/external/", "");
		sensitiveDbUris.put("'content://media/external/'", "");
		sensitiveDbUris.put("content://media/external/", "");
		sensitiveDbUris.put("'content://media/external/'", "");
		sensitiveDbUris.put("content://mms/drm/", "");
		sensitiveDbUris.put("'content://mms/drm/'", "");
		sensitiveDbUris.put("content://mms/part/", "");
		sensitiveDbUris.put("'content://mms/part/'", "");
		sensitiveDbUris.put("content://settings", "");
		sensitiveDbUris.put("'content://settings'", "");
		
	}

	
	private void loadAudioSourceConstants(){
		
		// Though all constants are integers, we store them as strings for easy comparisons during analysis.
		audioSourceConstants.put("0", "DEFAULT");   
		audioSourceConstants.put("1", "MIC");
		audioSourceConstants.put("2", "VOICE_UPLINK");
		audioSourceConstants.put("3", "VOICE_DOWNLINK");
		audioSourceConstants.put("4", "VOICE_CALL");
		audioSourceConstants.put("5", "CAMCORDER");
		audioSourceConstants.put("6", "VOICE_RECOGNITION");
		audioSourceConstants.put("7", "VOICE_COMMUNICATION");
		audioSourceConstants.put("8", "REMOTE_SUBMIX");
	}

	private void loadVideoSourceConstants()
	{
		videoSourceConstants.put("0", "DEFAULT");   
		videoSourceConstants.put("1", "CAMERA");
		videoSourceConstants.put("2", "SURFACE");
	}

	private void loadRingerModeConstants(){
		ringerModeConstants.put("0", "makes the phone silent by setting ringer-mode to RINGER_MODE_SILENT");   
		ringerModeConstants.put("1", "makes the phone silent with vibration by setting ringer-mode to RINGER_MODE_VIBRATE");   
		ringerModeConstants.put("2", "sets phone's ringer mode to normal (audible) mode using RINGER_MODE_NORMAL.");   
	}

	private void loadAudioManagerStreamTypes(){
		audioManagerStreamTypes.put("4", "STREAM_ALARM");   
		audioManagerStreamTypes.put("8", "STREAM_DTMF");
		audioManagerStreamTypes.put("3", "STREAM_MUSIC");
		audioManagerStreamTypes.put("5", "STREAM_NOTIFICATION");
		audioManagerStreamTypes.put("2", "STREAM_RING");
		audioManagerStreamTypes.put("1", "STREAM_SYSTEM");
		audioManagerStreamTypes.put("0", "STREAM_VOICE_CALL");
	}

	private void loadAudioManagerStreamActions(){
//		audioManagerStreamActions.put("-1", "ADJUST_LOWER");   
//		audioManagerStreamActions.put("-100", "ADJUST_MUTE");   
//		audioManagerStreamActions.put("1", "ADJUST_RAISE");   
//		audioManagerStreamActions.put("0", "ADJUST_SAME");   
//		audioManagerStreamActions.put("101", "ADJUST_TOGGLE_MUTE");   
//		audioManagerStreamActions.put("100", "ADJUST_UNMUTE");   

		audioManagerStreamActions.put("-1", "reducing volume");   
		audioManagerStreamActions.put("-100", "muting volume");   
		audioManagerStreamActions.put("1", "increasing volume");   
		audioManagerStreamActions.put("0", "restoring the previous volume");   
		audioManagerStreamActions.put("101", "muting volume");   
		audioManagerStreamActions.put("100", "unmuting volume");   
}
	
	private void loadOutputFormatConstants()
	{
		// Though all constants are integers, we store them as strings for easy comparisons during analysis.
		outputFormatConstants.put("0", "DEFAULT");   
		outputFormatConstants.put("1", "3GPP");
		outputFormatConstants.put("2", "MPEG4");
		outputFormatConstants.put("3", "AMR_NB");
		outputFormatConstants.put("4", "AMR_WB");
		outputFormatConstants.put("6", "AAC_ADTS");

	}
	private void loadAudioEncoderConstants()
	{
		audioEncoderConstants.put("0", "DEFAULT");   
		audioEncoderConstants.put("1", "AMR_NB");
		audioEncoderConstants.put("2", "AMR_WB");
		audioEncoderConstants.put("3", "AAC");
		audioEncoderConstants.put("4", "HE_AAC");
		audioEncoderConstants.put("5", "AAC_ELD");
		
	}
	
	private void loadVideoEncoderConstants()
	{
		videoEncoderConstants.put("0", "DEFAULT");   
		videoEncoderConstants.put("1", "H263");
		videoEncoderConstants.put("2", "H264");
		videoEncoderConstants.put("3", "MPEG_4_SP");
		videoEncoderConstants.put("4", "VP8");
		
	}

	private Hashtable loadContactsContacts()
	{
		// Though all constants are integers, we store them as strings for easy comparisons during analysis.
		Hashtable ht = new Hashtable();
		
		//Most of places, developers don't know the index value, so they first getColumnIndex(), so I won't 
		// handle the case where getString(0) is called with integer-const value given by the developer.

		return ht;
	}
	
	public Hashtable getAudioSourceConstants() {
		return audioSourceConstants;
	}

	public Hashtable getOutputFormatConstants() {
		return outputFormatConstants;
	}

	public Hashtable getAudioEncoderConstants() {
		return audioEncoderConstants;
	}

	public Hashtable getSensitiveDbUris() {
		return sensitiveDbUris;
	}
	
	public Hashtable getVideoSourceConstants() {
		return videoSourceConstants;
	}

	public Hashtable getVideoEncoderConstants() {
		return videoEncoderConstants;
	}

	public Hashtable getAudioManagerStreamTypes() {
		return audioManagerStreamTypes;
	}

	public Hashtable getAudioManagerStreamActions() {
		return audioManagerStreamActions;
	}
	
	public Hashtable getRingerModeConstants() {
		return ringerModeConstants;
	}

	public Hashtable getSensitiveURIsURLs() {
		return sensitiveURIsURLs;
	}


}
