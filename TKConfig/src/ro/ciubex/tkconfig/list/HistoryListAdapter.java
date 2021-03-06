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
package ro.ciubex.tkconfig.list;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.TKConfigApplication;
import ro.ciubex.tkconfig.models.History;
import ro.ciubex.tkconfig.models.Utilities;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This adapter is used to populate the history list view.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class HistoryListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private TKConfigApplication application;

	public HistoryListAdapter(Context context, TKConfigApplication application) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.application = application;
	}

	/**
	 * Get the number of histories in list.
	 * 
	 * @return The number of histories.
	 */
	@Override
	public int getCount() {
		return application.getHistories().size();
	}

	/**
	 * Get the history event from the specified position.
	 * 
	 * @param position
	 *            The position from the list.
	 * @return The history event at specified position.
	 */
	@Override
	public Object getItem(int position) {
		return position > -1 && position < getCount() ? application
				.getHistories().get(position) : null;
	}

	/**
	 * Get the item id associated with the specified position in the list. In
	 * this case the position is also the id.
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Get a View that displays the data at the specified position in the data
	 * set.
	 * 
	 * @param position
	 *            The position of the item within the adapter's data set of the
	 *            item whose view we want.
	 * @param view
	 *            The old view to reuse, if possible.
	 * @param parent
	 *            The parent that this view will eventually be attached to.
	 * @return A View corresponding to the data at the specified position.
	 */
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		HistoryViewHolder viewHolder = null;
		if (view != null) {
			viewHolder = (HistoryViewHolder) view.getTag();
		} else {
			view = mInflater.inflate(R.layout.history_item_list_layout, null);
			viewHolder = new HistoryViewHolder();
			viewHolder.historyCommand = (TextView) view
					.findViewById(R.id.history_command);
			viewHolder.historyPhone = (TextView) view
					.findViewById(R.id.history_phone);
			viewHolder.historyDateTime = (TextView) view
					.findViewById(R.id.history_date_time);
			view.setTag(viewHolder);
		}
		if (viewHolder != null) {
			History history = (History) getItem(position);
			if (history != null) {
				viewHolder.historyCommand.setText(history.getSmsCommand());
				viewHolder.historyPhone.setText(history.getPhoneNumber());
				viewHolder.historyDateTime.setText(Utilities.formatDateTime(
						application, history.getDateTime()));
			}
		}
		return view;
	}

	/**
	 * View holder for history item within the list.
	 * 
	 */
	static class HistoryViewHolder {
		TextView historyCommand;
		TextView historyPhone;
		TextView historyDateTime;
	}
}
