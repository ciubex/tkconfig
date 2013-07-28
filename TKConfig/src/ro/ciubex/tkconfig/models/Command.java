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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Commands model class.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class Command {
	private String name;
	private String command;
	private String description;
	private List<String> parameters;
	private Map<String, Parameter> availableParameters;
	private boolean parametersModified;

	public Command(String name, String command) {
		this(name, command, "");
	}

	public Command(String name, String command, String description) {
		this.name = name;
		this.command = command;
		this.description = description;
		parameters = new ArrayList<String>();
		availableParameters = new HashMap<String, Parameter>();
		prepareParameters();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public boolean addParameter(String parameter) {
		return parameters.add(parameter);
	}

	public boolean hasParameters() {
		return parameters.size() > 0;
	}

	public boolean needParameters() {
		return command.indexOf("?") > -1;
	}

	public Map<String, Parameter> getAvailableParameters() {
		return availableParameters;
	}

	public boolean addToAvailableParameters(Parameter parameter) {
		return availableParameters.put(parameter.getName(), parameter) != null;
	}

	/**
	 * Set the value for a specified parameter.
	 * 
	 * @param parameterName
	 *            Parameter name.
	 * @param parameterValue
	 *            Parameter value.
	 */
	public void setParameterValue(String parameterName, String parameterValue) {
		Parameter p = availableParameters.get(parameterName);
		parametersModified = true;
		if (p == null) {
			p = new Parameter(parameterName, parameterValue);
			availableParameters.put(parameterName, p);
		} else {
			p.setValue(parameterValue);
		}
	}

	/**
	 * Obtain the parameter value for the specified parameter name.
	 * 
	 * @param parameterName
	 *            The parameter to be search.
	 * 
	 * @return The parameter value or the parameter name if the parameter is not
	 *         defined.
	 */
	public String getParameterValue(String parameterName) {
		String result = parameterName;
		if (availableParameters.containsKey(parameterName)) {
			Parameter p = availableParameters.get(parameterName);
			if (p != null) {
				result = p.getValue();
			}
		}
		return result;
	}

	/**
	 * Obtain a string list with the parameter names and the values.
	 * 
	 * @return A string list with the parameter names and values. If the
	 *         parameter list is empty, will be returned the empty string.
	 */
	public String getParametersListToBeShow() {
		StringBuilder sb = new StringBuilder("");
		int i = 1;
		for (String paramName : parameters) {
			if (sb.length() > 0) {
				sb.append(",\n");
			}
			sb.append(i).append(". ").append(paramName).append(": ")
					.append(getParameterValue(paramName));
			i++;
		}
		return sb.toString();
	}

	/**
	 * Get prepared SMS command, based on the parameters and associated values.
	 * 
	 * @return The SMS command to send.
	 */
	public String getSMSCommand() {
		String smsText = command;
		if (hasParameters()) {
			for (String param : parameters) {
				smsText = smsText.replaceAll("\\?" + param + "\\?",
						getParameterValue(param));
			}
		}
		return smsText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((command == null) ? 0 : command.hashCode());
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
		Command other = (Command) obj;
		if (command == null) {
			if (other.command != null)
				return false;
		} else if (!command.equals(other.command))
			return false;
		return true;
	}

	/**
	 * Prepare the parameters list.
	 */
	private void prepareParameters() {
		Matcher m = Utilities.PARAMETERS.matcher(command);
		while (m.find()) {
			parameters.add(Utilities.getParameterName(m.group()));
		}
	}

	/**
	 * Obtain the parameter name based on the position in the list.
	 * 
	 * @param parameterPosition
	 *            The position of the parameter in the list.
	 * @return The parameter name from the specified position. Null if the
	 *         position is not correct.
	 */
	public String getParameterName(int parameterPosition) {
		String parameterName = null;
		if (parameterPosition > -1 && parameterPosition < parameters.size()) {
			parameterName = parameters.get(parameterPosition);
		}
		return parameterName;
	}

	/**
	 * Returns the number of parameters.
	 * 
	 * @return The number of parameters.
	 */
	public int getParametersSize() {
		return parameters.size();
	}

	/**
	 * Check if the parameters were modified.
	 * 
	 * @return True if the parameters were modified.
	 */
	public boolean hasParametersModified() {
		return parametersModified;
	}

	/**
	 * Set the parameters modified flag state.
	 * 
	 * @param flag
	 *            The parameters modified flag state.
	 */
	public void setParametersModified(boolean flag) {
		parametersModified = flag;
	}

}
