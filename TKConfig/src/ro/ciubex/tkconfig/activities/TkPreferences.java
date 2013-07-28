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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * This is the main preference activity class.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class TkPreferences extends PreferenceActivity {
	private TKConfigApplication application;

	/**
	 * Method called when this preference activity is created
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (TKConfigApplication) getApplication();
		addPreferencesFromResource(R.xml.tk_preferences);
		prepareCommandsReset();
	}

	/**
	 * Prepare reset preference handler
	 */
	private void prepareCommandsReset() {
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
