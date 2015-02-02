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
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * EditText widget that appears in a dialog when preference item is selected. 
 * @author demetri
 *
 */
public class MyEditTextPreference extends EditTextPreference {

	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================
	private String summaryPrefix = "";
	
	
	// ===========================================================
	// Lifecycle
	// ===========================================================
	public MyEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public MyEditTextPreference(Context context) {
		super(context);
	}
	
	public MyEditTextPreference(Context context, String aSummaryPrefix) {
		super(context);
		summaryPrefix = aSummaryPrefix;
	}
	
		
	
	@Override 
	protected void onDialogClosed (boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		
		// Set the summary field to newly set value in the edit text.
		this.refreshSummaryField();
	}
	
	/**
	 *  Set the summary field such that it displays the value of the edit text.
	 */
	public void refreshSummaryField() {
		this.setSummary(summaryPrefix + this.getText());	
	}

	// ===========================================================
	// Getters/Setters Methods
	// ===========================================================
	public String getSummaryPrefix() {
		return summaryPrefix;
	}

	public void setSummaryPrefix(String summaryPrefix) {
		this.summaryPrefix = summaryPrefix;
	}
}
