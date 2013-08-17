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
 * Here should be defined all constants.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public interface Constants {
	public static final String PASSWORD = "password";
	public static final String STARS = "******";
	
	/** This is the Regular expression used to identify the possible parameters **/
	public static final Pattern PARAMETERS = Pattern
			.compile("\\?[\\w\\d-]+\\?");
}
