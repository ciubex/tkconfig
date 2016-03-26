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
import ro.ciubex.tkconfig.models.GpsContact;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * This adapter is used to populate the GPS contact list view.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class GpsContactListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private TKConfigApplication application;
	private boolean modified;

	public GpsContactListAdapter(Context context, TKConfigApplication application) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.application = application;
	}

	/**
	 * Get the number of GPS contacts in list.
	 * 
	 * @return The number of GPS contacts.
	 */
	@Override
	public int getCount() {
		return application.getContacts().size();
	}

	/**
	 * Get the GPS contact from the specified position.
	 * 
	 * @param position
	 *            The position from the list.
	 * @return The GPS contact at specified position.
	 */
	@Override
	public Object getItem(int position) {
		return position > -1 && position < getCount() ? application
				.getContacts().get(position) : null;
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
		GpsContactViewHolder viewHolder = null;
		if (view != null) {
			viewHolder = (GpsContactViewHolder) view.getTag();
		} else {
			view = mInflater.inflate(R.layout.gps_contact_item_list_layout,
					null);
			viewHolder = new GpsContactViewHolder();
			viewHolder.selected = (CheckBox) view
					.findViewById(R.id.contact_selected);
			viewHolder.name = (TextView) view.findViewById(R.id.gps_name);
			viewHolder.phone = (TextView) view.findViewById(R.id.gps_phone);
			view.setTag(viewHolder);
		}
		if (viewHolder != null) {
			final GpsContact contact = (GpsContact) getItem(position);
			if (contact != null) {
				viewHolder.selected.setChecked(contact.isSelected());
				viewHolder.name.setText(contact.getName());
				viewHolder.phone.setText(contact.getPhone());
				viewHolder.selected.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						contact.setSelected(cb.isChecked());
						modified = true;
					}
				});
			}
		}
		return view;
	}

	/**
	 * View holder for GPS contact item within the list.
	 * 
	 */
	static class GpsContactViewHolder {
		CheckBox selected;
		TextView name;
		TextView phone;
	}

	/**
	 * Obtain the modified flag.
	 * 
	 * @return True if the adapter items are modified.
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Set the modified flag.
	 * 
	 * @param modified
	 *            The modified flag.
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}

}
