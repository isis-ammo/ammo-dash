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
package edu.vu.isis.ammo.dash.provider;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.SyncStateContract.Columns;
import android.provider.SyncStateContract.Constants;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import edu.vu.isis.ammo.dash.Util;
import edu.vu.isis.ammo.dash.incident.provider.IncidentContentDescriptor;

/**
 * SyncAdapter implementation for syncing sample SyncAdapter contacts to the
 * platform ContactOperations provider.
 */
public class IncidentSyncAdaptor extends AbstractThreadedSyncAdapter {
	private final Logger logger = LoggerFactory.getLogger("class.IncidentSyncAdaptor");

	private final AccountManager accountManager;
	@SuppressWarnings("unused")
	private final Context context;
	@SuppressWarnings("unused")
	private Date lastUpdate;

	public IncidentSyncAdaptor(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		this.context = context;
		accountManager = AccountManager.get(context);
		this.lastUpdate = new Date();
	}

	protected static final UriMatcher uriMatcher;

	protected static String[] mediaProjectionKey;
	protected static HashMap<String, String> mediaProjectionMap;

	protected static String[] eventProjectionKey;
	protected static HashMap<String, String> eventProjectionMap;

	protected static String[] categoryProjectionKey;
	protected static HashMap<String, String> categoryProjectionMap;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		//uriMatcher.addURI(IncidentSchemaBase.AUTHORITY, Tables.MEDIA_TBL, MEDIA_SET);
		//uriMatcher.addURI(IncidentSchemaBase.AUTHORITY, Tables.MEDIA_TBL + "/#", MEDIA_ID);

		//uriMatcher.addURI(IncidentSchemaBase.AUTHORITY, Tables.EVENT_TBL, EVENT_SET);
		//uriMatcher.addURI(IncidentSchemaBase.AUTHORITY, Tables.EVENT_TBL + "/#", EVENT_ID);

		//uriMatcher.addURI(IncidentSchemaBase.AUTHORITY, Tables.CATEGORY_TBL, CATEGORY_SET);
		//uriMatcher.addURI(IncidentSchemaBase.AUTHORITY, Tables.CATEGORY_TBL + "/#", CATEGORY_ID);


		HashMap<String, String> columns;
		mediaProjectionKey = new String[1];
		mediaProjectionKey[0] = BaseColumns._ID;

		columns = new HashMap<String, String>();
		columns.put(BaseColumns._ID, BaseColumns._ID);
		columns.put(IncidentContentDescriptor.Media.Cols.EVENT_ID, "\""+IncidentContentDescriptor.Media.Cols.EVENT_ID+"\""); 
		columns.put(IncidentContentDescriptor.Media.Cols.DATA_TYPE, "\""+IncidentContentDescriptor.Media.Cols.DATA_TYPE+"\"");
		columns.put(IncidentContentDescriptor.Media.Cols.DATA, "\""+IncidentContentDescriptor.Media.Cols.DATA+"\""); 
		columns.put(IncidentContentDescriptor.Media.Cols.CREATED_DATE, "\""+IncidentContentDescriptor.Media.Cols.CREATED_DATE+"\""); 
		columns.put(IncidentContentDescriptor.Media.Cols.MODIFIED_DATE, "\""+IncidentContentDescriptor.Media.Cols.MODIFIED_DATE+"\""); 
		columns.put(IncidentContentDescriptor.Media.Cols._RECEIVED_DATE, "\""+IncidentContentDescriptor.Media.Cols._RECEIVED_DATE+"\"");
		columns.put(IncidentContentDescriptor.Media.Cols._DISPOSITION, "\""+IncidentContentDescriptor.Media.Cols._DISPOSITION+"\"");

		mediaProjectionMap = columns;

		eventProjectionKey = new String[1];
		eventProjectionKey[0] = BaseColumns._ID;

		columns = new HashMap<String, String>();
		columns.put(BaseColumns._ID, BaseColumns._ID);
		columns.put(IncidentContentDescriptor.Event.Cols.UUID, "\""+IncidentContentDescriptor.Event.Cols.UUID+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.MEDIA_COUNT, "\""+IncidentContentDescriptor.Event.Cols.MEDIA_COUNT+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.ORIGINATOR, "\""+IncidentContentDescriptor.Event.Cols.ORIGINATOR+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.DISPLAY_NAME, "\""+IncidentContentDescriptor.Event.Cols.DISPLAY_NAME+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.CATEGORY_ID, "\""+IncidentContentDescriptor.Event.Cols.CATEGORY_ID+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.TITLE, "\""+IncidentContentDescriptor.Event.Cols.TITLE+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.DESCRIPTION, "\""+IncidentContentDescriptor.Event.Cols.DESCRIPTION+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.LONGITUDE, "\""+IncidentContentDescriptor.Event.Cols.LONGITUDE+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.LATITUDE, "\""+IncidentContentDescriptor.Event.Cols.LATITUDE+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.CREATED_DATE, "\""+IncidentContentDescriptor.Event.Cols.CREATED_DATE+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.MODIFIED_DATE, "\""+IncidentContentDescriptor.Event.Cols.MODIFIED_DATE+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.CID, "\""+IncidentContentDescriptor.Event.Cols.CID+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.CATEGORY, "\""+IncidentContentDescriptor.Event.Cols.CATEGORY+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.UNIT, "\""+IncidentContentDescriptor.Event.Cols.UNIT+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.SIZE, "\""+IncidentContentDescriptor.Event.Cols.SIZE+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.DEST_GROUP_TYPE, "\""+IncidentContentDescriptor.Event.Cols.DEST_GROUP_TYPE+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.DEST_GROUP_NAME, "\""+IncidentContentDescriptor.Event.Cols.DEST_GROUP_NAME+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols.STATUS, "\""+IncidentContentDescriptor.Event.Cols.STATUS+"\""); 
		columns.put(IncidentContentDescriptor.Event.Cols._RECEIVED_DATE, "\""+IncidentContentDescriptor.Event.Cols._RECEIVED_DATE+"\"");
		columns.put(IncidentContentDescriptor.Event.Cols._DISPOSITION, "\""+IncidentContentDescriptor.Event.Cols._DISPOSITION+"\"");

		eventProjectionMap = columns;

		categoryProjectionKey = new String[1];
		categoryProjectionKey[0] = BaseColumns._ID;

		columns = new HashMap<String, String>();
		columns.put(BaseColumns._ID, BaseColumns._ID);
		columns.put(IncidentContentDescriptor.Category.Cols.MAIN_CATEGORY, "\""+IncidentContentDescriptor.Category.Cols.MAIN_CATEGORY+"\""); 
		columns.put(IncidentContentDescriptor.Category.Cols.SUB_CATEGORY, "\""+IncidentContentDescriptor.Category.Cols.SUB_CATEGORY+"\""); 
		columns.put(IncidentContentDescriptor.Category.Cols.TIGR_ID, "\""+IncidentContentDescriptor.Category.Cols.TIGR_ID+"\""); 
		columns.put(IncidentContentDescriptor.Category.Cols.ICON_TYPE, "\""+IncidentContentDescriptor.Category.Cols.ICON_TYPE+"\"");
		columns.put(IncidentContentDescriptor.Category.Cols.ICON, "\""+IncidentContentDescriptor.Category.Cols.ICON+"\""); 
		columns.put(IncidentContentDescriptor.Category.Cols._RECEIVED_DATE, "\""+IncidentContentDescriptor.Category.Cols._RECEIVED_DATE+"\"");
		columns.put(IncidentContentDescriptor.Category.Cols._DISPOSITION, "\""+IncidentContentDescriptor.Category.Cols._DISPOSITION+"\"");

		categoryProjectionMap = columns;

	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		try {
			// use the account manager to request the credentials
			final String  authtoken = accountManager
					.blockingGetAuthToken(account, Columns.ACCOUNT_TYPE, true);         

			// update the last synced date.
			this.lastUpdate = new Date();
			// update platform contacts.
			logger.debug("Calling contactManager's sync contacts {}", authtoken);
			// fetch and update status messages for all the synced users.
		} catch (final AuthenticatorException ex) {
			syncResult.stats.numParseExceptions++;
			logger.error("AuthenticatorException", ex);
		} catch (final OperationCanceledException ex) {
			logger.error("OperationCanceledExcetpion", ex);
		} catch (final IOException ex) {
			logger.error("IOException", ex);
			syncResult.stats.numIoExceptions++;
		} catch (final ParseException ex) {
			syncResult.stats.numParseExceptions++;
			logger.error("ParseException", ex);
		} 
	}

	/**
	 * Table Name: media <P>
	 */
	static public class MediaWrapper {
		public MediaWrapper() {
			// logger.info("building MediaWrapper");
		}
		private String eventId;
		public String getEventId() {
			return this.eventId;
		}
		public MediaWrapper setEventId(String val) {
			this.eventId = val;
			return this;
		} 
		private String dataType;
		public String getDataType() { 
			return this.dataType;
		}
		public MediaWrapper setDataType(String val) {
			this.dataType = val;
			return this;
		}

		private String data;
		public String getData() {
			return this.data;
		}
		public MediaWrapper setData(String val) {
			this.data = val;
			return this;
		}  
		private long createdDate;
		public long getCreatedDate() {
			return this.createdDate;
		}
		public MediaWrapper setCreatedDate(long val) {
			this.createdDate = val;
			return this;
		} 
		private long modifiedDate;
		public long getModifiedDate() {
			return this.modifiedDate;
		}
		public MediaWrapper setModifiedDate(long val) {
			this.modifiedDate = val;
			return this;
		} 
		private int _disposition;
		public int get_Disposition() {
			return this._disposition;
		}
		public MediaWrapper set_Disposition(int val) {
			this._disposition = val;
			return this;
		}
		private long _received_date;
		public long get_ReceivedDate() {
			return this._received_date;
		}
		public MediaWrapper set_ReceivedDate(long val) {
			this._received_date = val;
			return this;
		}
	} 
	/**
	 * Table Name: event <P>
	 */
	static public class EventWrapper {
		public EventWrapper() {
			// logger.info("building EventWrapper");
		}
		private String uuid;
		public String getUuid() {
			return this.uuid;
		}
		public EventWrapper setUuid(String val) {
			this.uuid = val;
			return this;
		} 
		private int mediaCount;
		public int getMediaCount() {
			return this.mediaCount;
		}
		public EventWrapper setMediaCount(int val) {
			this.mediaCount = val;
			return this;
		} 
		private String originator;
		public String getOriginator() {
			return this.originator;
		}
		public EventWrapper setOriginator(String val) {
			this.originator = val;
			return this;
		} 
		private String displayName;
		public String getDisplayName() {
			return this.displayName;
		}
		public EventWrapper setDisplayName(String val) {
			this.displayName = val;
			return this;
		} 
		private String categoryId;
		public String getCategoryId() {
			return this.categoryId;
		}
		public EventWrapper setCategoryId(String val) {
			this.categoryId = val;
			return this;
		} 
		private String title;
		public String getTitle() {
			return this.title;
		}
		public EventWrapper setTitle(String val) {
			this.title = val;
			return this;
		} 
		private String description;
		public String getDescription() {
			return this.description;
		}
		public EventWrapper setDescription(String val) {
			this.description = val;
			return this;
		} 
		private double longitude;
		public double getLongitude() {
			return this.longitude;
		}
		public EventWrapper setLongitude(double val) {
			this.longitude = val;
			return this;
		} 
		private double latitude;
		public double getLatitude() {
			return this.latitude;
		}
		public EventWrapper setLatitude(double val) {
			this.latitude = val;
			return this;
		} 
		private long createdDate;
		public long getCreatedDate() {
			return this.createdDate;
		}
		public EventWrapper setCreatedDate(long val) {
			this.createdDate = val;
			return this;
		} 
		private long modifiedDate;
		public long getModifiedDate() {
			return this.modifiedDate;
		}
		public EventWrapper setModifiedDate(long val) {
			this.modifiedDate = val;
			return this;
		} 
		private String cid;
		public String getCid() {
			return this.cid;
		}
		public EventWrapper setCid(String val) {
			this.cid = val;
			return this;
		} 
		private String category;
		public String getCategory() {
			return this.category;
		}
		public EventWrapper setCategory(String val) {
			this.category = val;
			return this;
		} 
		private String unit;
		public String getUnit() {
			return this.unit;
		}
		public EventWrapper setUnit(String val) {
			this.unit = val;
			return this;
		} 
		private long size;
		public long getSize() {
			return this.size;
		}
		public EventWrapper setSize(long val) {
			this.size = val;
			return this;
		} 
		private String destGroupType;
		public String getDestGroupType() {
			return this.destGroupType;
		}
		public EventWrapper setDestGroupType(String val) {
			this.destGroupType = val;
			return this;
		} 
		private String destGroupName;
		public String getDestGroupName() {
			return this.destGroupName;
		}
		public EventWrapper setDestGroupName(String val) {
			this.destGroupName = val;
			return this;
		} 
		private int status;
		public int getStatus() {
			return this.status;
		}
		public EventWrapper setStatus(int val) {
			this.status = val;
			return this;
		} 
		private int _disposition;
		public int get_Disposition() {
			return this._disposition;
		}
		public EventWrapper set_Disposition(int val) {
			this._disposition = val;
			return this;
		}
		private long _received_date;
		public long get_ReceivedDate() {
			return this._received_date;
		}
		public EventWrapper set_ReceivedDate(long val) {
			this._received_date = val;
			return this;
		}
	} 
	/**
	 * Table Name: category <P>
	 */
	static public class CategoryWrapper {
		public CategoryWrapper() {
			// logger.info("building CategoryWrapper");
		}
		private String mainCategory;
		public String getMainCategory() {
			return this.mainCategory;
		}
		public CategoryWrapper setMainCategory(String val) {
			this.mainCategory = val;
			return this;
		} 
		private String subCategory;
		public String getSubCategory() {
			return this.subCategory;
		}
		public CategoryWrapper setSubCategory(String val) {
			this.subCategory = val;
			return this;
		} 
		private String tigrId;
		public String getTigrId() {
			return this.tigrId;
		}
		public CategoryWrapper setTigrId(String val) {
			this.tigrId = val;
			return this;
		} 
		private String iconType;
		public String getIconType() { 
			return this.iconType;
		}
		public CategoryWrapper setIconType(String val) {
			this.iconType = val;
			return this;
		}

		private String icon;
		public String getIcon() {
			return this.icon;
		}
		public CategoryWrapper setIcon(String val) {
			this.icon = val;
			return this;
		}  
		private int _disposition;
		public int get_Disposition() {
			return this._disposition;
		}
		public CategoryWrapper set_Disposition(int val) {
			this._disposition = val;
			return this;
		}
		private long _received_date;
		public long get_ReceivedDate() {
			return this._received_date;
		}
		public CategoryWrapper set_ReceivedDate(long val) {
			this._received_date = val;
			return this;
		}
	} 



	/**
	 * This method is provided with the express purpose of being overridden and extended.
	 *
	 *    StringBuilder sb = new StringBuilder();
	 *    sb.append("\""+IncidentContentDescriptor.Media.Cols.FUNCTION_CODE+"\" = '"+ wrap.getFunctionCode()+"'"); 
	 *    return sb.toString();   
	 *
	 * @param wrap
	 */
	protected String mediaSelectKeyClause(MediaWrapper wrap) {
		return null;
	}

	/**
	 * This method is provided with the express purpose of being overridden and extended.
	 * @param wrap
	 */
	protected ContentValues mediaComposeValues(MediaWrapper wrap) {
		ContentValues cv = new ContentValues();
		cv.put(IncidentContentDescriptor.Media.Cols.EVENT_ID, wrap.getEventId()); 
		cv.put(IncidentContentDescriptor.Media.Cols.DATA, wrap.getData());
		cv.put(IncidentContentDescriptor.Media.Cols.DATA_TYPE, wrap.getDataType()); 
		cv.put(IncidentContentDescriptor.Media.Cols.CREATED_DATE, wrap.getCreatedDate()); 
		cv.put(IncidentContentDescriptor.Media.Cols.MODIFIED_DATE, wrap.getModifiedDate()); 
		cv.put(IncidentContentDescriptor.Media.Cols._RECEIVED_DATE, wrap.get_ReceivedDate());
		cv.put(IncidentContentDescriptor.Media.Cols._DISPOSITION, wrap.get_Disposition());
		return cv;   
	}



	/**
	 * This method is provided with the express purpose of being overridden and extended.
	 *
	 *    StringBuilder sb = new StringBuilder();
	 *    sb.append("\""+IncidentContentDescriptor.Event.Cols.FUNCTION_CODE+"\" = '"+ wrap.getFunctionCode()+"'"); 
	 *    return sb.toString();   
	 *
	 * @param wrap
	 */
	protected String eventSelectKeyClause(EventWrapper wrap) {
		return null;
	}

	/**
	 * This method is provided with the express purpose of being overridden and extended.
	 * @param wrap
	 */
	protected ContentValues eventComposeValues(EventWrapper wrap) {
		ContentValues cv = new ContentValues();
		cv.put(IncidentContentDescriptor.Event.Cols.UUID, wrap.getUuid()); 
		cv.put(IncidentContentDescriptor.Event.Cols.MEDIA_COUNT, wrap.getMediaCount()); 
		cv.put(IncidentContentDescriptor.Event.Cols.ORIGINATOR, wrap.getOriginator()); 
		cv.put(IncidentContentDescriptor.Event.Cols.DISPLAY_NAME, wrap.getDisplayName()); 
		cv.put(IncidentContentDescriptor.Event.Cols.CATEGORY_ID, wrap.getCategoryId()); 
		cv.put(IncidentContentDescriptor.Event.Cols.TITLE, wrap.getTitle()); 
		cv.put(IncidentContentDescriptor.Event.Cols.DESCRIPTION, wrap.getDescription()); 
		cv.put(IncidentContentDescriptor.Event.Cols.LONGITUDE, wrap.getLongitude()); 
		cv.put(IncidentContentDescriptor.Event.Cols.LATITUDE, wrap.getLatitude()); 
		cv.put(IncidentContentDescriptor.Event.Cols.CREATED_DATE, wrap.getCreatedDate()); 
		cv.put(IncidentContentDescriptor.Event.Cols.MODIFIED_DATE, wrap.getModifiedDate()); 
		cv.put(IncidentContentDescriptor.Event.Cols.CID, wrap.getCid()); 
		cv.put(IncidentContentDescriptor.Event.Cols.CATEGORY, wrap.getCategory()); 
		cv.put(IncidentContentDescriptor.Event.Cols.UNIT, wrap.getUnit()); 
		cv.put(IncidentContentDescriptor.Event.Cols.SIZE, wrap.getSize()); 
		cv.put(IncidentContentDescriptor.Event.Cols.DEST_GROUP_TYPE, wrap.getDestGroupType()); 
		cv.put(IncidentContentDescriptor.Event.Cols.DEST_GROUP_NAME, wrap.getDestGroupName()); 
		cv.put(IncidentContentDescriptor.Event.Cols.STATUS, wrap.getStatus()); 
		cv.put(IncidentContentDescriptor.Event.Cols._RECEIVED_DATE, wrap.get_ReceivedDate());
		cv.put(IncidentContentDescriptor.Event.Cols._DISPOSITION, wrap.get_Disposition());
		return cv;   
	}



	/**
	 * This method is provided with the express purpose of being overridden and extended.
	 *
	 *    StringBuilder sb = new StringBuilder();
	 *    sb.append("\""+IncidentContentDescriptor.Category.Cols.FUNCTION_CODE+"\" = '"+ wrap.getFunctionCode()+"'"); 
	 *    return sb.toString();   
	 *
	 * @param wrap
	 */
	protected String categorySelectKeyClause(CategoryWrapper wrap) {
		return null;
	}

	/**
	 * This method is provided with the express purpose of being overridden and extended.
	 * @param wrap
	 */
	protected ContentValues categoryComposeValues(CategoryWrapper wrap) {
		ContentValues cv = new ContentValues();
		cv.put(IncidentContentDescriptor.Category.Cols.MAIN_CATEGORY, wrap.getMainCategory()); 
		cv.put(IncidentContentDescriptor.Category.Cols.SUB_CATEGORY, wrap.getSubCategory()); 
		cv.put(IncidentContentDescriptor.Category.Cols.TIGR_ID, wrap.getTigrId()); 
		cv.put(IncidentContentDescriptor.Category.Cols.ICON, wrap.getIcon());
		cv.put(IncidentContentDescriptor.Category.Cols.ICON_TYPE, wrap.getIconType()); 
		cv.put(IncidentContentDescriptor.Category.Cols._RECEIVED_DATE, wrap.get_ReceivedDate());
		cv.put(IncidentContentDescriptor.Category.Cols._DISPOSITION, wrap.get_Disposition());
		return cv;   
	}



	interface IMyWriter {
		public long meta(StringBuilder sb);
		public long payload(long rowId, String label, byte[] buf);
	}

	static final int READING_META = 0;
	static final int READING_LABEL = 1;
	static final int READING_PAYLOAD_SIZE = 2;
	static final int READING_PAYLOAD = 3;
	static final int READING_PAYLOAD_CHECK = 4;

	protected long deserializer(File file, IMyWriter writer) {
		logger.debug("::deserializer");
		InputStream ins;
		try {
			ins = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			return -1;
		}
		BufferedInputStream bufferedInput = new BufferedInputStream(ins);
		byte[] buffer = new byte[1024];
		StringBuilder sb = new StringBuilder();
		long rowId = -1;
		String label = "";
		byte[] payloadSizeBuf = new byte[4];
		int payloadSize = 0;
		byte[] payloadBuf = null;
		int payloadPos = 0;
		try {
			int bytesBuffered = bufferedInput.read(buffer);
			int bufferPos = 0;
			int state = READING_META;
			boolean eod = false;
			while (bytesBuffered > -1) {
				if (bytesBuffered == bufferPos) { 
					bytesBuffered = bufferedInput.read(buffer);
					bufferPos = 0; // reset buffer position
				}
				if (bytesBuffered < 0) eod = true;

				switch (state) {
				case READING_META:
					if (eod) {
						writer.meta(sb);
						break;
					}
					for (; bytesBuffered > bufferPos; bufferPos++) {
						byte b = buffer[bufferPos];
						if (b == '\0') {
							bufferPos++;
							state = READING_LABEL;
							rowId = writer.meta(sb);
							sb = new StringBuilder();
							break;
						}
						sb.append((char)b);
					}
					break;
				case READING_LABEL:
					if (eod)  break;

					for (; bytesBuffered > bufferPos; bufferPos++) {
						byte b = buffer[bufferPos];
						if (b == '\0') {
							label = sb.toString();
							bufferPos++;
							state = READING_PAYLOAD_SIZE;
							payloadPos = 0;
							break;
						}
						sb.append((char)b);
					}
					break;
				case READING_PAYLOAD_SIZE:
					if ((bytesBuffered - bufferPos) < (payloadSizeBuf.length - payloadPos)) { 
						// buffer doesn't contain the last byte of the length
						for (; bytesBuffered > bufferPos; bufferPos++, payloadPos++) { 
							payloadSizeBuf[payloadPos] = buffer[bufferPos];
						}
					} else {
						// buffer contains the last byte of the length
						for (; payloadSizeBuf.length > payloadPos; bufferPos++, payloadPos++) { 
							payloadSizeBuf[payloadPos] = buffer[bufferPos];
						}
						ByteBuffer dataSizeBuf = ByteBuffer.wrap(payloadSizeBuf);
						dataSizeBuf.order(ByteOrder.LITTLE_ENDIAN);
						payloadSize = dataSizeBuf.getInt();
						payloadBuf = new byte[payloadSize];
						payloadPos = 0;
						state = READING_PAYLOAD;
					}
					break;
				case READING_PAYLOAD:
					if ((bytesBuffered - bufferPos) < (payloadSize - payloadPos)) { 
						for (; bytesBuffered > bufferPos; bufferPos++, payloadPos++) { 
							payloadBuf[payloadPos] = buffer[bufferPos];
						}
					} else {
						for (; payloadSize > payloadPos; bufferPos++, payloadPos++) { 
							payloadBuf[payloadPos] = buffer[bufferPos];
						}

						payloadPos = 0;
						state = READING_PAYLOAD_CHECK;
					}
					break;
				case READING_PAYLOAD_CHECK:
					if ((bytesBuffered - bufferPos) < (payloadSizeBuf.length - payloadPos)) { 
						for (; bytesBuffered > bufferPos; bufferPos++, payloadPos++) { 
							payloadSizeBuf[payloadPos] = buffer[bufferPos];
						}
					} else {
						for (; payloadSizeBuf.length > payloadPos; bufferPos++, payloadPos++) { 
							payloadSizeBuf[payloadPos] = buffer[bufferPos];
						}
						ByteBuffer dataSizeBuf = ByteBuffer.wrap(payloadSizeBuf);
						dataSizeBuf.order(ByteOrder.LITTLE_ENDIAN);
						if (payloadSize != dataSizeBuf.getInt()) {
							logger.error("message garbled {} {}", payloadSize, dataSizeBuf.getInt());
							state = READING_LABEL;
							break;
						} 
						writer.payload(rowId, label, payloadBuf);
						state = READING_LABEL;
					}
					break;
				}
			}
			bufferedInput.close();
		} catch (IOException e) {
			logger.error("could not read serialized file");
			return -1;
		}
		return rowId;
	}

	//@Override 
	public ArrayList<File> mediaSerialize(Cursor cursor) {
		logger.debug( "::mediaSerialize");
		ArrayList<File> paths = new ArrayList<File>();      
		if (1 > cursor.getCount()) return paths;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream eos = new DataOutputStream(baos);

		for (boolean more = cursor.moveToFirst(); more; more = cursor.moveToNext()) {
			MediaWrapper iw = new MediaWrapper();
			iw.setEventId(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Media.Cols.EVENT_ID)));  
			iw.setDataType(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Media.Cols.DATA_TYPE))); 
			iw.setData(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Media.Cols.DATA)));  
			iw.setCreatedDate(cursor.getLong(cursor.getColumnIndex(IncidentContentDescriptor.Media.Cols.CREATED_DATE)));  
			iw.setModifiedDate(cursor.getLong(cursor.getColumnIndex(IncidentContentDescriptor.Media.Cols.MODIFIED_DATE)));  
			iw.set_ReceivedDate(cursor.getLong(cursor.getColumnIndex(IncidentContentDescriptor.Media.Cols._RECEIVED_DATE))); 
			iw.set_Disposition(cursor.getInt(cursor.getColumnIndex(IncidentContentDescriptor.Media.Cols._DISPOSITION))); 

			Gson gson = new Gson();

			try {
				eos.writeBytes(gson.toJson(iw));
				eos.writeByte(0);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			// not a reference field name :event id eventId event_id\n 
			try {
				String fileName = iw.getData(); 
				File dataFile = new File(fileName);
				int dataSize = (int)dataFile.length();
				byte[] buffData = new byte[dataSize];
				FileInputStream fileStream = new FileInputStream(dataFile);
				int ret = 0;   
				for (int position = 0; (ret > -1 && dataSize > position); position += ret) {
					ret = fileStream.read(buffData, position, dataSize - position);
				}
				fileStream.close();

				eos.writeBytes("data"); 
				eos.writeByte(0);

				ByteBuffer dataSizeBuf = ByteBuffer.allocate(Integer.SIZE/Byte.SIZE);
				dataSizeBuf.order(ByteOrder.LITTLE_ENDIAN);
				dataSizeBuf.putInt(dataSize);

				// write the media back out
				eos.write(dataSizeBuf.array());
				eos.write(buffData);
				eos.write(dataSizeBuf.array());
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			} 
			// not a reference field name :created date createdDate created_date\n 
			// not a reference field name :modified date modifiedDate modified_date\n 
			// IncidentContentDescriptor.Media.Cols._DISPOSITION;

			//           try {
			// TODO write to content provider using openFile
			// if (!applCacheMediaDir.exists() ) applCacheMediaDir.mkdirs();

			// File outfile = new File(applCacheMediaDir, Integer.toHexString((int) System.currentTimeMillis())); 
			//              BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(outfile), 8192);
			//              bufferedOutput.write(baos.toByteArray());
			//              bufferedOutput.flush();
			//              bufferedOutput.close();


			//           } catch (FileNotFoundException e) {
			//              e.printStackTrace();
			//           } catch (IOException e) {
			//              e.printStackTrace();
			//           }
		}
		return paths;
	} 
	//@Override 
	public ArrayList<File> eventSerialize(Cursor cursor) {
		logger.debug( "::eventSerialize");
		ArrayList<File> paths = new ArrayList<File>();      
		if (1 > cursor.getCount()) return paths;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream eos = new DataOutputStream(baos);

		for (boolean more = cursor.moveToFirst(); more; more = cursor.moveToNext()) {
			EventWrapper iw = new EventWrapper();
			iw.setUuid(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.UUID)));  
			iw.setMediaCount(cursor.getInt(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.MEDIA_COUNT)));  
			iw.setOriginator(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.ORIGINATOR)));  
			iw.setDisplayName(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.DISPLAY_NAME)));  
			iw.setCategoryId(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.CATEGORY_ID)));  
			iw.setTitle(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.TITLE)));  
			iw.setDescription(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.DESCRIPTION)));  
			iw.setLongitude(Util.scaleIntCoordinate(cursor.getInt(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.LONGITUDE))));  
			iw.setLatitude(Util.scaleIntCoordinate(cursor.getInt(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.LATITUDE))));  
			iw.setCreatedDate(cursor.getLong(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.CREATED_DATE)));  
			iw.setModifiedDate(cursor.getLong(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.MODIFIED_DATE)));  
			iw.setCid(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.CID)));  
			iw.setCategory(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.CATEGORY)));  
			iw.setUnit(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.UNIT)));  
			iw.setSize(cursor.getLong(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.SIZE)));  
			iw.setDestGroupType(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.DEST_GROUP_TYPE)));  
			iw.setDestGroupName(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.DEST_GROUP_NAME)));  
			iw.setStatus(cursor.getInt(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols.STATUS)));  
			iw.set_ReceivedDate(cursor.getLong(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols._RECEIVED_DATE))); 
			iw.set_Disposition(cursor.getInt(cursor.getColumnIndex(IncidentContentDescriptor.Event.Cols._DISPOSITION))); 

			Gson gson = new Gson();

			try {
				eos.writeBytes(gson.toJson(iw));
				eos.writeByte(0);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			// not a reference field name :uuid uuid uuid\n 
			// not a reference field name :media count mediaCount media_count\n 
			// not a reference field name :originator originator originator\n 
			// not a reference field name :display name displayName display_name\n 
			// not a reference field name :category id categoryId category_id\n 
			// not a reference field name :title title title\n 
			// not a reference field name :description description description\n 
			// not a reference field name :longitude longitude longitude\n 
			// not a reference field name :latitude latitude latitude\n 
			// not a reference field name :created date createdDate created_date\n 
			// not a reference field name :modified date modifiedDate modified_date\n 
			// not a reference field name :cid cid cid\n 
			// not a reference field name :category category category\n 
			// not a reference field name :unit unit unit\n 
			// not a reference field name :size size size\n 
			// not a reference field name :dest group type destGroupType dest_group_type\n 
			// not a reference field name :dest group name destGroupName dest_group_name\n 
			// not a reference field name :STATUS status status\n 
			// IncidentContentDescriptor.Event.Cols._DISPOSITION;

			//           try {
			// TODO write to content provider using openFile
			// if (!applCacheEventDir.exists() ) applCacheEventDir.mkdirs();

			// File outfile = new File(applCacheEventDir, Integer.toHexString((int) System.currentTimeMillis())); 
			//              BufferedOutputStream bufferedOutput = 
			//            		  new BufferedOutputStream(new FileOutputStream(outfile), 8192);
			//              bufferedOutput.write(baos.toByteArray());
			//              bufferedOutput.flush();
			//              bufferedOutput.close();

			// paths.add(outfile);
			//           } catch (FileNotFoundException e) {
			//              e.printStackTrace();
			//           } catch (IOException e) {
			//              e.printStackTrace();
			//           }
		}
		return paths;
	} 
	//@Override 
	public ArrayList<File> categorySerialize(Cursor cursor) {
		logger.debug( "::categorySerialize");
		ArrayList<File> paths = new ArrayList<File>();      
		if (1 > cursor.getCount()) return paths;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream eos = new DataOutputStream(baos);

		for (boolean more = cursor.moveToFirst(); more; more = cursor.moveToNext()) {
			CategoryWrapper iw = new CategoryWrapper();
			iw.setMainCategory(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Category.Cols.MAIN_CATEGORY)));  
			iw.setSubCategory(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Category.Cols.SUB_CATEGORY)));  
			iw.setTigrId(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Category.Cols.TIGR_ID)));  
			iw.setIconType(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Category.Cols.ICON_TYPE))); 
			iw.setIcon(cursor.getString(cursor.getColumnIndex(IncidentContentDescriptor.Category.Cols.ICON)));  
			iw.set_ReceivedDate(cursor.getLong(cursor.getColumnIndex(IncidentContentDescriptor.Category.Cols._RECEIVED_DATE))); 
			iw.set_Disposition(cursor.getInt(cursor.getColumnIndex(IncidentContentDescriptor.Category.Cols._DISPOSITION))); 

			Gson gson = new Gson();

			try {
				eos.writeBytes(gson.toJson(iw));
				eos.writeByte(0);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			// not a reference field name :main category mainCategory main_category\n 
			// not a reference field name :sub category subCategory sub_category\n 
			// not a reference field name :tigr id tigrId tigr_id\n 
			try {
				String fileName = iw.getIcon(); 
				File dataFile = new File(fileName);
				int dataSize = (int)dataFile.length();
				byte[] buffData = new byte[dataSize];
				FileInputStream fileStream = new FileInputStream(dataFile);
				int ret = 0;   
				for (int position = 0; (ret > -1 && dataSize > position); position += ret) {
					ret = fileStream.read(buffData, position, dataSize - position);
				}
				fileStream.close();

				eos.writeBytes("icon"); 
				eos.writeByte(0);

				ByteBuffer dataSizeBuf = ByteBuffer.allocate(Integer.SIZE/Byte.SIZE);
				dataSizeBuf.order(ByteOrder.LITTLE_ENDIAN);
				dataSizeBuf.putInt(dataSize);

				// write the category back out
				eos.write(dataSizeBuf.array());
				eos.write(buffData);
				eos.write(dataSizeBuf.array());
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			} 
			// IncidentContentDescriptor.Category.Cols._DISPOSITION;

			//           try {
			//              if (!applCacheCategoryDir.exists() ) applCacheCategoryDir.mkdirs();
			//              
			//              File outfile = new File(applCacheCategoryDir, Integer.toHexString((int) System.currentTimeMillis())); 
			//              BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(outfile), 8192);
			//              bufferedOutput.write(baos.toByteArray());
			//              bufferedOutput.flush();
			//              bufferedOutput.close();
			//           
			//              paths.add(outfile);
			//           } catch (FileNotFoundException e) {
			//              e.printStackTrace();
			//           } catch (IOException e) {
			//              e.printStackTrace();
			//           }
		}
		return paths;
	} 

	class mediaDeserializer implements IMyWriter {

		@Override
		public long meta(StringBuilder sb) {
			String json = sb.toString();
			Gson gson = new Gson();
			MediaWrapper wrap = null;
			try {
				wrap = gson.fromJson(json, MediaWrapper.class);
			} catch (JsonParseException ex) {
				ex.getMessage();
				ex.printStackTrace();
				return -1;
			} catch (java.lang.RuntimeException ex) {
				ex.getMessage();
				ex.printStackTrace();
				return -1;
			}
			if (wrap == null) return -1;

			//SQLiteDatabase db = openHelper.getReadableDatabase();

			ContentValues cv = mediaComposeValues(wrap);
			// Put the current system time into the received column for relative time pulls.
			cv.put(IncidentContentDescriptor.Media.Cols._RECEIVED_DATE, System.currentTimeMillis());
			// String whereClause = mediaSelectKeyClause(wrap);

			//         if (whereClause != null) {
			//            // Switch on the path in the uri for what we want to query.
			//            Cursor updateCursor = db.query(Tables.MEDIA_TBL, mediaProjectionKey, whereClause, null, null, null, null);
			//            long rowId = -1;
			//            for (boolean more = updateCursor.moveToFirst(); more;)
			//            {
			//                rowId = updateCursor.getLong(updateCursor.getColumnIndex(IncidentContentDescriptor.Media.Cols._ID));  
			// 
			//                db.update(Tables.MEDIA_TBL, cv, 
			//                       "\""+IncidentContentDescriptor.Media.Cols._ID+"\" = '"+ Long.toString(rowId)+"'",
			//                        null); 
			//                break;
			//            }
			//            updateCursor.close();
			//            if (rowId > 0) {
			//                getContext().getContentResolver().notifyChange(IncidentContentDescriptor.Media.Cols.CONTENT_URI, null); 
			//                return rowId;
			//            }
			//         }
			//long rowId = db.insert(Tables.MEDIA_TBL, 
			//         IncidentContentDescriptor.Media.Cols.EVENT_ID,
			//         cv);
			Uri rowUri = getContext().getContentResolver().insert(IncidentContentDescriptor.Media.CONTENT_URI, cv);
			long rowId = Long.valueOf(rowUri.getLastPathSegment()).longValue();

			getContext().getContentResolver().notifyChange(IncidentContentDescriptor.Media.CONTENT_URI, null); 
			return rowId;
		}

		@Override
		public long payload(long rowId, String label, byte[] buf) {
			ContentResolver cr = getContext().getContentResolver();
			Uri rowUri = ContentUris.withAppendedId(IncidentContentDescriptor.Media.CONTENT_URI, rowId);
			Cursor cursor = cr.query(rowUri, null, null, null, null);
			cursor.moveToFirst();
			String filename = cursor.getString(cursor.getColumnIndex(label));  
			cursor.close();
			File dataFile = new File(filename);
			File dataDir = dataFile.getParentFile();
			if (!dataDir.exists()) {
				dataDir.mkdirs();
			}
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(dataFile);
			} catch (FileNotFoundException e) {
				return -1;
			}
			try {
				fos.write(buf);
				fos.close();
			} catch (IOException e) {
				return -1;
			}
			return 0;
		}
	}

	public long mediaDeserialize(File file) {
		return this.deserializer(file, new mediaDeserializer());
	} 

	class eventDeserializer implements IMyWriter {

		@Override
		public long meta(StringBuilder sb) {
			String json = sb.toString();
			Gson gson = new Gson();
			EventWrapper wrap = null;
			try {
				wrap = gson.fromJson(json, EventWrapper.class);
			} catch (JsonParseException ex) {
				ex.getMessage();
				ex.printStackTrace();
				return -1;
			} catch (java.lang.RuntimeException ex) {
				ex.getMessage();
				ex.printStackTrace();
				return -1;
			}
			if (wrap == null) return -1;

			//SQLiteDatabase db = openHelper.getReadableDatabase();

			ContentValues cv = eventComposeValues(wrap);
			// Put the current system time into the received column for relative time pulls.
			cv.put(IncidentContentDescriptor.Event.Cols._RECEIVED_DATE, System.currentTimeMillis());
			// String whereClause = eventSelectKeyClause(wrap);

			//         if (whereClause != null) {
			//            // Switch on the path in the uri for what we want to query.
			//            Cursor updateCursor = db.query(Tables.EVENT_TBL, eventProjectionKey, whereClause, null, null, null, null);
			//            long rowId = -1;
			//            for (boolean more = updateCursor.moveToFirst(); more;)
			//            {
			//                rowId = updateCursor.getLong(updateCursor.getColumnIndex(IncidentContentDescriptor.Event.Cols._ID));  
			// 
			//                db.update(Tables.EVENT_TBL, cv, 
			//                       "\""+IncidentContentDescriptor.Event.Cols._ID+"\" = '"+ Long.toString(rowId)+"'",
			//                        null); 
			//                break;
			//            }
			//            updateCursor.close();
			//            if (rowId > 0) {
			//                getContext().getContentResolver().notifyChange(IncidentContentDescriptor.Event.Cols.CONTENT_URI, null); 
			//                return rowId;
			//            }
			//         }
			//long rowId = db.insert(Tables.EVENT_TBL, 
			//         IncidentContentDescriptor.Event.Cols.UUID,
			//         cv);
			Uri rowUri = getContext().getContentResolver().insert(IncidentContentDescriptor.Event.CONTENT_URI, cv);
			long rowId = Long.valueOf(rowUri.getLastPathSegment()).longValue();

			getContext().getContentResolver().notifyChange(IncidentContentDescriptor.Event.CONTENT_URI, null); 
			return rowId;
		}

		@Override
		public long payload(long rowId, String label, byte[] buf) {
			ContentResolver cr = getContext().getContentResolver();
			Uri rowUri = ContentUris.withAppendedId(IncidentContentDescriptor.Event.CONTENT_URI, rowId);
			Cursor cursor = cr.query(rowUri, null, null, null, null);
			cursor.moveToFirst();
			String filename = cursor.getString(cursor.getColumnIndex(label));  
			cursor.close();
			File dataFile = new File(filename);
			File dataDir = dataFile.getParentFile();
			if (!dataDir.exists()) {
				dataDir.mkdirs();
			}
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(dataFile);
			} catch (FileNotFoundException e) {
				return -1;
			}
			try {
				fos.write(buf);
				fos.close();
			} catch (IOException e) {
				return -1;
			}
			return 0;
		}
	}

	public long eventDeserialize(File file) {
		return this.deserializer(file, new eventDeserializer());
	} 

	class categoryDeserializer implements IMyWriter {

		@Override
		public long meta(StringBuilder sb) {
			String json = sb.toString();
			Gson gson = new Gson();
			CategoryWrapper wrap = null;
			try {
				wrap = gson.fromJson(json, CategoryWrapper.class);
			} catch (JsonParseException ex) {
				ex.getMessage();
				ex.printStackTrace();
				return -1;
			} catch (java.lang.RuntimeException ex) {
				ex.getMessage();
				ex.printStackTrace();
				return -1;
			}
			if (wrap == null) return -1;

			//SQLiteDatabase db = openHelper.getReadableDatabase();

			ContentValues cv = categoryComposeValues(wrap);
			// Put the current system time into the received column for relative time pulls.
			cv.put(IncidentContentDescriptor.Category.Cols._RECEIVED_DATE, System.currentTimeMillis());
			// String whereClause = categorySelectKeyClause(wrap);

			//         if (whereClause != null) {
			//            // Switch on the path in the uri for what we want to query.
			//            Cursor updateCursor = db.query(Tables.CATEGORY_TBL, categoryProjectionKey, whereClause, null, null, null, null);
			//            long rowId = -1;
			//            for (boolean more = updateCursor.moveToFirst(); more;)
			//            {
			//                rowId = updateCursor.getLong(updateCursor.getColumnIndex(IncidentContentDescriptor.Category.Cols._ID));  
			// 
			//                db.update(Tables.CATEGORY_TBL, cv, 
			//                       "\""+IncidentContentDescriptor.Category.Cols._ID+"\" = '"+ Long.toString(rowId)+"'",
			//                        null); 
			//                break;
			//            }
			//            updateCursor.close();
			//            if (rowId > 0) {
			//                getContext().getContentResolver().notifyChange(IncidentContentDescriptor.Category.CONTENT_URI, null); 
			//                return rowId;
			//            }
			//         }
			//long rowId = db.insert(Tables.CATEGORY_TBL, 
			//         IncidentContentDescriptor.Category.Cols.MAIN_CATEGORY,
			//         cv);
			Uri rowUri = getContext().getContentResolver().insert(IncidentContentDescriptor.Category.CONTENT_URI, cv);
			long rowId = Long.valueOf(rowUri.getLastPathSegment()).longValue();

			getContext().getContentResolver().notifyChange(IncidentContentDescriptor.Category.CONTENT_URI, null); 
			return rowId;
		}

		@Override
		public long payload(long rowId, String label, byte[] buf) {
			ContentResolver cr = getContext().getContentResolver();
			Uri rowUri = ContentUris.withAppendedId(IncidentContentDescriptor.Category.CONTENT_URI, rowId);
			Cursor cursor = cr.query(rowUri, null, null, null, null);
			cursor.moveToFirst();
			String filename = cursor.getString(cursor.getColumnIndex(label));  
			cursor.close();
			File dataFile = new File(filename);
			File dataDir = dataFile.getParentFile();
			if (!dataDir.exists()) {
				dataDir.mkdirs();
			}
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(dataFile);
			} catch (FileNotFoundException e) {
				return -1;
			}
			try {
				fos.write(buf);
				fos.close();
			} catch (IOException e) {
				return -1;
			}
			return 0;
		}
	}

	public long categoryDeserialize(File file) {
		return this.deserializer(file, new categoryDeserializer());
	} 



}
