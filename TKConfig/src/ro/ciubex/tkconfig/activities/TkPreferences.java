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
package ro.ciubex.tkconfig.activities;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.TKConfigApplication;
import ro.ciubex.tkconfig.models.Constants;
import ro.ciubex.tkconfig.tasks.DefaultAsyncTaskResult;
import ro.ciubex.tkconfig.tasks.PreferencesFileUtilAsynkTask;
import ro.ciubex.tkconfig.forms.CustomEditTextPreference;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * This is the main preference activity class.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class TkPreferences extends PreferenceActivity implements
		CustomEditTextPreference.Listener,
		PreferencesFileUtilAsynkTask.Responder {
	private TKConfigApplication application;
	private CustomEditTextPreference preferencesBackup;
	private CustomEditTextPreference preferencesRestore;
	private static final int PREF_BACKUP = 1;
	private static final int PREF_RESTORE = 2;

	/**
	 * Method called when this preference activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (TKConfigApplication) getApplication();
		addPreferencesFromResource(R.xml.tk_preferences);
		prepareCommands();
		prepareAllCustomEditTextPreference();
	}

	/**
	 * Prepare preference handler
	 */
	private void prepareCommands() {
		Preference preferencesGpsContacts = (Preference) findPreference("gpsContacts");
		preferencesGpsContacts
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						return onShowGPSContacts();
					}
				});
		Preference preferencesReset = (Preference) findPreference("resetCommands");
		preferencesReset
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						return onCommandsReset();
					}
				});
	}

	/**
	 * Prepare some custom preferences to not be stored. The preference backup
	 * and restore are actually used as buttons.
	 */
	private void prepareAllCustomEditTextPreference() {
		String backupPath = getBackupPath();
		preferencesBackup = (CustomEditTextPreference) findPreference("preferencesBackup");
		preferencesBackup.setResultListener(this, PREF_BACKUP);
		preferencesBackup.setText(backupPath);
		preferencesRestore = (CustomEditTextPreference) findPreference("preferencesRestore");
		preferencesRestore.setResultListener(this, PREF_RESTORE);
		preferencesRestore.setText(backupPath);
	}

	/**
	 * Show the GPS contact list.
	 * 
	 * @return Always will be returned TRUE.
	 */
	private boolean onShowGPSContacts() {
		Intent intent = new Intent(getBaseContext(), GpsContactActivity.class);
		startActivityForResult(intent, 1);
		return true;
	}

	/**
	 * Method invoked when is started PreferencesFileUtilAsynkTask task
	 * 
	 * @param operationType
	 *            The operation type: backup or restore
	 */
	@Override
	public void startFileAsynkTask(
			PreferencesFileUtilAsynkTask.Operation operationType) {
		if (operationType == PreferencesFileUtilAsynkTask.Operation.RESTORE) {
			application.showProgressDialog(this, R.string.backup_started);
		} else {
			application.showProgressDialog(this, R.string.restore_started);
		}
	}

	/**
	 * Method invoked when is ended PreferencesFileUtilAsynkTask task
	 * 
	 * @param operationType
	 *            The operation type: backup or restore
	 * @param result
	 *            The process result
	 */
	@Override
	public void endFileAsynkTask(
			PreferencesFileUtilAsynkTask.Operation operationType,
			DefaultAsyncTaskResult result) {
		application.hideProgressDialog();
		if (result.resultId == Constants.OK) {
			application.showMessageInfo(this, result.resultMessage);
			if (operationType == PreferencesFileUtilAsynkTask.Operation.RESTORE) {
				// restartPreferencesActivity();
			}
		} else {
			application.showMessageError(this, result.resultMessage);
		}
	}

	/**
	 * Method invoked when is pressed the positive (OK) button from a preference
	 * edit dialog
	 * 
	 * @param resultId
	 *            The id of pressed preference: PREF_RESTORE or PREF_BACKUP
	 * @param value
	 *            The full file and path of saved or loaded preferences
	 */
	@Override
	public void onPositiveResult(int resultId, String value) {
		if (resultId > 0) {
			storeBackupPath(value);
		}
		if (resultId == PREF_RESTORE) {
			preferencesBackup.setText(value);
			onRestorePreferences(value);
		} else if (resultId == PREF_BACKUP) {
			preferencesRestore.setText(value);
			onBackupPreferences(value);
		}
	}

	/**
	 * Persist into preferences the full file and path of saved or loaded
	 * preferences
	 * 
	 * @param backupPath
	 *            The full file and path of saved or loaded preferences
	 */
	private void storeBackupPath(String backupPath) {
		application.setBackupPath(backupPath);
	}

	/**
	 * Obtain the full file and path of saved or loaded preferences
	 * 
	 * @return The full file and path of saved or loaded preferences
	 */
	private String getBackupPath() {
		return application.getBackupPath();
	}

	/**
	 * Method invoked when is pressed the negative (Cancel) button from a
	 * preference edit dialog
	 * 
	 * @param resultId
	 *            The id of pressed preference: PREF_RESTORE or PREF_BACKUP
	 * @param value
	 *            The full file and path of saved or loaded preferences
	 */
	@Override
	public void onNegativeResult(int resultId, String value) {

	}

	/**
	 * Method invoked when is pressed the restore preference This will launch a
	 * PreferencesFileUtilAsynkTask task to restore preferences
	 * 
	 * @param backupPath
	 *            The full file and path from where should be loaded preferences
	 */
	private void onRestorePreferences(String backupPath) {
		new PreferencesFileUtilAsynkTask(this, backupPath,
				PreferencesFileUtilAsynkTask.Operation.RESTORE).execute();
	}

	/**
	 * Method invoked when is pressed the back-up preference This will launch
	 * PreferencesFileUtilAsynkTask task to backup preferences
	 * 
	 * @param backupPath
	 *            The full file and path where should be stored preferences
	 */
	private void onBackupPreferences(String backupPath) {
		new PreferencesFileUtilAsynkTask(this, backupPath,
				PreferencesFileUtilAsynkTask.Operation.BACKUP).execute();
	}

	/**
	 * Show a reset command question dialog window.
	 * 
	 * @return Always will be returned TRUE.
	 */
	private boolean onCommandsReset() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.reset_commands_question)
				.setMessage(R.string.reset_commands_question_desc)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								doCommandsReset();
							}
						}).setNegativeButton(R.string.no, null).show();
		return true;
	}

	/**
	 * This method is used to reset the command list to default commands.
	 */
	private void doCommandsReset() {
		application.populateDefaultCommands();
		application.commandsStoreCleanup();
		application.setMustReloadCommands(true);
	}
}
