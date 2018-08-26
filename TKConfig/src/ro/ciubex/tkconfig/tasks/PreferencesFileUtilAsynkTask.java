/**
 * This file is part of TKConfig application.
 * 
 * Copyright (C) 2018 Claudiu Ciobotariu
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
package ro.ciubex.tkconfig.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import ro.ciubex.tkconfig.R;
import ro.ciubex.tkconfig.TKConfigApplication;
import ro.ciubex.tkconfig.models.Constants;
import ro.ciubex.tkconfig.models.Utilities;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Claudiu Ciobotariu
 * 
 */
public class PreferencesFileUtilAsynkTask extends
		AsyncTask<Void, Void, DefaultAsyncTaskResult> {
	private final static String TAG = PreferencesFileUtilAsynkTask.class
			.getName();

	/**
	 * The listener should implement this interface
	 */
	public interface Responder {
		public Application getApplication();

		public void startFileAsynkTask(Operation operationType);

		public void endFileAsynkTask(Operation operationType,
				DefaultAsyncTaskResult result);
	}

	/** Define available operations type */
	public enum Operation {
		BACKUP, RESTORE
	}

	private Responder responder;
	private Operation operationType;
	private String externalFileName;

	/**
	 * The constructor of this task
	 * 
	 * @param responder
	 *            The listener of this task
	 * @param fileName
	 *            Full file name path of exported / imported preferences
	 * @param operationType
	 *            Type of operation
	 */
	public PreferencesFileUtilAsynkTask(Responder responder, String fileName,
			Operation operationType) {
		this.responder = responder;
		externalFileName = fileName != null ? fileName.trim() : "";
		this.operationType = operationType;
	}

	/**
	 * Method invoked when is started this task
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.startFileAsynkTask(operationType);
	}

	/**
	 * Method invoked at the end of this task
	 * 
	 * @param result
	 *            The result of this task
	 */
	@Override
	protected void onPostExecute(DefaultAsyncTaskResult result) {
		super.onPostExecute(result);
		responder.endFileAsynkTask(operationType, result);
	}

	/**
	 * This is main task method, here should be processed all background
	 * operations
	 */
	@Override
	protected DefaultAsyncTaskResult doInBackground(Void... params) {
		DefaultAsyncTaskResult result = new DefaultAsyncTaskResult();
		if (externalFileName.length() > 0) {
			result.resultId = Constants.OK;
		} else {
			result.resultId = Constants.ERROR;
			result.resultMessage = responder.getApplication().getString(
					R.string.file_name_missing);
		}
		if (result.resultId == Constants.OK) {
			if (operationType == Operation.RESTORE) {
				restoreFromFile(result);
			} else {
				backupToFile(result);
			}
		}
		return result;
	}

	/**
	 * Check if parent folders exists. If not, create them. This method is
	 * invoked on exporting.
	 * 
	 * @param file
	 *            File which should be created with exported preferences
	 * @return True if parent folders exist or are created successfully
	 */
	private boolean createParentFolders(File directory) {
		if (directory != null) {
			if (directory.exists()) {
				return true;
			} else if (directory.mkdir()) {
				return true;
			}
			File canonDir = null;
			try {
				canonDir = directory.getCanonicalFile();
			} catch (IOException e) {
				return false;
			}
			File parentDir = canonDir.getParentFile();
			return (parentDir != null
					&& (createParentFolders(parentDir) || parentDir.exists()) && canonDir
						.mkdir());
		}
		return false;
	}

	/**
	 * Method used to backup all application preferences
	 * 
	 * @param result
	 *            Result of backup operation
	 */
	private void backupToFile(DefaultAsyncTaskResult result) {
		TKConfigApplication app = (TKConfigApplication) responder
				.getApplication();
		result.resultMessage = app.getString(R.string.backup_success,
				externalFileName);
		OutputStream fos = null;
		File outFile;
		try {
			outFile = new File(externalFileName);
			if (createParentFolders(outFile.getParentFile())) {
				fos = new FileOutputStream(outFile);
				SharedPreferences prefs = app.getSharedPreferences();
				Map<String, ?> keys = prefs.getAll();
				String key, clazz, value;
				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, ?> entry : keys.entrySet()) {
					key = entry.getKey();
					clazz = entry.getValue().getClass().getName();
					value = String.valueOf(entry.getValue());
					sb.append(key).append(':').append(clazz).append(':')
							.append(value).append('\n');
				}
				String content = sb.toString();
				fos.write(content.getBytes());
				fos.flush();
			} else {
				result.resultId = Constants.ERROR;
				result.resultMessage = app.getString(
						R.string.create_folders_error, externalFileName);
			}
		} catch (IllegalArgumentException e) {
			result.resultId = Constants.ERROR;
			result.resultMessage = app.getString(R.string.backup_exception,
					externalFileName, "IllegalArgumentException",
					e.getMessage());
			Log.e(TAG, "Exception: " + e.getMessage(), e);
		} catch (FileNotFoundException e) {
			result.resultId = Constants.ERROR;
			result.resultMessage = app.getString(R.string.backup_exception,
					externalFileName, "FileNotFoundException", e.getMessage());
			Log.e(TAG, "Exception: " + e.getMessage(), e);
		} catch (IOException e) {
			result.resultId = Constants.ERROR;
			result.resultMessage = app.getString(R.string.backup_exception,
					externalFileName, "IOException", e.getMessage());
			Log.e(TAG, "Exception: " + e.getMessage(), e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					result.resultId = Constants.ERROR;
					result.resultMessage = app.getString(
							R.string.backup_exception, externalFileName,
							"Closing IOException", e.getMessage());
					Log.e(TAG, "Exception: " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Method used to restore application preferences
	 * 
	 * @param result
	 *            Result of restore operation
	 */
	private void restoreFromFile(DefaultAsyncTaskResult result) {
		result.resultMessage = responder.getApplication().getString(
				R.string.restore_success, externalFileName);
		TKConfigApplication app = (TKConfigApplication) responder
				.getApplication();
		FileInputStream inFile = null;
		BufferedReader reader = null;
		try {
			File f = new File(externalFileName);
			if (f.exists()) {
				inFile = new FileInputStream(f);
				reader = new BufferedReader(new InputStreamReader(inFile));
				SharedPreferences prefs = app.getSharedPreferences();
				Editor editor = prefs.edit();
				String line;
				String[] arrLine;
				while ((line = reader.readLine()) != null) {
					arrLine = currentLine(line);
					if (arrLine != null) {
						storeCurrentLine(editor, arrLine);
					}
				}
				editor.commit();
			} else {
				result.resultId = Constants.ERROR;
				result.resultMessage = app.getString(
						R.string.restore_file_not_exist, externalFileName);
			}
		} catch (FileNotFoundException e) {
			result.resultId = Constants.ERROR;
			result.resultMessage = app.getString(R.string.restore_exception,
					externalFileName, "FileNotFoundException", e.getMessage());
			Log.e(TAG, "Exception: " + e.getMessage(), e);
		} catch (IOException e) {
			result.resultId = Constants.ERROR;
			result.resultMessage = app.getString(R.string.restore_exception,
					externalFileName, "IOException", e.getMessage());
			Log.e(TAG, "Exception: " + e.getMessage(), e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					Log.e(TAG, "Exception: " + e.getMessage(), e);
				}
			}
			if (inFile != null) {
				try {
					inFile.close();
				} catch (IOException e) {
					result.resultId = Constants.ERROR;
					result.resultMessage = app.getString(
							R.string.restore_exception, externalFileName,
							"Closing IOException", e.getMessage());
					Log.e(TAG, "Exception: " + e.getMessage(), e);
				}
			}
		}
	}

	private void storeCurrentLine(Editor editor, String[] arrLine) {
		String key = arrLine[0], clazz = arrLine[1], value = arrLine[2];
		int intValue;
		float floatValue;
		long longValue;
		if ("java.lang.String".equals(clazz)) {
			editor.putString(key, value);
		} else if ("java.lang.Boolean".equals(clazz)) {
			editor.putBoolean(key, "true".equalsIgnoreCase(value));
		} else if ("java.lang.Integer".equals(clazz)) {
			intValue = Utilities.parseInt(value);
			editor.putInt(key, intValue);
		} else if ("java.lang.Float".equals(clazz)) {
			floatValue = Utilities.parseFloat(value);
			editor.putFloat(key, floatValue);
		} else if ("java.lang.Long".equals(clazz)) {
			longValue = Utilities.parseLong(value);
			editor.putLong(key, longValue);
		}
	}

	private String[] currentLine(String line) {
		String arr[] = null;
		if (line != null) {
			String text = line.trim();
			if (text.length() > 0) {
				int idx1 = text.indexOf(':');
				int idx2 = text.indexOf(':', idx1 + 1);
				String key, clazz, value;
				if (idx1 > 0 && idx2 > 0) {
					key = text.substring(0, idx1);
					clazz = text.substring(idx1 + 1, idx2);
					value = text.substring(idx2 + 1);
					arr = new String[3];
					arr[0] = key;
					arr[1] = clazz;
					arr[2] = value;
				}
			}
		}
		return arr;
	}
}
