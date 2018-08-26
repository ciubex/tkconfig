/**
 * This file is part of TKConfig application.
 *
 * Copyright (C) 2018 Claudiu Ciobotariu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ro.ciubex.tkconfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ro.ciubex.tkconfig.models.Command;
import ro.ciubex.tkconfig.models.Constants;
import ro.ciubex.tkconfig.models.ContactChooseHandler;
import ro.ciubex.tkconfig.models.ContactModel;
import ro.ciubex.tkconfig.models.GpsContact;
import ro.ciubex.tkconfig.models.History;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * This is main application class. Here are defined the progress dialog and
 * information popup.
 *
 * @author Claudiu Ciobotariu
 */
public class TKConfigApplication extends Application {
    private final static String TAG = TKConfigApplication.class.getName();
    private static Context mContext;
    private ProgressDialog progressDialog;
    private List<Command> commands;
    private List<History> histories;
    private List<GpsContact> contacts;
    private Locale defaultLocale;
    private boolean mustReloadCommands;
    private SmsManager smsManager;
    private ContactChooseHandler contactChooseHandler;
    private List<ContactModel> phoneContacts;
    private Uri sendFolderUri;
    private String defaultBackupPath;

    private static int mSdkInt = 8;
    private SharedPreferences mSharedPreferences;

    private static final String KEY_PREFIX_HISTORY = "history_";
    private boolean mMustRestart;

    private static final String KEY_HAVE_PERMISSIONS_ASKED = "havePermissionsAsked";
    public static final String PERMISSION_FOR_READ_CONTACTS = "android.permission.READ_CONTACTS";
    public static final String PERMISSION_FOR_SEND_SMS = "android.permission.SEND_SMS";
    public static final String PERMISSION_FOR_WRITE_SMS = "android.permission.WRITE_SMS";
    public static final String PERMISSION_FOR_READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    public static final String PERMISSION_FOR_READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    public static final String PERMISSION_FOR_WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    public static final String KEY_APP_THEME = "appTheme";

    public static final List<String> FUNCTIONAL_PERMISSIONS = Arrays.asList(
            PERMISSION_FOR_READ_CONTACTS,
            PERMISSION_FOR_SEND_SMS,
            PERMISSION_FOR_WRITE_SMS,
            PERMISSION_FOR_READ_PHONE_STATE,
            PERMISSION_FOR_READ_EXTERNAL_STORAGE,
            PERMISSION_FOR_WRITE_EXTERNAL_STORAGE
    );

    /**
     * This method is invoked when the application is created.
     *
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        TKConfigApplication.mContext = getApplicationContext();
        mSdkInt = android.os.Build.VERSION.SDK_INT;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i(TAG, "TKConfigApplication started!");
        commands = new ArrayList<Command>() {
            private static final long serialVersionUID = 8883327862834322486L;

            public boolean add(Command mt) {
                int index = Collections.binarySearch(this, mt);
                if (index < 0)
                    index = ~index;
                super.add(index, mt);
                return true;
            }
        };
        histories = new ArrayList<>();
        contacts = new ArrayList<>();
        defaultLocale = Locale.getDefault();
        smsManager = SmsManager.getDefault();
        contactsLoad();
    }

    public static Context getAppContext() {
        return TKConfigApplication.mContext;
    }

    /**
     * Retrieve default application locale
     *
     * @return Default locale used on application
     */
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * Get the command list.
     *
     * @return The command list.
     */
    public List<Command> getCommands() {
        return commands;
    }

    public void populateDefaultCommands() {
        if (commands.size() > 0) {
            commands.clear();
        }
        commands.add(new Command(
                "Initialization",
                "begin?password?",
                "Send \"begin+password\" in SMS to the unit, it will reply \"begin ok\" and initialize all the settings."));
        commands.add(new Command("Change the password",
                "password?password? ?new-password?",
                "Send SMS password+old password+space+new password to change the password."));
        commands.add(new Command(
                "Authorization",
                "admin?password? ?admin-phone?",
                "Send SMS admin+password+space+cell phone number to set up a authorized number."));
        commands.add(new Command(
                "Auto Track",
                "t030s***n?password?",
                "Send SMS t030s***n+password to the unit, it will reply SMS heaps of times. Remark: the interval must not be less than 20s."));
        commands.add(new Command("Cancel Auto Track", "notn?password?",
                "Send notn+password to delete the auto track."));
        commands.add(new Command(
                "Swtich to Monitor",
                "monitor?password?",
                "Send SMS monitor+password to the unit, and it will reply \"monitor ok !\" and switch to \"monitor\" mode."));
        commands.add(new Command(
                "Swtich to Track",
                "tracker?password?",
                "Send SMS tracker+password to the unit, and it will reply \"tracker ok !\" and switch to \"track\" mode."));
        commands.add(new Command(
                "Geo-fence",
                "stockade?password? ?longitudeEW?,?latitudeNS?; ?longitudeEW?,?latitudeNS?",
                "Set up a geo-fence for the unit to restrict its movements within a district. The unit will send the message to the authorized numbers when it breaches the district."));
        commands.add(new Command("Cancel Geo-fence", "nostockade?password?",
                "Send SMS nostockade+password to deactivate the Geo-fence function."));
        commands.add(new Command(
                "Movement alert",
                "move?password?",
                "When the unit stays immobile in a place for 3-10 minutes, the user can send SMS move+password to the unit, then the unit will reply \"move ok\"."));
        commands.add(new Command("Cancel Movement alert", "nomove?password?",
                "Send SMS nomove+password to deactivate the movement alert."));
        commands.add(new Command(
                "Overspeed alert",
                "speed?password? ?speed?",
                "Send SMS speed+password+space+080 to the unit (suppose the speed is 80km/h), and it will reply \"speed ok\"."));
        commands.add(new Command("Cancel Overspeed alert", "nospeed?password?",
                "Send SMS nospeed+password to deactivate the overspeed alert."));
        commands.add(new Command("IMEI checking", "imei?password?",
                "Send SMS imei+password to the unit to check the IMEI number."));
        commands.add(new Command("SMS center",
                "adminsms?password? ?phone-number?",
                "Send SMS adminsms+password+space+cell phone number to set the SMS center."));
        commands.add(new Command("Cancel SMS center", "noadminsms?password?",
                "Send SMS noadminsms+password to cancel the SMS center."));
        commands.add(new Command(
                "GPRS: Set server address",
                "adminip?password? ?server-ip? ?server-port?",
                "Send SMS adminip+password+space+IP address+space+port. If setup successful, the tracker will reply SMS \"adminip ok\"."));
        commands.add(new Command("Cancel GPRS", "noadminip?password?",
                "Send SMS noadminip+password"));
        commands.add(new Command(
                "Set APN",
                "apn?password? ?apn-name?",
                "Send SMS apn+password+space+specify apn. If setup successful, the tracker will reply SMS \"APN ok\"."));
        // TK102 b
        commands.add(new Command(
                "Low battery alert ON",
                "lowbattery?password? on",
                "TK102-2: Tracker will send SMS \"low battery+latitude/longitude\" to authorized numbers 2 times in total in 15 minutes interval when voltage of battery is going to be about 3.55V"));
        commands.add(new Command(
                "Low battery alert OFF",
                "lowbattery?password? off",
                "TK102-2: Tracker will stop send SMS \"low battery\" allerts."));
        commands.add(new Command(
                "State checking",
                "check?password?",
                "TK102-2: Check tracker status for GSM, GPS, GPRS and battery."));
        commands.add(new Command(
                "Version Checking",
                "version?password?",
                "TK102-2: Check tracker version."));
        commands.add(new Command(
                "Motion sensor ON",
                "shake?password? ?sensitive?",
                "TK102-2: This command will enable shake sensor to send alerts when the tracker is shaked, the ?sensitive? parameter should have a value between 1, for the least sensitive degree and 10 for the most sensitive degree."));
        commands.add(new Command(
                "SD storing data ON",
                "sdlog?password? 1",
                "TK102-2: Activate the function of storing data in SD card."));
        commands.add(new Command(
                "SD storing data OFF",
                "sdlog?password? 0",
                "TK102-2: Deactivate the function of storing data in SD card."));
        commands.add(new Command(
                "SD send data to the GPRS server ON",
                "readsd?password? 1",
                "TK102-2: Activate the function to send data from the SD card to the GPRS server."));
        commands.add(new Command(
                "SD send data to the GPRS server OFF",
                "readsd?password? 0",
                "TK102-2: Deactivate the function to send data from the SD card to the GPRS server."));
        commands.add(new Command(
                "SMS position link",
                "smslink?password?",
                "TK102-2: Turn tracker messages with the tracker positions as links."));
        commands.add(new Command(
                "SMS position text",
                "smstext?password?",
                "TK102-2: Turn tracker back to text messages, default format messages."));
        commands.add(new Command(
                "SMS link once",
                "smslinkone?password?",
                "TK102-2: Tracker will send one messages with the position as a link."));
        commands.add(new Command(
                "Set APN user",
                "apnuser?password? ?username?",
                "TK102-2: Set the APN user name."));
        commands.add(new Command(
                "Set APN password",
                "apnuser?password? ?apnpassword?",
                "TK102-2: Set the APN password."));
        commands.add(new Command(
                "Set Time Zone",
                "time zone?password? ?timezone?",
                "TK102-2: Set the tracker reports time zone."));
        commands.add(new Command(
                "Tlimit function ON",
                "tlimit?password? ?distance?",
                "TK102-2: Activate the limited distance for autor reporting mode (t030s***n...). Distance can be a numeric value between 50 and 5999 meters."));
        commands.add(new Command(
                "Tlimit function OFF",
                "tlimit?password? 0",
                "TK102-2: Deactivate Tlimit function."));
        commands.add(new Command(
                "Set GPRS MODE-UDP",
                "gprsmode?password? 1",
                "TK102-2: Set GPRS MODE to UDP protocol."));
        commands.add(new Command(
                "Set GPRS MODE-TCP",
                "gprsmode?password? 0",
                "TK102-2: Set GPRS MODE to TCP protocol. (default protocol)"));
    }

    /**
     * This method should be used to prepare the initial parameters of specified
     * command. The parameters are taken from the application preferences.
     *
     * @param command The command to be prepared.
     */
    public void prepareCommandParameters(Command command) {
        if (command.hasParameters()) {
            String parameterValue;
            for (String parameterName : command.getParameters()) {
                // skip if is the password
                if (!Constants.PASSWORD.equals(parameterName)) {
                    parameterValue = mSharedPreferences.getString(parameterName,
                            parameterName);
                    command.setParameterValue(parameterName, parameterValue);
                }
            }
        }
    }

    /**
     * This method is used to obtain the GPS tracker phone number.
     *
     * @return The GPS tracker phone number.
     */
    private String getGPSPhoneNumber() {
        return mSharedPreferences.getString("gpsPhoneNumber", "");
    }

    /**
     * Save the command parameters value to the application preferences.
     *
     * @param command The command which parameters should to be saved to the
     *                application preferences.
     */
    public void saveCommandParameters(Command command) {
        if (command.hasParameters()) {
            String parameterValue;
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            for (String parameterName : command.getParameters()) {
                // skip if is the password
                if (!Constants.PASSWORD.equals(parameterName)) {
                    parameterValue = command.getParameterValue(parameterName);
                    if (parameterValue != null) {
                        editor.putString(parameterName, parameterValue);
                    }
                }
            }
            editor.commit();
        }
    }

    /**
     * Method used when the application should be closed.
     */
    public void onClose() {
        phoneContacts = null;
        hideProgressDialog();
    }

    /**
     * Method used to save the commands to the application preferences.
     */
    public void commandsSave() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("commands", commands.size());
        int i = 0;
        for (Command command : commands) {
            editor.putString("command_" + i + "_name", command.getName());
            editor.putString("command_" + i + "_cmd", command.getCommand());
            editor.putString("command_" + i + "_desc", command.getDescription());
            i++;
        }
        editor.commit();
    }

    /**
     * Method used to load the commands from the application preferences.
     */
    public void commandsLoad() {
        int count = mSharedPreferences.getInt("commands", 0);
        int i = 0;
        if (commands.size() > 0) {
            commands.clear();
        }
        while (i < count) {
            commands.add(new Command(mSharedPreferences.getString("command_" + i
                    + "_name", ""), mSharedPreferences.getString("command_" + i
                    + "_cmd", ""), mSharedPreferences.getString("command_" + i
                    + "_desc", "")));
            i++;
        }
        if (i == 0) {
            populateDefaultCommands();
        }
    }

    /**
     * Remove the commands stored on the application preferences.
     */
    public void commandsStoreCleanup() {
        int count = mSharedPreferences.getInt("commands", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        int i = 0;
        while (i < count) {
            editor.remove("command_" + i + "_name");
            editor.remove("command_" + i + "_cmd");
            editor.remove("command_" + i + "_desc");
            i++;
        }
        if (i > 0) {
            editor.remove("commands");
            editor.commit();
        }
    }

    /**
     * Check if should be reloaded the command list adapter.
     *
     * @return True, if should be reloaded the command list adapter.
     */
    public boolean isMustReloadCommands() {
        return mustReloadCommands;
    }

    /**
     * Set the reload command list adapter flag.
     *
     * @param mustReloadCommands Boolean flag to be set to the reload command list adapter
     *                           flag.
     */
    public void setMustReloadCommands(boolean mustReloadCommands) {
        this.mustReloadCommands = mustReloadCommands;
    }

    /**
     * This will show a progress dialog using a context and a message ID from
     * application string resources.
     *
     * @param context   The context where should be displayed the progress dialog.
     * @param messageId The string resource id.
     */
    public void showProgressDialog(Context context, int messageId) {
        showProgressDialog(context, getString(messageId));
    }

    /**
     * This will show a progress dialog using a context and the message to be
     * showed on the progress dialog.
     *
     * @param context The context where should be displayed the progress dialog.
     * @param message The message displayed inside of progress dialog.
     */
    public void showProgressDialog(Context context, String message) {
        hideProgressDialog();
        progressDialog = ProgressDialog.show(context,
                getString(R.string.please_wait), message);
    }

    /**
     * Method used to hide the progress dialog.
     */
    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    /**
     * Add an history event to the histories list.
     *
     * @param history The history event to be added.
     * @return Always will be returned true.
     */
    public boolean addHistory(History history) {
        return histories.add(history);
    }

    /**
     * Obtain the histories list.
     *
     * @return The histories list.
     */
    public List<History> getHistories() {
        return histories;
    }

    /**
     * Method used to save the histories to the application preferences.
     */
    public void historiesSave() {
        int i = 0;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        removeOldHistories(editor);
        editor.putInt("histories", histories.size());
        for (History history : histories) {
            editor.putLong(KEY_PREFIX_HISTORY + i + "_dateTime", history.getDateTime());
            editor.putString(KEY_PREFIX_HISTORY + i + "_cmd", history.getSmsCommand());
            editor.putString(KEY_PREFIX_HISTORY + i + "_number",
                    history.getPhoneNumber());
            i++;
        }
        editor.commit();
    }

    /**
     * Remove all histories from the SharedPreferences.
     *
     * @param editor The SharedPreferences editor.
     */
    private void removeOldHistories(SharedPreferences.Editor editor) {
        Set<String> keys = mSharedPreferences.getAll().keySet();
        for (String key : keys) {
            if (key.startsWith(KEY_PREFIX_HISTORY)) {
                editor.remove(key);
            }
        }
    }

    /**
     * Method used to load the histories from the application preferences.
     */
    public void historiesLoad() {
        int count = mSharedPreferences.getInt("histories", 0);
        int i = 0;
        if (histories.size() > 0) {
            histories.clear();
        }
        while (i < count) {
            histories.add(new History(mSharedPreferences.getLong(KEY_PREFIX_HISTORY + i
                    + "_dateTime", 0L), mSharedPreferences.getString(KEY_PREFIX_HISTORY
                    + i + "_number", ""), mSharedPreferences.getString(
                    KEY_PREFIX_HISTORY + i + "_cmd", "")));
            i++;
        }
    }

    /**
     * Method used to send a SMS message to provided phone number.
     *
     * @param context     The context used to send the SMS.
     * @param clazz       The sender class.
     * @param phoneNumber The phone number.
     * @param message     The message to be send.
     */
    public void sendSMS(Context context, Class<?> clazz, String phoneNumber,
                        String message) {
        addHistory(new History(phoneNumber, message));
        historiesSave();
        Log.i(TAG, "Send to: " + phoneNumber + " the SMS:\"" + message + "\"");
        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(
                context, clazz), 0);
        smsManager.sendTextMessage(phoneNumber, null, message, pi, null);
        saveMessageToSendFolder(phoneNumber, message);
    }

    /**
     * Save the message to the Send folder from Messaging application.
     *
     * @param phoneNumber The phone number were was send the message.
     * @param message     The message to be saved.
     */
    private void saveMessageToSendFolder(String phoneNumber, String message) {
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("body", message);
        try {
            if (sendFolderUri == null) {
                sendFolderUri = Uri.parse("content://sms/sent");
            }
            if (sendFolderUri != null) {
                this.getContentResolver().insert(sendFolderUri, values);
            }
        } catch (Exception e) {
            Log.e(TAG, "Save message to the send folder:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Method used to send a SMS message to all selected GPS contacts.
     *
     * @param context The context used to send the SMS.
     * @param clazz   The sender class.
     * @param message The message to be send.
     */
    public void sendSMS(Context context, Class<?> clazz, String message) {
        int i = 0;
        for (GpsContact contact : contacts) {
            String cmd = prepareCommandPassword(message, contact);
            if (contact.isSelected()) {
                sendSMS(context, clazz, contact.getPhone(), cmd);
                i++;
            }
        }
        if (i == 1) {
            showMessageInfo(context, R.string.sms_command_send_one);
        } else if (i > 0) {
            showMessageInfo(context, R.string.sms_command_send_many, "" + i);
        } else {
            showMessageInfo(context, R.string.sms_command_not_send);
        }
    }

    /**
     * Prepare the specific GPS password for the provided contact used on the
     * message.
     *
     * @param message The original message.
     * @param contact The contact used to obtain the GPS password.
     * @return The prepared message which include the GPS password.
     */
    private String prepareCommandPassword(String message, GpsContact contact) {
        String result = message.replaceAll("\\?password\\?",
                contact.getPassword());
        return result;
    }

    /**
     * Check if are selected GPS contacts into the list.
     *
     * @return True if is at least one selected GPS contact in the list.
     */
    public boolean haveContactsSelected() {
        boolean result = false;
        for (GpsContact contact : contacts) {
            if (contact.isSelected()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Add a GPS contact to the list of contacts.
     *
     * @param contact The contact to be added to the list.
     */
    public void addGpsContact(GpsContact contact) {
        contacts.add(contact);
    }

    /**
     * Obtain the whole GPS contact list.
     *
     * @return The GPS contact list.
     */
    public List<GpsContact> getContacts() {
        return contacts;
    }

    /**
     * Load the list of GPS contacts.
     */
    private void contactsLoad() {
        int count = mSharedPreferences.getInt("contacts", 0);
        int i = 0;
        if (contacts.size() > 0) {
            contacts.clear();
        }
        while (i < count) {
            contacts.add(new GpsContact(mSharedPreferences.getString("contact_"
                    + i + "_name", ""), mSharedPreferences.getString("contact_"
                    + i + "_phone", ""), mSharedPreferences.getString("contact_"
                    + i + "_password", ""), mSharedPreferences.getBoolean(
                    "contact_" + i + "_selected", false)));
            i++;
        }
        if (contacts.size() < 1) {
            String temp = getGPSPhoneNumber();
            String pass = mSharedPreferences.getString("password", "123456");
            if (temp.length() > 0) {
                contacts.add(new GpsContact(temp, temp, pass, true));
            }
        }
    }

    /**
     * Save the GPS contacts list
     */
    public void contactsSave() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("contacts", contacts.size());
        int i = 0;
        for (GpsContact contact : contacts) {
            editor.putString("contact_" + i + "_name", contact.getName());
            editor.putString("contact_" + i + "_phone", contact.getPhone());
            editor.putString("contact_" + i + "_password",
                    contact.getPassword());
            editor.putBoolean("contact_" + i + "_selected",
                    contact.isSelected());
            i++;
        }
        editor.remove("gpsPhoneNumber");
        editor.remove("password");
        editor.commit();
    }

    /**
     * Method used to show the informations.
     *
     * @param context           The context where should be displayed the message.
     * @param resourceMessageId The string resource id.
     */
    public void showMessageInfo(Context context, int resourceMessageId) {
        String message = getString(resourceMessageId);
        showMessageInfo(context, message);
    }

    /**
     * Method used to show formated informations.
     *
     * @param context           The context where should be displayed the message.
     * @param resourceMessageId The string resource id.
     * @param formatArgs        The arguments used on formated message.
     */
    public void showMessageInfo(Context context, int resourceMessageId,
                                Object... formatArgs) {
        String message = getString(resourceMessageId, formatArgs);
        showMessageInfo(context, message);
    }

    /**
     * This method is used to show on front of a context a toast message.
     *
     * @param context The context where should be showed the message.
     * @param message The message used to be displayed on the information box.
     */
    public void showMessageInfo(Context context, String message) {
        if (message != null && message.length() > 0) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Method used to show the errors.
     *
     * @param context           The context where should be displayed the error message.
     * @param resourceMessageId The string resource id.
     */
    public void showMessageError(Context context, int resourceMessageId) {
        String message = getString(resourceMessageId);
        showMessageError(context, message);
    }

    /**
     * Method used to show error formated messages.
     *
     * @param context           The context where should be displayed the error message.
     * @param resourceMessageId The string resource id.
     * @param formatArgs        The arguments used on formated message.
     */
    public void showMessageError(Context context, int resourceMessageId,
                                 Object... formatArgs) {
        String message = getString(resourceMessageId, formatArgs);
        showMessageError(context, message);
    }

    /**
     * This method is used to show on front of a context a toast message
     * containing applications errors.
     *
     * @param context The context where should be showed the message.
     * @param message The error message used to be displayed on the information box.
     */
    public void showMessageError(Context context, String message) {
        if (message != null && message.length() > 0) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Obtain the contact choose handler.
     *
     * @return The contact choose handler.
     */
    public ContactChooseHandler getContactChooseHandler() {
        return contactChooseHandler;
    }

    /**
     * Set the contact choose handler.
     *
     * @param contactChooseHandler The contact choose handler.
     */
    public void setContactChooseHandler(
            ContactChooseHandler contactChooseHandler) {
        this.contactChooseHandler = contactChooseHandler;
    }

    /**
     * List phone contacts.
     *
     * @return the phoneContacts
     */
    public List<ContactModel> getPhoneContacts() {
        return phoneContacts;
    }

    /**
     * Save list phone contacts for future use on current application session.
     *
     * @param phoneContacts the phoneContacts to set
     */
    public void setPhoneContacts(List<ContactModel> phoneContacts) {
        this.phoneContacts = phoneContacts;
    }

    /**
     * Obtain the application shared preferences.
     *
     * @return The application shared preferences.
     */
    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    /**
     * Retrieve default backup path for exported application preferences file.
     *
     * @return Default backup path.
     */
    public String getDefaultBackupPath() {
        if (defaultBackupPath == null) {
            File defaultDir = Environment.getExternalStorageDirectory();
            if (defaultDir != null && defaultDir.exists()) {
                try {
                    defaultBackupPath = defaultDir.getCanonicalPath()
                            + File.pathSeparator;
                } catch (IOException e) {
                    defaultBackupPath = "";
                }
            } else {
                defaultBackupPath = "";
            }
        }
        return defaultBackupPath;
    }

    /**
     * Retrieve the importing path for the exported application preferences
     * file.
     *
     * @return Importing backup path.
     */
    public String getBackupPath() {
        String state = Environment.getExternalStorageState();
        String defaultPath = "";
        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            defaultPath += Environment.getExternalStorageDirectory().getPath()
                    + File.separator;
        } else {
            defaultPath += getString(R.string.default_backup_dir);
        }
        defaultPath += getString(R.string.default_backup_file);
        return mSharedPreferences.getString("backupPath", defaultPath);
    }

    /**
     * Store the backup path for exporting or importing application preferences
     * file.
     *
     * @param backupPath Path used for exporting or importing application preferences
     *                   file.
     */
    public void setBackupPath(String backupPath) {
        Editor editor = mSharedPreferences.edit();
        editor.putString("backupPath", backupPath);
        editor.commit();
    }

    /**
     * Check for pro version.
     *
     * @return True if pro version exist.
     */
    public boolean isProPresent() {
        PackageManager pm = getPackageManager();
        boolean success = false;
        try {
            success = (PackageManager.SIGNATURE_MATCH == pm.checkSignatures(
                    this.getPackageName(), "ro.ciubex.tkconfigpro"));
            Log.d(TAG, "isProPresent: " + success);
        } catch (Exception e) {
            Log.e(TAG, "isProPresent: " + e.getMessage(), e);
        }
        return success;
    }

    /**
     * Store a boolean value on the shared preferences.
     *
     * @param key   The shared preference key.
     * @param value The boolean value to be saved.
     */
    private void saveBooleanValue(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Remove a shared preference.
     *
     * @param key The key of the shared preference to be removed.
     */
    private void removeSharedPreference(String key) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * Check if should be asked for permissions.
     *
     * @return True if should be asked for permissions.
     */
    public boolean shouldAskPermissions() {
        return mSdkInt > 22;
    }

    /**
     * Check if the permissions were asked.
     *
     * @return True if the permissions were asked.
     */
    public boolean havePermissionsAsked() {
        return mSharedPreferences.getBoolean(KEY_HAVE_PERMISSIONS_ASKED, false);
    }

    /**
     * Set the permission asked flag to true.
     */
    public void markPermissionsAsked() {
        saveBooleanValue(KEY_HAVE_PERMISSIONS_ASKED, true);
    }

    /**
     * Check if a permission was asked.
     *
     * @param permission The permission to be asked.
     * @return True if the permission was asked before.
     */
    public boolean isPermissionAsked(String permission) {
        return mSharedPreferences.getBoolean(permission, false);
    }

    /**
     * Mark a permission as asked.
     *
     * @param permission Permission to be marked as asked.
     */
    public void markPermissionAsked(String permission) {
        saveBooleanValue(permission, true);
    }

    /**
     * Remove the permission asked flag.
     *
     * @param permission The permission for which will be removed the asked flag.
     */
    public void removePermissionAskedMark(String permission) {
        removeSharedPreference(permission);
    }

    /**
     * Check if a permission is allowed.
     *
     * @param permission The permission to be checked.
     * @return True if the permission is allowed.
     */
    public boolean hasPermission(String permission) {
        if (shouldAskPermissions()) {
            return hasPermission23(permission);
        }
        return true;
    }

    /**
     * Check if a permission is allowed. (API 23)
     *
     * @param permission The permission to be checked.
     * @return True if the permission is allowed.
     */
    @TargetApi(23)
    private boolean hasPermission23(String permission) {
        return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Check if the application have functional permissions.
     *
     * @return True if all functional permissions are allowed.
     */
    public boolean haveFunctionalPermissions() {
        for (String permission : TKConfigApplication.FUNCTIONAL_PERMISSIONS) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get all not granted permissions.
     */
    public String[] getNotGrantedPermissions() {
        List<String> permissions = new ArrayList<>();
        buildRequiredPermissions(permissions, TKConfigApplication.FUNCTIONAL_PERMISSIONS, true);
        String[] array = null;
        if (!permissions.isEmpty()) {
            array = new String[permissions.size()];
            array = permissions.toArray(array);
        }
        return array;
    }

    /**
     * Get an array with all required permissions.
     *
     * @return Array with permissions to be requested.
     */
    public String[] getAllRequiredPermissions() {
        List<String> permissions = new ArrayList<>();
        buildRequiredPermissions(permissions, TKConfigApplication.FUNCTIONAL_PERMISSIONS, false);
        String[] array = null;
        if (!permissions.isEmpty()) {
            array = new String[permissions.size()];
            array = permissions.toArray(array);
        }
        return array;
    }

    /**
     * Put on the permissions all required permissions which is missing and was not asked.
     *
     * @param permissions         List of permissions to be requested.
     * @param requiredPermissions List with all required permissions to be checked.
     */
    private void buildRequiredPermissions(List<String> permissions, List<String> requiredPermissions, boolean force) {
        for (String permission : requiredPermissions) {
            if ((force && !hasPermission(permission)) ||
                    (!isPermissionAsked(permission) && !hasPermission(permission))) {
                permissions.add(permission);
            }
        }
    }

    /**
     * Get the application theme.
     *
     * @return The application theme.
     */
    public int getApplicationTheme() {
        String theme = mSharedPreferences.getString(KEY_APP_THEME, "dark");
        if ("dark".equals(theme)) {
            return R.style.AppThemeDark;
        }
        return R.style.AppThemeLight;
    }

    /**
     * Set the must restart flag.
     *
     * @param mustRestart The value to be set.
     */
    public void setMustRestart(boolean mustRestart) {
        this.mMustRestart = mustRestart;
    }

    /**
     * Check the must restart flag state.
     *
     * @return The must restart flag state.
     */
    public boolean isMustRestart() {
        return mMustRestart;
    }
}
