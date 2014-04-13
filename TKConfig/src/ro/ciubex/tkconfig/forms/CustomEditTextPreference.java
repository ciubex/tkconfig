/**
 * This file is part of TKConfig application.
 * 
 * Copyright (C) 2014 Claudiu Ciobotariu
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
package ro.ciubex.tkconfig.forms;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * This class is used to create a customizable dialog preference. This dialog is
 * used for exporting and importing preferences.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class CustomEditTextPreference extends EditTextPreference {

	public interface Listener {
		public void onPositiveResult(int resultId, String value);

		public void onNegativeResult(int resultId, String value);
	}

	private Listener listener;
	private int resultId;

	public CustomEditTextPreference(Context context) {
		super(context);
		setPersistent(false);
	}

	public CustomEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPersistent(false);
	}

	public CustomEditTextPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setPersistent(false);
	}

	/**
	 * This method is used to attach to this control a listener with an id used
	 * by the listener to determinate the proper action
	 * 
	 * @param listener
	 *            The listener of this control
	 * @param resultId
	 *            The id of the action invoked at the end when this dialog
	 *            preference is closed
	 */
	public void setResultListener(Listener listener, int resultId) {
		this.listener = listener;
		this.resultId = resultId;
	}

	/**
	 * This method is used to call the listener when this dialog preference is
	 * closed.
	 */
	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (listener != null) {
			if (positiveResult) {
				listener.onPositiveResult(resultId, super.getText());
			} else {
				listener.onNegativeResult(resultId, super.getText());
			}
		}
	}
}
