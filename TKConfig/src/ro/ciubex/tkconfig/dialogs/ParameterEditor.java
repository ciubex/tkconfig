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
import ro.ciubex.tkconfig.models.Command;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

/**
 * This dialog is used to edit a parameter.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class ParameterEditor extends BaseDialog {
	private Command command;
	private String parameterName;
	private EditText editParameter;

	public ParameterEditor(Context context, Command command,
			int parameterPosition) {
		super(context);
		this.command = command;
		this.parameterName = command.getParameterName(parameterPosition);
		prepareTitle();
		initEditTextFields();
	}

	/**
	 * Prepare dialog title.
	 */
	private void prepareTitle() {
		if (parameterName != null) {
			initDialog(R.layout.parameter_editor, application.getString(
					R.string.edit_parameter_with_name, parameterName));
		} else {
			initDialog(R.layout.parameter_editor, R.string.edit_parameter);
		}
	}

	/**
	 * This method is used to populate the edit text fields with the entity key
	 * and content.
	 */
	@Override
	protected void initEditTextFields() {
		editParameter = (EditText) findViewById(R.id.edit_parameter);
		listEditText.add(editParameter);
		if (command != null) {
			String temp = command.getParameterValue(parameterName);
			editParameter.setHint(parameterName);
			if (temp != null) {
				editParameter.setText(temp);
			}
		}
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
		if (view == btnOk) {
			onSave();
		}
		super.onClick(view);
	}

	/**
	 * Save edited text to the entity.
	 */
	private void onSave() {
		String paramValue = editParameter.getText().toString();
		if (paramValue != null && paramValue.length() > 0) {
			String oldValue = command.getParameterValue(parameterName);
			if (!paramValue.equals(oldValue)) {
				command.setParameterValue(parameterName, paramValue);
				command.setParametersModified(true);
			}
		}
	}

}
