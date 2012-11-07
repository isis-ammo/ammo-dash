/*Copyright (C) 2010-2012 Institute for Software Integrated Systems (ISIS)
This software was developed by the Institute for Software Integrated
Systems (ISIS) at Vanderbilt University, Tennessee, USA for the 
Transformative Apps program under DARPA, Contract # HR011-10-C-0175.
The United States Government has unlimited rights to this software. 
The US government has the right to use, modify, reproduce, release, 
perform, display, or disclose computer software or computer software 
documentation in whole or in part, in any manner and for any 
purpose whatsoever, and to have or authorize others to do so.
*/
package edu.vu.isis.ammo.dash;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import edu.vu.isis.ammo.dash.preferences.ContactsUtil;
import edu.vu.isis.ammo.dash.provider.IncidentSchema.EventTableSchema;
import edu.vu.isis.ammo.dash.provider.IncidentSchemaBase.EventTableSchemaBase;

/**
 * Model object used as a wrapper for all data associated with a Dash report.
 * @author demetri
 *
 */
public class DashModel {
	private ContentValues model = new ContentValues();
	private Context context;
	
	private Bitmap thumbnail;
	private Uri photoUri;
	private Uri currentMediaUri;
	private int currentMediaType;
	private String templateData;
	
	
	private static final long BASE_DASH_SIZE = 20;
	private static final Logger logger = LoggerFactory.getLogger("class.DashModel");
	

	public DashModel(Context context) {
		this.context = context;
	}
	
	public ContentValues getContentValues() {
		setAdditionalFields();
		return model;
	}
	
	public void setContentValues(ContentValues model) {
		this.model = model;
		if(this.model == null) {
			this.model = new ContentValues();
		}
	}
	
	public String getId() {
		return model.getAsString(EventTableSchemaBase.UUID);
	}
	
	public void setId(String id) {
		model.put(EventTableSchemaBase.UUID, id);
	}
	
	public String getOriginator() {
		return model.getAsString(EventTableSchemaBase.ORIGINATOR);
	}
	
	public void setOriginator(String originator) {
		model.put(EventTableSchemaBase.ORIGINATOR, originator);
	}
	
	public String getDescription() {
		return model.getAsString(EventTableSchemaBase.DESCRIPTION);
	}

	public void setDescription(String description) {
		model.put(EventTableSchemaBase.DESCRIPTION, description);
	}
	
	public Long getTime() {
		return model.getAsLong(EventTableSchemaBase.MODIFIED_DATE);
	}
	
	public void setTime(Long time) {
		model.put(EventTableSchemaBase.CREATED_DATE, time);
		model.put(EventTableSchemaBase.MODIFIED_DATE, time);
	}
	
	public Location getLocation() {
		if(model.containsKey(EventTableSchemaBase.LATITUDE) && model.containsKey(EventTableSchemaBase.LONGITUDE)) {
			return Util.buildLocation(model.getAsDouble(EventTableSchemaBase.LATITUDE), model.getAsDouble(EventTableSchemaBase.LONGITUDE));
		}
		return null;
	}
	
	public void setLocation(Location location) {
		if(location != null) {
			// 10/29/12: (TA-4377)
			// Round coordinates to 4 decimal places to make the data displayed
			// on the device the same as what is displayed on BLOX
			
			BigDecimal lat = new BigDecimal(location.getLatitude());
			lat = lat.setScale(4, BigDecimal.ROUND_HALF_UP);
			
			BigDecimal lon = new BigDecimal(location.getLongitude());
			lon = lon.setScale(4, BigDecimal.ROUND_HALF_UP);
			
			logger.info("Rounded lat and lon to {},{}", lat.toPlainString(), lon.toPlainString());
			
			model.put(EventTableSchemaBase.LATITUDE, lat.doubleValue());
			model.put(EventTableSchemaBase.LONGITUDE, lon.doubleValue());
		}
	}
	
	public Uri getCurrentMediaUri() {
		return currentMediaUri;
	}

	public void setCurrentMediaUri(Uri currentMediaUri) {
		this.currentMediaUri = currentMediaUri;
	}

	public int getCurrentMediaType() {
		return currentMediaType;
	}

	public void setCurrentMediaType(int currentMediaType) {
		this.currentMediaType = currentMediaType;
	}
	
	public Uri getPhotoUri() {
		return photoUri;
	}

	public void setImageUri(Uri photoUri) {
		this.photoUri = photoUri;
	}

	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getTemplateData() {
		return templateData;
	}

	public void setTemplateData(String templateData) {
		this.templateData = templateData;
	}
	
	//
	// Code that massages the data model, to prepare it for delivery:
	//

	private void setAdditionalFields() {
		model.put(EventTableSchemaBase.UNIT, ContactsUtil.getUnit(context));
		model.put(EventTableSchemaBase.STATUS, EventTableSchema.STATUS_SENT);
		
		//For a "dash", these are intentionally blank
		model.put(EventTableSchemaBase.CATEGORY_ID, "");
		model.put(EventTableSchemaBase.DEST_GROUP_TYPE, "");
		model.put(EventTableSchemaBase.DEST_GROUP_NAME, "");
		model.put(EventTableSchemaBase.TITLE, "");
		model.put(EventTableSchemaBase.DISPLAY_NAME, "<no title>");
		
		model.put(EventTableSchemaBase.MEDIA_COUNT, Util.getMediaCount(currentMediaUri, templateData));
		model.put(EventTableSchemaBase.SIZE, Util.getSize(BASE_DASH_SIZE, context, currentMediaUri, templateData)/1000.);
	}

}
