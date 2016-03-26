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

import java.util.ArrayList;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.list.ContactListAdapter;
import ro.ciubex.tkconfig.list.ContactListView;
import ro.ciubex.tkconfig.models.Constants;
import ro.ciubex.tkconfig.models.ContactChooseHandler;
import ro.ciubex.tkconfig.models.ContactModel;
import ro.ciubex.tkconfig.tasks.DefaultAsyncTaskResult;
import ro.ciubex.tkconfig.tasks.LoadContactImageAsyncTask;
import ro.ciubex.tkconfig.tasks.LoadContactsAsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

/**
 * @author Claudiu Ciobotariu
 * 
 */
public class ContactsActivity extends BaseActivity implements
		LoadContactsAsyncTask.Responder, LoadContactImageAsyncTask.Responder {

	private EditText contactsFilterBox;
	private ListView contactsListView = null;
	private ContactListAdapter adapter;

	/**
	 * The method invoked when the activity is creating
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list_layout);
		prepareContactFilterBox();
		prepareContactListView();
	}

	/**
	 * Method invoked when the activity is started
	 */
	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * Prepare contact filter box.
	 */
	private void prepareContactFilterBox() {
		contactsFilterBox = (EditText) findViewById(R.id.contacts_filter_box);
		contactsFilterBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				applyFilter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * This method is invoked when the filter is edited
	 * 
	 * @param charSequence
	 *            The char sequence from the filter
	 */
	private void applyFilter(CharSequence charSequence) {
		mApplication.showProgressDialog(this, R.string.filtering);
		adapter.getFilter().filter(charSequence);
		mApplication.hideProgressDialog();
	}

	/**
	 * Prepare the contact list view.
	 */
	private void prepareContactListView() {
		contactsListView = (ContactListView) findViewById(R.id.contacts_list);
		contactsListView.setEmptyView(findViewById(R.id.emptyListView));
		contactsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				doContactChoose(position);
			}
		});
		if (mApplication.getPhoneContacts() == null ||
				mApplication.getPhoneContacts().isEmpty()) {
			mApplication.setPhoneContacts(new ArrayList<ContactModel>());
			loadContactListView();
		} else {
			preparePhoneContactsList();
		}
	}
	
	private void doContactChoose(int position) {
		if (adapter != null && position > -1
				&& position < adapter.getCount()) {
			ContactModel contact = adapter.getItem(position);
			ContactChooseHandler contactHandler = mApplication
					.getContactChooseHandler();
			if (contactHandler != null) {
				contactHandler.onContactChoose(contact);
				finish();
			}
		}
	}

	/**
	 * Prepare thread used to load the contacts to the list view.
	 */
	private void loadContactListView() {
		new LoadContactsAsyncTask(this, mApplication.getPhoneContacts()).execute();
	}

	/**
	 * 
	 */
	@Override
	public void startLoadContacts() {
		mApplication.showProgressDialog(this, R.string.please_wait);
	}

	/**
	 * 
	 */
	@Override
	public void endLoadContacts(DefaultAsyncTaskResult result) {
		preparePhoneContactsList();
		if (Constants.OK == result.resultId) {
			mApplication.showMessageInfo(this, result.resultMessage);
			startLoadContactImageAsyncTask();
		} else {
			mApplication.hideProgressDialog();
			showMessageDialog(R.string.information, result.resultMessage, 0,
					null);
		}
	}
	
	private void preparePhoneContactsList() {
		adapter = new ContactListAdapter(this, mApplication.getPhoneContacts(), mApplication.getDefaultLocale());
		contactsListView.removeAllViewsInLayout();
		contactsListView.setAdapter(adapter);
		contactsListView.invalidateViews();
		contactsListView.scrollBy(0, 0);
		contactsListView.setFastScrollEnabled(adapter.getCount() > 50);
	}

	private void startLoadContactImageAsyncTask() {
		new LoadContactImageAsyncTask(this, mApplication.getPhoneContacts()).execute();
	}

	@Override
	public void startLoadPictures() {

	}

	/**
	 * Method invoked during the load contact thread. Used to update the
	 * interface.
	 */
	@Override
	public void updateItemView(long contactId) {
		if (contactId > -1 && adapter.getCount() > 0) {
			int start = contactsListView.getFirstVisiblePosition();
			int end = contactsListView.getLastVisiblePosition();
			ContactModel contact;
			int i;
			View view;
			for (i = start; i <= end; i++) {
				contact = (ContactModel) contactsListView.getItemAtPosition(i);
				if (contact != null && contact.getId() == contactId
						&& contact.havePicture() && !contact.isViewUpdated()) {
					view = contactsListView.getChildAt(i - start);
					adapter.getView(i, view, contactsListView);
					contact.setViewUpdated(true);
				}
			}
		}
	}

	@Override
	public void endLoadPictures(DefaultAsyncTaskResult result) {
		mApplication.hideProgressDialog();
	}

}
