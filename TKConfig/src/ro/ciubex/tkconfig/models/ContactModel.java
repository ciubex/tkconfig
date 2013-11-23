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
package ro.ciubex.tkconfig.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is a model for phone contacts.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class ContactModel implements Comparable<ContactModel>, Parcelable {
	private long id;
	private String contactName;
	private String phoneNumber;
	private Bitmap picture;
	private boolean viewUpdated;

	public static final Parcelable.Creator<ContactModel> CREATOR = new Parcelable.Creator<ContactModel>() {
		public ContactModel createFromParcel(Parcel in) {
			return new ContactModel(in);
		}

		public ContactModel[] newArray(int size) {
			return new ContactModel[size];
		}
	};

	public ContactModel() {
	}

	public ContactModel(Parcel in) {
		readFromParcel(in);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean havePhoneNumber() {
		return phoneNumber != null && phoneNumber.length() > 0;
	}

	public Bitmap getPicture() {
		return picture;
	}

	public void setPicture(Bitmap picture) {
		this.picture = picture;
	}

	public boolean havePicture() {
		return (picture != null) ? (picture.getWidth() > 0 && picture
				.getHeight() > 0) : false;
	}

	@Override
	public int compareTo(ContactModel another) {
		int n1 = contactName != null ? contactName.length() : 0;
		int n2 = another.contactName != null ? another.contactName.length() : 0;
		int min = Math.min(n1, n2);
		for (int i = 0; i < min; i++) {
			char c1 = contactName.charAt(i);
			char c2 = another.contactName.charAt(i);
			if (c1 != c2) {
				c1 = Character.toUpperCase(c1);
				c2 = Character.toUpperCase(c2);
				if (c1 != c2) {
					c1 = Character.toLowerCase(c1);
					c2 = Character.toLowerCase(c2);
					if (c1 != c2) {
						return c1 - c2;
					}
				}
			}
		}
		return n1 - n2;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ContactModel [");
		builder.append(contactName);
		builder.append(", ");
		builder.append(id);
		builder.append(", ");
		builder.append(phoneNumber);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the viewUpdated
	 */
	public boolean isViewUpdated() {
		return viewUpdated;
	}

	/**
	 * @param viewUpdated
	 *            the viewUpdated to set
	 */
	public void setViewUpdated(boolean viewUpdated) {
		this.viewUpdated = viewUpdated;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(contactName);
		dest.writeString(phoneNumber);
	}

	public void readFromParcel(Parcel in) {
		id = in.readLong();
		contactName = in.readString();
		phoneNumber = in.readString();
	}
}
