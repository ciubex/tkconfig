/**
 * This file is part of TKConfig application.
 * 
 * Copyright (C) 2016 Claudiu Ciobotariu
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
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Here are defined default Activity behavior.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class BaseActivity extends AppCompatActivity {
	protected TKConfigApplication mApplication;
	protected int menuId;
	private boolean showMenu;

	/**
	 * The method invoked when the activity is creating
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mApplication = (TKConfigApplication) getApplication();
		applyApplicationTheme();
		super.onCreate(savedInstanceState);
	}

	/**
	 * Apply application theme.
	 */
	protected void applyApplicationTheme() {
		this.setTheme(mApplication.getApplicationTheme());
	}

	/**
	 * Override default menu, with another menu
	 * 
	 * @param menuId
	 *            Menu resource ID
	 */
	protected void setMenuId(int menuId) {
		this.menuId = menuId;
		showMenu = true;
	}

	/**
	 * Create option menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean processed = false;
		if (showMenu) {
			MenuInflater inflater = getMenuInflater();
			if (inflater != null) {
				inflater.inflate(menuId, menu);
				if (mApplication.isProPresent()) {
					MenuItem item_donate = menu.findItem(R.id.menu_donate);
					if (item_donate != null && item_donate.isVisible()) {
						item_donate.setVisible(false);
					}
				}
			}
			processed = true;
		}
		return processed;
	}

	/**
	 * Method invoked on exit
	 */
	protected void onExit() {
		mApplication.onClose();
		finish();
	}

	/**
	 * Method invoked on back
	 */
	protected void goBack() {
		onExit();
	}

	/**
	 * Invoked when the activity is put on pause
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	/**
	 * On this method is a confirmation dialog.
	 * 
	 * @param titleStringId
	 *            The resource string id used for the confirmation dialog title.
	 * @param message
	 *            The message used for the confirmation dialog text.
	 * @param confirmationId
	 *            The id used to be identified the confirmed case.
	 * @param anObject
	 *            This could be used to send from the object needed on the
	 *            confirmation.
	 */
	protected void showConfirmationDialog(int titleStringId, String message,
			final int confirmationId, final Object anObject) {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(titleStringId)
				.setMessage(message)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								onConfirmation(true, confirmationId, anObject);
							}

						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								onConfirmation(false, confirmationId, anObject);
							}

						}).show();
	}

	/**
	 * This method should overwrite on each activity to handle confirmations
	 * cases.
	 * 
	 * @param positive
	 *            True if the confirmation is positive.
	 * @param confirmationId
	 *            The confirmation ID to identify the case.
	 * @param anObject
	 *            An object send by the caller method.
	 */
	protected void onConfirmation(boolean positive, int confirmationId,
			Object anObject) {

	}

	/**
	 * Invoked when an item from option menu is selected Send the menu ID to be
	 * processed.
	 * 
	 * @param item
	 *            The selected menu item
	 * @return A boolean value. True if the item is processed by this activity
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return onMenuItemSelected(item.getItemId());
	}

	/**
	 * A default menu option consumer
	 * 
	 * @param menuItemId
	 *            The menu item ID to be processed
	 * @return A boolean value. True if the item is processed by this activity
	 */
	protected boolean onMenuItemSelected(int menuItemId) {
		return false;
	}

	/**
	 * Method invoked when the used click on the OK dialog message.
	 * 
	 * @param messageId
	 *            The ID of the message to be identified on the caller activity.
	 * @param anObject
	 *            The object used by the caller activity.
	 */
	protected void onMessageOk(int messageId, Object anObject) {
	}

	/**
	 * This method should be used to show a dialog message to the user.
	 * 
	 * @param titleStringId
	 *            The resource string id used for the confirmation dialog title.
	 * @param message
	 *            The message used for the confirmation dialog text.
	 * @param messageId
	 *            The ID of the message to be identified on the caller activity.
	 * @param anObject
	 *            The object used by the caller activity.
	 */
	protected void showMessageDialog(int titleStringId, String message,
			final int messageId, final Object anObject) {
		new AlertDialog.Builder(this)
		.setTitle(titleStringId)
		.setMessage(message)
		.setIcon(android.R.drawable.ic_dialog_info)
		.setNeutralButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int whichButton) {
						onMessageOk(messageId, anObject);
					}
				}).show();
	}
}
