/**
 * This file is part of TKConfig application.
 * 
 * Copyright (C) 2015 Claudiu Ciobotariu
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.TKConfigApplication;

/**
 * Here should be defined utilities methods.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class Utilities {

	/**
	 * Extract the string between ? signs. e.g.: For ?parameter-name? will be
	 * returned the string: parameter-name
	 * 
	 * @param originalString
	 *            The original string used to extract the parameter name.
	 * @return The computed parameter name.
	 */
	public static String getParameterName(String originalString) {
		String parameterName = originalString;
		if (parameterName != null && parameterName.startsWith("?")
				&& parameterName.endsWith("?")) {
			parameterName = parameterName.substring(1,
					parameterName.length() - 1);
		}
		return parameterName;
	}

	/**
	 * Format the date time information into a human readable format.
	 * 
	 * @param dateTime
	 *            The date time information in milliseconds.
	 * @return The formated date time.
	 */
	public static String formatDateTime(TKConfigApplication application,
			long dateTime) {
		Calendar today = Calendar.getInstance();
		Calendar yesterday = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(dateTime);
		yesterday.add(Calendar.DATE, -1);
		DateFormat dateFormatter = new SimpleDateFormat(
				application.getString(R.string.date_format),
				application.getDefaultLocale());
		DateFormat timeFormatter = new SimpleDateFormat(
				application.getString(R.string.time_format),
				application.getDefaultLocale());
		String date = "";

		if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
				&& calendar.get(Calendar.DAY_OF_YEAR) == today
						.get(Calendar.DAY_OF_YEAR)) {
			date = application.getString(R.string.today);
		} else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR)
				&& calendar.get(Calendar.DAY_OF_YEAR) == yesterday
						.get(Calendar.DAY_OF_YEAR)) {
			date = application.getString(R.string.yesterday);
		} else {
			date = dateFormatter.format(calendar.getTime());
		}
		return date + " " + timeFormatter.format(calendar.getTime());
	}

	/**
	 * Parse a string to an integer. If the string could not be formated zero
	 * will be returned.
	 * 
	 * @param value
	 *            The string value to be parsed.
	 * @return Parsed integer.
	 */
	public static int parseInt(String value) {
		return parseInt(value, 0);
	}

	/**
	 * Parse a string to an integer. If the string could not be formated will be
	 * returned the defaultValue.
	 * 
	 * @param value
	 *            The string value to be parsed.
	 * @param defaultValue
	 *            Default value returned if the string could not be parsed.
	 * @return Parsed integer.
	 */
	public static int parseInt(String value, int defaultValue) {
		int i = defaultValue;
		try {
			i = Integer.parseInt(value);
		} catch (NumberFormatException e) {
		}
		return i;
	}

	/**
	 * Parse a string to a float number. If the string could not be parsed will
	 * be returned the value zero.
	 * 
	 * @param value
	 *            The string to be parsed.
	 * @return Parsed float.
	 */
	public static float parseFloat(String value) {
		float f = 0;
		try {
			f = Float.parseFloat(value);
		} catch (NumberFormatException e) {
		}
		return f;
	}

	/**
	 * Parse a string to a long number. If the string could not be parsed will
	 * be returned the value zero.
	 * 
	 * @param value
	 *            The string to be parsed.
	 * @return Parsed long.
	 */
	public static long parseLong(String value) {
		long l = 0;
		try {
			l = Long.parseLong(value);
		} catch (NumberFormatException e) {
		}
		return l;
	}

	/**
	 * Returns true if the object is null or is empty.
	 *
	 * @param object The object to be examined.
	 * @return True if object is null or zero length.
	 */
	public static boolean isEmpty(Object object) {
		if (object != null) {
			if (object instanceof CharSequence) {
				String string = String.valueOf(object);
				return string.trim().length() == 0;
			} else if (object instanceof StringBuilder) {
				String string = String.valueOf(object);
				return string.trim().length() == 0;
			} else if (object instanceof Collection) {
				return ((Collection) object).isEmpty();
			} else if (object instanceof Object[]) {
				return ((Object[]) object).length == 0;
			}
			return false;
		}
		return true;
	}
}
