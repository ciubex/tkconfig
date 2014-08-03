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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.models.ContactModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * @author Claudiu Ciobotariu
 * 
 */
public class ContactListAdapter extends BaseAdapter implements SectionIndexer {
	private LayoutInflater mInflater;
	private Filter filter;
	private Locale locale;
	private List<ContactModel> contacts;
	private List<ContactListItem> items;
	private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String[] sections;
	private Map<String, Integer> indexes;

	/**
	 * Define item views type
	 */
	public enum ITEM_TYPE {
		ITEM, SEPARATOR, UNUSED
	};

	public ContactListAdapter(Context context, List<ContactModel> contacts,
			Locale locale) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.contacts = contacts;
		this.locale = locale;
		items = new ArrayList<ContactListAdapter.ContactListItem>();
		indexes = new HashMap<String, Integer>();
		initListView(contacts);
	}

	/**
	 * Used to prepare all item views on this adapter
	 * 
	 * @param contacts
	 *            A list of contact models
	 */
	private void initListView(final List<ContactModel> contacts) {
		for (ContactModel contact : contacts) {
			add(contact);
		}
		initIndexes();
	}

	/**
	 * Initialize the list indexes
	 */
	public void initIndexes() {
		if (!indexes.isEmpty()) {
			indexes.clear();
		}
		if (items.isEmpty()) {
			sections = new String[1];
			char key = mSections.charAt(0);
			indexes.put("" + key, 0);
			sections[0] = "" + key;
		} else {
			int s, i, idx = 0;
			int size = mSections.length();
			int count = items.size();
			char key;
			char sectionChar;
			sections = new String[size];
			for (s = 0; s < size; s++) {
				key = mSections.charAt(s);
				for (i = idx; i < count; i++) {
					sectionChar = items.get(i).sectionChar;
					if (key == sectionChar) {
						idx = i;
						break;
					}
				}
				indexes.put("" + key, idx);
				sections[s] = "" + key;
			}
		}
	}

	/**
	 * Used to clear the list of items
	 */
	public void clear() {
		items.clear();
	}

	/**
	 * Used to obtain items list size
	 * 
	 * @return Items size
	 */
	@Override
	public int getCount() {
		return items.size();
	}

	/**
	 * Retrieve the contact model from the items list at specified position
	 * 
	 * @param position
	 *            The position in list for retrieve contact
	 * @return The contact model object a specified position
	 */
	@Override
	public ContactModel getItem(int position) {
		return items.get(position).contactModel;
	}

	/**
	 * Get the row id associated with the specified position in the list. In
	 * this case the position is also the id.
	 */
	@Override
	public long getItemId(int pos) {
		return pos;
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
		ContactListItem item = items.get(position);
		ContactsViewHolder viewHolder = null;
		if (view != null) {
			viewHolder = (ContactsViewHolder) view.getTag();
		}
		if (view == null || viewHolder == null
				|| viewHolder.itemType != item.itemType) {
			switch (item.itemType) {
			case ITEM:
				view = mInflater.inflate(R.layout.contact_item_layout, null);
				viewHolder = initItemView(view);
				view.setTag(viewHolder);
				break;
			case SEPARATOR:
				view = mInflater.inflate(R.layout.contact_separator_layout,
						null);
				viewHolder = initSeparatorView(view);
				view.setTag(viewHolder);
				break;
			case UNUSED:
				break;
			default:
				break;
			}
		}
		if (viewHolder != null) {
			switch (item.itemType) {
			case ITEM:
				prepareItemView(viewHolder, item.contactModel);
				break;
			case SEPARATOR:
				prepareSeparatorView(viewHolder, "" + item.sectionChar);
				break;
			case UNUSED:
				break;
			default:
				break;
			}
		}
		return view;
	}

	/**
	 * Initialize the separator item view
	 * 
	 * @param view
	 *            The view used to obtain the text view used to display the
	 *            separator character
	 * @return The separator view
	 */
	private ContactsViewHolder initSeparatorView(View view) {
		ContactsViewHolder viewHolder = new ContactsViewHolder();
		viewHolder.itemType = ITEM_TYPE.SEPARATOR;
		viewHolder.firstItemText = (TextView) view
				.findViewById(R.id.separatorItem);
		return viewHolder;
	}

	/**
	 * Initialize the normal item view, with the contact photo, name, birthday
	 * and check mark.
	 * 
	 * @param view
	 *            The view used to obtain all child views.
	 * @return Contact item view.
	 */
	private ContactsViewHolder initItemView(View view) {
		ContactsViewHolder viewHolder = new ContactsViewHolder();
		viewHolder.itemType = ITEM_TYPE.ITEM;
		viewHolder.picture = (ImageView) view.findViewById(R.id.contactImage);
		viewHolder.firstItemText = (TextView) view
				.findViewById(R.id.firstItemText);
		viewHolder.secondItemText = (TextView) view
				.findViewById(R.id.secondItemText);
		return viewHolder;
	}

	/**
	 * Prepare the separator view with the section separator character
	 * 
	 * @param viewHolder
	 *            The view holder of this section separator
	 * @param sectionChar
	 *            The section separator character
	 */
	private void prepareSeparatorView(ContactsViewHolder viewHolder,
			String sectionChar) {
		viewHolder.firstItemText.setText(sectionChar);
	}

	/**
	 * Prepare the normal item with the contact details
	 * 
	 * @param viewHolder
	 *            The view holder
	 * @param contact
	 *            The contact model used to extrat contact details
	 */
	private void prepareItemView(ContactsViewHolder viewHolder,
			ContactModel contact) {
		if (contact != null) {
			if (contact.havePicture()) {
				viewHolder.picture.setImageBitmap(contact.getPicture());
			} else {
				viewHolder.picture.setImageResource(R.drawable.contact_image);
			}
			viewHolder.firstItemText.setText(contact.getContactName());
			viewHolder.secondItemText.setText(contact.getPhoneNumber());
		}
	}

	/**
	 * Used to obtain the position for a specified section
	 * 
	 * @param section
	 *            Specified section
	 * @return The section for specified position
	 */
	@Override
	public int getPositionForSection(int section) {
		String key = sections[section];
		int position = 0;
		if (indexes.containsKey(key)) {
			position = indexes.get(key);
		}
		return position;
	}

	/**
	 * Used to obtain the section for a specified position
	 * 
	 * @param position
	 *            The specified position
	 * @return The section for specified position
	 */
	@Override
	public int getSectionForPosition(int position) {
		int index = 0;
		ContactListItem item = items.get(position);
		String key = "" + item.sectionChar;
		if (indexes.containsKey(key)) {
			index = indexes.get(key);
		}
		return index;
	}

	/**
	 * Retrieve all sections
	 * 
	 * @return The sections array
	 */
	@Override
	public Object[] getSections() {
		return sections;
	}

	/**
	 * Add a contact item to the adapter item list
	 * 
	 * @param item
	 *            The contact model to be added to the adapter item list
	 */
	public void add(ContactModel item) {
		items.add(prepareAdd(item));
	}

	/**
	 * Prepare the items list based on added contact model
	 * 
	 * @param item
	 *            The contact model to be added to the adapter item list
	 * @return A contact list item
	 */
	private ContactListItem prepareAdd(ContactModel item) {
		ContactListItem cItem = new ContactListItem(item);
		if (items.isEmpty()) {
			items.add(new ContactListItem(cItem.sectionChar));
		} else {
			ContactListItem lastItem = items.get(items.size() - 1);
			if (lastItem == null || lastItem.sectionChar != cItem.sectionChar) {
				items.add(new ContactListItem(cItem.sectionChar));
			}
		}
		return cItem;
	}

	/**
	 * Used to obtain the adapter filter
	 * 
	 * @return Adapter customized filter
	 */
	public Filter getFilter() {
		if (filter == null)
			filter = new ContactListFilter(this, locale);
		return filter;
	}

	/**
	 * Get all contacts loaded on the adapter
	 * 
	 * @return List of contact models
	 */
	public List<ContactModel> getContacts() {
		return contacts;
	}

	/**
	 * View holder for contact items within the list.
	 * 
	 */
	static class ContactsViewHolder {
		ITEM_TYPE itemType;
		ImageView picture;
		TextView firstItemText;
		TextView secondItemText;
	}

	/**
	 * Static class with contact list items holders.
	 */
	static class ContactListItem {
		ITEM_TYPE itemType;
		char sectionChar;
		ContactModel contactModel;

		public ContactListItem() {
			itemType = ITEM_TYPE.UNUSED;
		}

		public ContactListItem(char sectionChar) {
			this.sectionChar = sectionChar;
			itemType = ITEM_TYPE.SEPARATOR;
		}

		public ContactListItem(ContactModel contactModel) {
			this.contactModel = contactModel;
			itemType = ITEM_TYPE.ITEM;
			char ch = contactModel.getContactName().charAt(0);
			sectionChar = (Character.isDigit(ch) ? '#' : Character
					.toUpperCase(ch));
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ContactListItem [").append(sectionChar)
					.append(", ").append(contactModel).append("]");
			return builder.toString();
		}
	}
}
