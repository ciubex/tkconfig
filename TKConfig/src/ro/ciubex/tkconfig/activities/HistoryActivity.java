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
import ro.ciubex.tkconfig.list.HistoryListAdapter;
import ro.ciubex.tkconfig.models.History;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * @author Claudiu Ciobotariu
 * 
 */
public class HistoryActivity extends BaseActivity {
	private HistoryListAdapter adapter;
	private ListView historiesList;

	private final int CONFIRM_ID_RESEND = 0;
	private final int CONFIRM_ID_DELETE = 1;

	/**
	 * The method invoked when the activity is creating
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_list_layout);
		setMenuId(R.menu.history_menu);
		prepareHistoryListView();
	}

	/**
	 * Method invoked when the activity is resumed.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		app.showProgressDialog(this, R.string.please_wait);
		reloadAdapter();
	}

	/**
	 * Method used to initialize the history list view.
	 */
	private void prepareHistoryListView() {
		historiesList = (ListView) findViewById(R.id.history_list);
		historiesList.setEmptyView(findViewById(R.id.no_history));
		historiesList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position > -1 && position < adapter.getCount()) {
					showItemDialogMenu(position);
				}
			}
		});
		adapter = new HistoryListAdapter(app);
		historiesList.setAdapter(adapter);
	}

	/**
	 * Reload adapter and histories list.
	 */
	public void reloadAdapter() {
		adapter.notifyDataSetChanged();
//		historiesList.invalidateViews();
//		historiesList.scrollBy(0, 0);
		historiesList.setFastScrollEnabled(app.getHistories().size() > 50);
		app.hideProgressDialog();
	}

	/**
	 * Prepare Option menu
	 */
	@Override
	protected boolean onMenuItemSelected(int menuItemId) {
		boolean processed = false;
		switch (menuItemId) {
		case R.id.menu_back:
			processed = true;
			goBack();
			break;
		}
		return processed;
	}

	/**
	 * This method show the pop up menu when the user do a long click on a list
	 * item.
	 * 
	 * @param contactPosition
	 *            The contact position where was made the long click
	 */
	private void showItemDialogMenu(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.item_edit);
		builder.setItems(R.array.history_menu_list,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							onMenuItemResendSMS(position);
							break;
						case 1:
							onMenuItemDelete(position);
							break;
						}
					}
				});
		builder.create().show();
	}

	/**
	 * This method is invoked when the user chose to delete an history item.
	 * 
	 * @param position
	 *            The position of history item to be deleted.
	 */
	private void onMenuItemDelete(int position) {
		final History history = (History) adapter.getItem(position);
		if (history != null) {
			showConfirmationDialog(
					R.string.remove_history,
					app.getString(R.string.remove_history_question,
							history.getSmsCommand()), CONFIRM_ID_DELETE,
					history);
		}
	}

	/**
	 * This method is invoked when the user chose to resend an history item.
	 * 
	 * @param position
	 *            The position of history item to be resend.
	 */
	private void onMenuItemResendSMS(int position) {
		final History history = (History) adapter.getItem(position);
		if (history != null) {
			showConfirmationDialog(
					R.string.resend_command,
					app.getString(R.string.resend_command_question,
							history.getSmsCommand()), CONFIRM_ID_RESEND,
					history);
		}
	}

	/**
	 * This method is invoked by the each time when is accepted a confirmation
	 * dialog.
	 * 
	 * @param positive
	 *            True if the confirmation is positive.
	 * @param confirmationId
	 *            The confirmation ID to identify the case.
	 * @param anObject
	 *            An object send by the caller method.
	 */
	@Override
	protected void onConfirmation(boolean positive, int confirmationId,
			Object anObject) {
		if (positive) {
			switch (confirmationId) {
			case CONFIRM_ID_RESEND:
				doResendSMS((History) anObject);
				break;
			case CONFIRM_ID_DELETE:
				doDeleteHistory((History) anObject);
				break;
			}
		}
	}

	/**
	 * Remove the history from the list.
	 * 
	 * @param history
	 *            History to be removed.
	 */
	private void doDeleteHistory(History history) {
		app.showProgressDialog(this, R.string.please_wait);
		app.getHistories().remove(history);
		app.historiesSave();
		reloadAdapter();
	}

	/**
	 * Resend a SMS command from the history.
	 * 
	 * @param history
	 *            History with the command to be resend.
	 */
	private void doResendSMS(History history) {
		app.showProgressDialog(this, R.string.please_wait);
		app.sendSMS(this, HistoryActivity.class, history.getPhoneNumber(),
				history.getSmsCommand());
		app.showMessageInfo(this, R.string.resend_command_finish);
		reloadAdapter();
	}
}
