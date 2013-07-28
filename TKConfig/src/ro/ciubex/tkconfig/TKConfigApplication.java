/**
 * This file is part of TKConfig application.
 * 
 * Copyright (C) 2013 Claudiu Ciobotariu
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ro.ciubex.tkconfig.models.Command;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This is main application class. Here are defined the progress dialog and
 * information popup.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class TKConfigApplication extends Application {
	private ProgressDialog progressDialog;
	private List<Command> commands;
	private Locale defaultLocale;
	private SharedPreferences sharedPreferences;
	private boolean mustReloadCommands;

	/**
	 * This method is invoked when the application is created.
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		commands = new ArrayList<Command>();
		defaultLocale = Locale.getDefault();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
				"stockade?password? ?latitude1?,?longitude1?; ?latitude2?,?longitude2?",
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
				"Send SMS adminip+123456+space+IP address+space+port. If setup successful, the tracker will reply SMS \"adminip ok\"."));
		commands.add(new Command("Cancel GPRS", "noadminip?password?",
				"Send SMS noadminip+123456"));
		commands.add(new Command(
				"Set APN",
				"apn?password? ?apn-name?",
				"Send SMS apn+123456+space+specify apn. If setup successful, the tracker will reply SMS \"APN ok\"."));
	}

	/**
	 * This method should be used to prepare the initial parameters of specified
	 * command. The parameters are taken from the application preferences.
	 * 
	 * @param command
	 *            The command to be prepared.
	 */
	public void prepareCommandParameters(Command command) {
		if (command.hasParameters()) {
			String parameterValue;
			for (String parameterName : command.getParameters()) {
				parameterValue = sharedPreferences.getString(parameterName,
						parameterName);
				command.setParameterValue(parameterName, parameterValue);
			}
		}
	}

	/**
	 * This method is used to obtain the GPS tracker phone number.
	 * 
	 * @return The GPS tracker phone number.
	 */
	public String getGPSPhoneNumber() {
		return sharedPreferences.getString("gpsPhoneNumber", "0123456789");
	}

	/**
	 * Save the command parameters value to the application preferences.
	 * 
	 * @param command
	 *            The command which parameters should to be saved to the
	 *            application preferences.
	 */
	public void saveCommandParameters(Command command) {
		if (command.hasParameters()) {
			String parameterValue;
			SharedPreferences.Editor editor = sharedPreferences.edit();
			for (String parameterName : command.getParameters()) {
				parameterValue = command.getParameterValue(parameterName);
				editor.putString(parameterName, parameterValue);
			}
			editor.commit();
		}
	}

	/**
	 * Method used when the application should be closed.
	 */
	public void onClose() {
		hideProgressDialog();
		commandsSave();
	}

	/**
	 * Method used to save the commands to the application preferences.
	 */
	private void commandsSave() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
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
		int count = sharedPreferences.getInt("commands", 0);
		int i = 0;
		if (commands.size() > 0) {
			commands.clear();
		}
		while (i < count) {
			commands.add(new Command(sharedPreferences.getString("command_" + i
					+ "_name", ""), sharedPreferences.getString("command_" + i
					+ "_cmd", ""), sharedPreferences.getString("command_" + i
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
		int count = sharedPreferences.getInt("commands", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
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
	 * @param mustReloadCommands
	 *            Boolean flag to be set to the reload command list adapter
	 *            flag.
	 */
	public void setMustReloadCommands(boolean mustReloadCommands) {
		this.mustReloadCommands = mustReloadCommands;
	}

	/**
	 * This will show a progress dialog using a context and a message ID from
	 * application string resources.
	 * 
	 * @param context
	 *            The context where should be displayed the progress dialog.
	 * @param messageId
	 *            The string resource id.
	 */
	public void showProgressDialog(Context context, int messageId) {
		showProgressDialog(context, getString(messageId));
	}

	/**
	 * This will show a progress dialog using a context and the message to be
	 * showed on the progress dialog.
	 * 
	 * @param context
	 *            The context where should be displayed the progress dialog.
	 * @param message
	 *            The message displayed inside of progress dialog.
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
}
