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
package ro.ciubex.tkconfig.dialogs;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.TKConfigApplication;
import ro.ciubex.tkconfig.activities.ContactsActivity;
import ro.ciubex.tkconfig.activities.GpsContactActivity;
import ro.ciubex.tkconfig.models.ContactChooseHandler;
import ro.ciubex.tkconfig.models.ContactModel;
import ro.ciubex.tkconfig.models.GpsContact;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * This class is used to define a dialog for GPS contact editing.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class GpsContactEditor extends BaseDialog implements
		ContactChooseHandler {
	private TKConfigApplication parentApplication;
	private GpsContact contact;
	private EditText contactName, contactPhone, contactPassword;
	private ImageButton doContactPicker;
	private CheckBox contactSelected;

	public GpsContactEditor(Context context,
			TKConfigApplication parentApplication, int titleId,
			GpsContact contact) {
		super(context);
		this.parentApplication = parentApplication;
		this.contact = contact;
		initDialog(R.layout.gps_contact_editor, titleId);
		initEditTextFields();
		initContactChooseHandler();
	}

	/**
	 * Initialize the contact choose handler.
	 */
	private void initContactChooseHandler() {
		parentApplication.setContactChooseHandler(this);
	}

	/**
	 * This method is used to populate the edit text fields with the entity key
	 * and content.
	 */
	@Override
	protected void initEditTextFields() {
		contactName = (EditText) findViewById(R.id.contact_name);
		contactPhone = (EditText) findViewById(R.id.contact_number);
		contactPassword = (EditText) findViewById(R.id.contact_passwd);
		contactSelected = (CheckBox) findViewById(R.id.contact_selected);
		listEditText.add(contactName);
		listEditText.add(contactPhone);
		listEditText.add(contactPassword);
		if (contact != null) {
			contactName.setText(contact.getName());
			contactPhone.setText(contact.getPhone());
			contactPassword.setText(contact.getPassword());
			contactSelected.setChecked(contact.isSelected());
		}
		doContactPicker = (ImageButton) findViewById(R.id.do_contact_picker);
		doContactPicker.setOnClickListener(this);
		super.initEditTextFields();
	}

	/**
	 * Called when a view has been clicked.
	 * 
	 * @param view
	 *            The view that was clicked.
	 */
	@Override
	public void onClick(View view) {
		if (view == doContactPicker) {
			onContactPicker();
		} else if (view == btnOk) {
			onSave();
			super.onClick(view);
		} else {
			super.onClick(view);
		}
	}

	/**
	 * Save edited texts to the contact entity.
	 */
	private void onSave() {
		String cName = contactName.getText().toString();
		String cPhone = contactPhone.getText().toString();
		String cPasswd = contactPassword.getText().toString();
		if (cName != null && cPhone != null && cName.length() > 0
				&& cPhone.length() > 0) {
			if (contact == null) {
				contact = new GpsContact(cName, cPhone, cPasswd,
						contactSelected.isChecked());
				((TKConfigApplication) application).addGpsContact(contact);
			} else {
				contact.setName(cName);
				contact.setPhone(cPhone);
				contact.setPassword(cPasswd);
				contact.setSelected(contactSelected.isChecked());
			}
			((GpsContactActivity) parentActivity).reloadAdapter();
			((TKConfigApplication) application).contactsSave();
		}
	}

	/**
	 * Is time to launch the contact list view.
	 */
	private void onContactPicker() {
		Intent intentContactsActivity = new Intent(parentActivity, ContactsActivity.class);
		parentActivity.startActivity(intentContactsActivity);
	}

	@Override
	public void onContactChoose(ContactModel contact) {
		if (contact != null) {
			contactName.setText(contact.getContactName());
			contactPhone.setText(contact.getPhoneNumber());
		}
	}
}
