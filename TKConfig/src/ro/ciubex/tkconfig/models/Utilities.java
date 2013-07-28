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

import java.util.regex.Pattern;

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
}
