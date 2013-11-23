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
package ro.ciubex.tkconfig.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ro.ciubex.tkconfig.models.Constants;
import ro.ciubex.tkconfig.models.ContactModel;
import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

/**
 * An AsyncTask used to load contacts pictures.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class LoadContactImageAsyncTask extends
		AsyncTask<Void, Long, DefaultAsyncTaskResult> {

	/**
	 * Responder used on loading process.
	 */
	public interface Responder {
		public Application getApplication();

		public void startLoadPictures();

		public void updateItemView(long contactId);

		public void endLoadPictures(DefaultAsyncTaskResult result);
	}

	private List<ContactModel> contacts;
	private Responder responder;

	public LoadContactImageAsyncTask(Responder responder,
			List<ContactModel> contacts) {
		this.responder = responder;
		this.contacts = contacts;
	}

	/**
	 * Method invoked on the UI thread before the task is executed.
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		responder.startLoadPictures();
	}

	/**
	 * This method is used to update the UI during this thread.
	 */
	@Override
	protected void onProgressUpdate(Long... values) {
		super.onProgressUpdate(values);
		responder.updateItemView(values != null ? values[0] : -1L);
	}

	/**
	 * Method invoked on the UI thread after the background computation
	 * finishes.
	 */
	@Override
	protected void onPostExecute(DefaultAsyncTaskResult result) {
		super.onPostExecute(result);
		responder.endLoadPictures(result);
	}

	/**
	 * Method invoked on the background thread.
	 */
	@Override
	protected DefaultAsyncTaskResult doInBackground(Void... params) {
		DefaultAsyncTaskResult result = new DefaultAsyncTaskResult();
		result.resultId = Constants.OK;
		loadAllImages(result);
		return result;
	}

	/**
	 * The main method used to load for all contacts the picture.
	 * 
	 * @param result
	 *            The process result.
	 */
	private void loadAllImages(DefaultAsyncTaskResult result) {
		Bitmap image;
		Application app = responder.getApplication();
		if (contacts.size() > 0) {
			ContentResolver cr = app.getContentResolver();
			for (ContactModel contact : contacts) {
				image = loadContactImage(cr, contact.getId());
				if (image != null) {
					contact.setPicture(image);
					publishProgress(contact.getId());
				}
				image = null;
			}
		}
	}

	/**
	 * Method used to load the bitmap picture for a contact.
	 * 
	 * @param cr
	 *            The application ContentResolver
	 * @param contactId
	 *            The contact id.
	 * @return The contact picture bitmap.
	 */
	private Bitmap loadContactImage(ContentResolver cr, long contactId) {
		Bitmap thumbnail = null;
		InputStream input = null;
		try {
			Uri uri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI, contactId);
			input = ContactsContract.Contacts.openContactPhotoInputStream(cr,
					uri);
			if (input != null) {
				thumbnail = BitmapFactory.decodeStream(input);
			}
		} catch (Exception ex) {

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
		return thumbnail;
	}

}
