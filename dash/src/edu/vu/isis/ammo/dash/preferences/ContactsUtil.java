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
import edu.vu.isis.ammo.INetPrefKeys;
import edu.vu.isis.ammo.api.AmmoContacts;
import edu.vu.isis.ammo.api.AmmoPreference;
//import android.provider.ContactsContract.Data;
//import android.provider.ContactsContract.RawContacts;

/**
 * Convenience class used for accessing user information stored in the contacts tables.
 * @author demetri
 *
 */
public class ContactsUtil {
	private static final Logger logger = LoggerFactory.getLogger("class.ContactsUtil");
	
	private ContactsUtil() {}
	
	/**
	 * @return the unit for this current user or null if cannot be found.
	 */
	public static String getUnit(Context context) {
		String userId;
		try {
			userId = AmmoPreference
                .getInstance(context)
                .getString(INetPrefKeys.CORE_OPERATOR_ID, 
                           INetPrefKeys.DEFAULT_CORE_OPERATOR_ID);
		}
		catch(Exception e) {
			//for some reason the AmmoPreference system will sometimes throw exceptions
			logger.error("AmmoPreference threw an exception for something it probably should not have: " + e, e);
			return null;
		}
		if(userId == null) {
			logger.error("AmmoPreference:  No operator id");
			return null;
		}
		

		AmmoContacts mData = AmmoContacts.newInstance(context);
		AmmoContacts.Contact g = mData.getContactByUserId(userId);
		if (g == null) return null;
		
		String unit = g.getUnit();
		return unit;
		
	}
	
}
