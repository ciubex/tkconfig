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
import ro.ciubex.tkconfig.activities.TKConfigActivity;
import ro.ciubex.tkconfig.models.Command;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

/**
 * This class is used to define a dialog for command editing.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class EditorDialog extends BaseDialog {
	private Command command;
	private EditText cmdName, cmdCommand, cmdDescription;

	public EditorDialog(Context context, int titleId, Command command) {
		super(context);
		this.command = command;
		initDialog(R.layout.command_editor, titleId);
		initEditTextFields();
	}

	/**
	 * This method is used to populate the edit text fields with the entity key
	 * and content.
	 */
	@Override
	protected void initEditTextFields() {
		cmdName = (EditText) findViewById(R.id.command_name);
		cmdCommand = (EditText) findViewById(R.id.command_the);
		cmdDescription = (EditText) findViewById(R.id.command_description);
		listEditText.add(cmdName);
		listEditText.add(cmdCommand);
		listEditText.add(cmdDescription);
		if (command != null) {
			cmdName.setText(command.getName());
			cmdCommand.setText(command.getCommand());
			cmdDescription.setText(command.getDescription());
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
	 * Save edited texts to the command entity.
	 */
	private void onSave() {
		String cName = cmdName.getText().toString();
		String cCommand = cmdCommand.getText().toString();
		String cDescription = cmdDescription.getText().toString();
		if (cCommand != null && cName != null && cCommand.length() > 0
				&& cName.length() > 0) {
			if (cDescription == null) {
				cDescription = "";
			}
			((TKConfigApplication) application).showProgressDialog(
					parentActivity, R.string.please_wait);
			if (command == null) {
				command = new Command(cName, cCommand, cDescription);
				((TKConfigApplication) application).getCommands().add(command);
			} else {
				command.setName(cName);
				command.setCommand(cCommand);
				command.setDescription(cDescription);
			}
			((TKConfigApplication) application).commandsSave();
			((TKConfigActivity) parentActivity).reloadAdapter();
		}
	}

}
