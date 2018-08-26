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
package ro.ciubex.tkconfig.activities;

import ro.ciubex.tkconfig.R;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Define About activity.
 * 
 * @author Claudiu Ciobotariu
 * 
 */
public class AboutActivity extends BaseActivity {
	private String version = "1.0";

	/**
	 * Prepare About activity
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);

		TextView tv = (TextView) findViewById(R.id.aboutTextView);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		tv.setText(Html.fromHtml(prepareAboutText()));

		setMenuId(R.menu.about_menu);
	}

	/**
	 * Prepare the full about text adding the version number, stored on the
	 * Application manifest file.
	 * 
	 * @return The about text.
	 */
	private String prepareAboutText() {
		String aboutText = "";
		try {
			version = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		aboutText = getString(R.string.about_text, version);
		return aboutText;
	}

	/**
	 * Prepare Option menu
	 */
	@Override
	protected boolean onMenuItemSelected(int menuItemId) {
		boolean processed = false;
		switch (menuItemId) {
		case R.id.menu_back:
			processed = true;
			goBack();
			break;
		case R.id.menu_license:
			processed = true;
			showLicense();
			break;
		case R.id.menu_policy:
			processed = true;
			showPolicy();
			break;
		}
		return processed;
	}

	/**
	 * Launch License Activity
	 */
	private void showLicense() {
		Intent intent = new Intent(getBaseContext(), ShowAssetsActivity.class);
		Bundle b = new Bundle();
		b.putString(ShowAssetsActivity.FILE_NAME, "gpl-3.0-standalone.html");
		intent.putExtras(b);
		startActivityForResult(intent, 1);
	}

	/**
	 * Launch Policy Activity
	 */
	private void showPolicy() {
		Intent intent = new Intent(getBaseContext(), ShowAssetsActivity.class);
		Bundle b = new Bundle();
		b.putString(ShowAssetsActivity.FILE_NAME, "privacy-policy.html");
		intent.putExtras(b);
		startActivityForResult(intent, 1);
	}
}
