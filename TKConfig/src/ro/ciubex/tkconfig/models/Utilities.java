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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.TKConfigApplication;

/**
 * Here should be defined utilities methods.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class Utilities {
	/** This is the Regular expression used to identify the possible parameters **/
	public static final Pattern PARAMETERS = Pattern
			.compile("\\?[\\w\\d-]+\\?");

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
}
