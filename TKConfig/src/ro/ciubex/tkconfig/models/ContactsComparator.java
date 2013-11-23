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

import java.util.Comparator;

/**
 * This comparator is used to sort the contacts on the list.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class ContactsComparator implements Comparator<ContactModel> {

	@Override
	public int compare(ContactModel o1, ContactModel o2) {
		String s1 = o1.getContactName();
		String s2 = o2.getContactName();
		int n1 = s1 != null ? s1.length() : 0;
		int n2 = s2 != null ? s2.length() : 0;
		int min = Math.min(n1, n2);
		for (int i = 0; i < min; i++) {
			char c1 = s1.charAt(i);
			char c2 = s2.charAt(i);
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

}
