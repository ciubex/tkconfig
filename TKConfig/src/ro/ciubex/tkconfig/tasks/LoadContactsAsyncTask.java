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
package ro.ciubex.tkconfig.tasks;

import java.util.Collections;
import java.util.List;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.models.Constants;
import ro.ciubex.tkconfig.models.ContactModel;
import ro.ciubex.tkconfig.models.ContactsComparator;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

/**
 * This is an AsyncTask used to load all contacts from the phone.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class LoadContactsAsyncTask extends
		AsyncTask<Void, Void, DefaultAsyncTaskResult> {

	/**
	 * Responder used on loading process.
	 */
	public interface Responder {
		public Application getApplication();

		public void startLoadContacts();

		public void endLoadContacts(DefaultAsyncTaskResult result);
	}

	private List<ContactModel> contacts;
	private Responder responder;

	public LoadContactsAsyncTask(Responder responder,
			List<ContactModel> contacts) {
		this.responder = responder;
		this.contacts = contacts;
	}

	/**
	 * Method invoked on the background thread.
	 */
	@Override
	protected DefaultAsyncTaskResult doInBackground(Void... params) {
		DefaultAsyncTaskResult result = new DefaultAsyncTaskResult();
		getContacts(result);
		return result;
	}

	/**
	 * Method invoked on the UI thread before the task is executed.
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.startLoadContacts();
	}

	/**
	 * Method invoked on the UI thread after the background computation
	 * finishes.
	 */
	@Override
	protected void onPostExecute(DefaultAsyncTaskResult result) {
		super.onPostExecute(result);
		responder.endLoadContacts(result);
	}

	/**
	 * This method is used to load all contacts from the phone.
	 * 
	 * @param result
	 *            The process result.
	 */
	private void getContacts(DefaultAsyncTaskResult result) {
		result.resultId = Constants.OK;
		if (contacts.size() > 0) {
			contacts.clear();
		}
		Application app = responder.getApplication();
		Cursor cursor = null;
		try {
			ContentResolver cr = app.getContentResolver();

			String[] columns = new String[] {
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
					ContactsContract.CommonDataKinds.Phone.NUMBER,
					ContactsContract.Contacts.HAS_PHONE_NUMBER };

			String where = ContactsContract.Contacts.IN_VISIBLE_GROUP
					+ " = '1'";

			cursor = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					columns, where, null, null);
			if (cursor != null) {
				long contactId;
				String contactName, contactPhone;
				while (cursor.moveToNext()) {
					contactName = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
					contactPhone = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					if (contactName != null && contactName.length() > 0) {
						contactId = cursor
								.getLong(cursor
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

						ContactModel contact = new ContactModel();
						contact.setId(contactId);
						contact.setContactName(contactName);
						contact.setPhoneNumber(contactPhone);
						contacts.add(contact);
					}
				}
			}
			String resultMessage = app.getString(R.string.no_phone_contacts);
			if (contacts.size() > 0) {
				resultMessage = app.getString(R.string.contacts_loaded,
						contacts.size());
			} else {
				result.resultId = Constants.ERROR;
			}
			result.resultMessage = resultMessage;
		} catch (Exception e) {
			e.printStackTrace();
			result.resultId = Constants.ERROR;
			result.resultMessage = e.getMessage();
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		Collections.sort(contacts, new ContactsComparator());
	}

}
