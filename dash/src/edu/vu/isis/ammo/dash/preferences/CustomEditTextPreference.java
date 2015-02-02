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

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.widget.Toast;
import edu.vu.isis.ammo.dash.R;

/**
 * EditText widget that appears in a dialog when preference item is selected. 
 * @author demetri
 *
 */
public class CustomEditTextPreference extends EditTextPreference {

	// ===========================================================
	// Constants
	// ===========================================================
	public static final Logger logger = LoggerFactory.getLogger("class.CustomEditTextPreference");
	public static enum Type {
		IP, PORT, DEVICE_ID, OPERATOR_ID, OPERATOR_KEY
	};
	
	// ===========================================================
	// Fields
	// ===========================================================
	private String summaryPrefix = "";
	private Type mType;
	private Context context;
	
	
	// ===========================================================
	// Lifecycle
	// ===========================================================
	public CustomEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public CustomEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;	
	}
	
	public CustomEditTextPreference(Context context) {
		super(context);
		this.context = context;
	}
	
	public CustomEditTextPreference(Context context, String aSummaryPrefix) {
		super(context);
		this.context = context;
		summaryPrefix = aSummaryPrefix;
	}
	
	// ===========================================================
	// IP/Port Input Management
	// ===========================================================
	@Override
	public void setText(String uncheckedText) {
		// We should do some bounds checking here based on type of ETP.
		String checkedText = uncheckedText;
		
		if (mType != null) {
			switch (mType) {
			case IP:
				if (!this.validateIP(uncheckedText)) {
					Toast.makeText(context, "Invalid IP, please try again", Toast.LENGTH_SHORT).show();
					checkedText = this.getText();
				}
				
				break;
				
			case PORT:
				if (!this.validatePort(uncheckedText)) {
					Toast.makeText(context, "Invalid port, please try again", Toast.LENGTH_SHORT).show();
					checkedText = this.getText();
				}
				break;
				
			case DEVICE_ID:
				// checkedText = this.getText();
				break;
				
			case OPERATOR_ID:
				// checkedText = this.getText();
				break;
				
			case OPERATOR_KEY:
				// checkedText = this.getText();
				
			default:
					// do nothing.
			}	
		}
		super.setText(checkedText);
	}
	
	/**
	 *  Checks whether or not the input ip address is valid for IPv4 protocol.
	 *  @see http://forums.sun.com/thread.jspa?threadID=584205
	 *  
	 * @param ip
	 * @return
	 */
	public boolean validateIP(String ip) {
		 String two_five_five = "(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2(?:[0-4][0-9]|5[0-5]))";
	        Pattern IPPattern = Pattern.compile("^(?:"+two_five_five+"\\.){3}"+two_five_five+"$");
		return IPPattern.matcher(ip).matches();
	}
	/**
	 * Check that the port value supplied is appropriate.
	 * @see http://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers
	 * 
	 * @param port
	 * @return
	 */
	public boolean validatePort(String port) {
		try {
			if (port.length() > 5) return false;
			if (port.length() < 2) return false;
			int portAsInt = Integer.valueOf(port);
			if (portAsInt < 1) return false;
			if (portAsInt < 1024) {
				Toast.makeText(context, context.getResources().getString(R.string.hello), Toast.LENGTH_LONG).show();
				return false;
			}
			if (portAsInt < 49151) {
				Toast.makeText(context, context.getResources().getString(R.string.hello), Toast.LENGTH_LONG).show();
				return true;
			}
			return true;
		} catch (NumberFormatException e) {
			logger.debug("Invalid port number");
			return false;
		}
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
		if (!summaryPrefix.equals("")) {
			this.setSummary(summaryPrefix + this.getText());	
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
