/* Copyright (c) 2010-2015 Vanderbilt University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.vu.isis.ammo.dash.preferences;


import android.content.Context;
import android.os.Bundle;
import edu.vu.isis.ammo.dash.R;

/**
 * Preference activity used to display certain preferences (well, duh). Mainly the
 * version number of Dash.
 * 
 * This class is currently not in use. 
 * @author demetri
 *
 */
public class DashPreferences extends PreferenceActivityEx {
	
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String PREF_DASH_LIMIT = "DASH_RETRIEVAL_LIMIT";
	public static final String DEFAULT_PREF_DASH_LIMIT = "50";
	
	// ===========================================================
	// Fields
	// ===========================================================
	private MyEditTextPreference dashLimit;
	
	// ===========================================================
	// Lifecycle
	// ===========================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.dash_preferences);
		dashLimit = (MyEditTextPreference) findPreference(PREF_DASH_LIMIT);
		
		this.setupViews();
	}

	// ===========================================================
	// UI Management
	// ===========================================================
	public void setupViews() {
		// Set the summary of each edit text to the current value
		// of its EditText field.
		if (dashLimit != null) dashLimit.refreshSummaryField();
	}

	public static boolean isMGRSPreference(Context context) {
		return true;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	
}
