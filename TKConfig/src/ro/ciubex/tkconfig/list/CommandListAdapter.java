/**
 * This file is part of TKConfig application.
 * 
 * Copyright (C) 2018 Claudiu Ciobotariu
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

import java.util.List;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.models.Command;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This is the list adapter used to populate the listView.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class CommandListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Command> commands;

	public CommandListAdapter(Context context, List<Command> commands) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.commands = commands;
	}

	/**
	 * Get the number of commands in list.
	 * 
	 * @return The number of commands.
	 */
	@Override
	public int getCount() {
		return commands.size();
	}

	/**
	 * Get the command from the specified position.
	 * 
	 * @param position
	 *            The position from the list.
	 * @return The command at specified position.
	 */
	@Override
	public Object getItem(int position) {
		return (position > -1 && position < commands.size()) ?
				commands.get(position) : null;
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
		CommandViewHolder viewHolder = null;
		if (view != null) {
			viewHolder = (CommandViewHolder) view.getTag();
		} else {
			view = mInflater.inflate(R.layout.list_item_layout, null);
			viewHolder = new CommandViewHolder();
			viewHolder.firstItemText = (TextView) view
					.findViewById(R.id.firstItemText);
			viewHolder.secondItemText = (TextView) view
					.findViewById(R.id.secondItemText);
			view.setTag(viewHolder);
		}
		if (viewHolder != null) {
			Command command = (Command) getItem(position);
			if (command != null) {
				viewHolder.firstItemText.setText(command.getName());
				viewHolder.secondItemText.setText(command.getCommand());
			}
		}
		return view;
	}

	/**
	 * View holder for command item within the list.
	 * 
	 */
	static class CommandViewHolder {
		TextView firstItemText;
		TextView secondItemText;
	}

}
