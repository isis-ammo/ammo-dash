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
package edu.vu.isis.ammo.dash;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Subclass of TextView used to display location information in different formats.
 * 
 * Currently, Dash only displays location information in MGRS format, but it supports lat-lng 
 * format as well.
 * @author demetri
 *
 */
public class LocationTextView extends TextView {

	private Context mContext;
	private Location locationRef = null;
	
	public LocationTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	/**
	 * Takes lat lon coordinates and if settings dictate, transforms them to 
	 * MGRS and sets the view.
	 * @param lat
	 * @param lon
	 */
	public void setFormattedTextFromLocation(Location location) {
		super.setText(Util.toString(location, mContext));
	}
	
	public void notifyLocationChanged(Location location) {
		if (location != null) {
			this.setFormattedTextFromLocation(location);
			locationRef = location;	
		}
	}
	
	public boolean hasLocation() {
		return (locationRef != null);
	}
}
