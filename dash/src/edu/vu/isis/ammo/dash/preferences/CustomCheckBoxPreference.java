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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
/**
 * This is a checkbox widget that can be used in the preferences screen of Dash. 
 * It currently is not in use.
 * @author demetri
 *
 */
public class CustomCheckBoxPreference extends CheckBoxPreference {

	// ===========================================================
	// Constants
	// ===========================================================
	public static final Logger logger = LoggerFactory.getLogger("class.CustomCheckBoxPreference");
	
	public static enum Type {
		JOURNAL
	};
	// ===========================================================
	// Fields
	// ===========================================================
	private String summaryPrefix = "";
	private Type mType;
	
	// ===========================================================
	// Lifecycle
	// ===========================================================
	public CustomCheckBoxPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomCheckBoxPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CustomCheckBoxPreference(Context context) {
		super(context);
	}
	
	public CustomCheckBoxPreference(Context context, String aSummaryPrefix) {
		super(context);
		summaryPrefix = aSummaryPrefix;
	}
	
	// ===========================================================
	// 
	// ===========================================================

	/**
	 *  Set the summary field such that it displays the value of the edit text.
	 */
	public void refreshSummaryField() {
		if (!summaryPrefix.equals("")) {
			this.setSummary(summaryPrefix + this.isChecked());	
		}
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

	public void setType(Type mType) {
		this.mType = mType;
	}

	public Type getType() {
		return mType;
	}
	
	
}
