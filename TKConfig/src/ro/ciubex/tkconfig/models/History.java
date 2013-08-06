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

/**
 * This class define the history events model.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class History {
	private long dateTime;
	private String phoneNumber;
	private String smsCommand;
	
	public History(String phoneNumber, String smsCommand) {
		this(System.currentTimeMillis(), phoneNumber, smsCommand);
	}
	
	public History(long dateTime, String phoneNumber, String smsCommand) {
		this.dateTime = dateTime;
		this.phoneNumber = phoneNumber;
		this.smsCommand = smsCommand;
	}

	public long getDateTime() {
		return dateTime;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getSmsCommand() {
		return smsCommand;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (dateTime ^ (dateTime >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		History other = (History) obj;
		if (dateTime != other.dateTime)
			return false;
		return true;
	}
}
