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


/**
 * @author phreed
 *
 */
public interface IDash {
	public static final String VIDEO_INTENT = "edu.vu.isis.ammo.dash.videoactivity.LAUNCH";
	public static final String VIDEO_PREVIEW_INTENT = "edu.vu.isis.ammo.dash.videopreviewactivity.LAUNCH";
	public static final String SETTINGS_INTENT = "edu.vu.isis.ammo.dash.preferences.DashPreferences.LAUNCH";
	public static final String BROWSE_REPORTS_INTENT = "edu.vu.isis.ammo.dash.ReportBrowserActivity.LAUNCH";
	public static final String STOP_PROGRESS_DIALOG_INTENT = "edu.vu.isis.ammo.dash.STOP_PROGRESS_DIALOG";
	public static final String VIEW_SUBSCRIPTIONS_INTENT = "edu.vu.isis.ammo.dash.SubscriptionViewer.LAUNCH";

	public static final String EXTRA_INCIDENT_ID = "extra_incident_id";
	public static final String EXTRA_INCIDENT_UUID = "extra_incident_uuid";

	public static final String MIME_TYPE_EXTENSION_BROADCAST = "broadcast";
	public static final String MIME_TYPE_EXTENSION_CALLSIGN = "callsign";
	public static final String MIME_TYPE_EXTENSION_TIGR_UID = "tigr_uid";
	public static final String MIME_TYPE_EXTENSION_UNIT = "unit";
}


