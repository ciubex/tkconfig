/**
 * 
 */
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
package ro.ciubex.tkconfig.list;

import java.util.ArrayList;
import java.util.List;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.models.Command;
import ro.ciubex.tkconfig.models.Constants;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This list adapter is used to show a list of parameter for a command.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class ParamListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Command mCommand;
	private List<String> mParameters;

	public ParamListAdapter(Context context, Command command) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		prepareParameters(command);
	}

	/**
	 * Method used to prepare parameters for a command.
	 * 
	 * @param command
	 *            The command with the parameters.
	 */
	private void prepareParameters(Command command) {
		mParameters = new ArrayList<String>();
		mCommand = command;
		for (String parameter : command.getParameters()) {
			if (!Constants.PASSWORD.equals(parameter)) {
				mParameters.add(parameter);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mParameters.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public String getItem(int position) {
		if (position > -1 && position < getCount()) {
			return mParameters.get(position);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int id) {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = mInflater.inflate(R.layout.param_list_layout, parent, false);
			viewHolder = initViewHolder(view);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		if (viewHolder != null) {
			prepareViewHolder(viewHolder, position);
		}
		return view;
	}

	/**
	 * Initialize the view holder.
	 * 
	 * @param view
	 *            The view with layout items.
	 * @return The view holder.
	 */
	private ViewHolder initViewHolder(View view) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.firstItemText = (TextView) view
				.findViewById(R.id.firstItemText);
		viewHolder.secondItemText = (TextView) view
				.findViewById(R.id.secondItemText);
		return viewHolder;
	}

	/**
	 * Prepare the view holder fields with texts.
	 * 
	 * @param view
	 *            View for current view holder.
	 * @return The view holder.
	 */
	private void prepareViewHolder(ViewHolder viewHolder, int position) {
		String parameter = getItem(position);
		if (parameter != null) {
			viewHolder.firstItemText.setText(parameter);
			String value = mCommand.getParameterValue(parameter);
			if (value != null) {
				viewHolder.secondItemText.setText(value);
			}
		}
	}

	/**
	 * View holder for item list elements
	 * 
	 */
	private class ViewHolder {
		TextView firstItemText;
		TextView secondItemText;
	}

}
