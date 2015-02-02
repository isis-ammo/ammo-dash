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
package edu.vu.isis.ammo.dash.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import edu.vu.isis.ammo.dash.provider.IncidentSchemaBase.CategoryTableSchemaBase;

/**
 * Extends the basic incident provider with duplicate detection for reports.
 * 
 * In a previous lifetime, we also had categories associated with reports though
 * that functionality has since been removed.
 * 
 */
public class IncidentProvider extends IncidentProviderBase {

	private static final Logger logger = LoggerFactory.getLogger("class.IncidentProvider");

	public static final String MAIN_ICON = "MainIcon";
	public static final int RESULT_CATEGORY_LOAD_SUCCESS = 1;
	public static final int RESULT_CATEGORY_LOAD_FAIL = -1;
	public static final String EXTRA_LOAD_RESULT = "load_result";

	protected class IncidentDatabaseHelper extends IncidentProviderBase.IncidentDatabaseHelper {
		protected IncidentDatabaseHelper(Context context) {
			// super(context, IncidentSchema.DATABASE_VERSION);
			
			// Cause a memory based content provider to be used
			// super(context, (String) null, (SQLiteDatabase.CursorFactory) null, IncidentSchema.DATABASE_VERSION);
			// Use a file based content provider
			super(context, IncidentSchema.DATABASE_NAME, 
                              (SQLiteDatabase.CursorFactory) null, IncidentSchema.DATABASE_VERSION);
			logger.info("making a database {} {}", 
                              IncidentSchema.DATABASE_NAME, IncidentSchema.DATABASE_VERSION);
		}
	}

	// ===========================================================
	// Content Provider Overrides
	// ===========================================================

	@Override
	public boolean onCreate() {
		super.onCreate();
		
		/**
		 * Introduce a broadcast receiver to re-populate the category table.
		 */
		final BroadcastReceiver controller = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, Intent intent) {
				final IncidentProvider provider = IncidentProvider.this;
				final SQLiteDatabase db = provider.openHelper.getWritableDatabase();
				final PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra("pending_intent");
				// We should load the categories in a background thread.
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean success = provider.unzipAndPopulateCategories(db);
						Intent dummyIntent = new Intent("edu.vu.isis.ammo.dash.Dash.NONE");
						dummyIntent.putExtra(EXTRA_LOAD_RESULT, success);
						try {
							pendingIntent.send(context, RESULT_CATEGORY_LOAD_SUCCESS, dummyIntent);
						} catch (CanceledException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		};
		
		final IntentFilter filter = new IntentFilter();
		filter.addAction(IncidentSchema.CategoryTableSchema.RELOAD);
		
	    getContext().registerReceiver(controller, filter);
	    
	    
		return true;
	}

	
	@Override
	public boolean createDatabaseHelper() {
		this.openHelper = new IncidentDatabaseHelper(getContext()) {
			@Override
			protected void preloadTables(SQLiteDatabase db) {
				IncidentProvider.this.unzipAndPopulateCategories(db);
			}
		};
		return true;
	}

	// ===========================================================
	// Helper methods
	// ===========================================================

	/**
	 * /**
     * In a previous version of dash, a set of icons were present which
     * indicated the type of object for which the incident was being 
     * created.  This capability is no longer used so the tigr-icons.zip
     * file is no longer distributed.  This method is no longer used so it has a quick return.
     * <p>
     *
	 * Extract the category information and update the Category table. It is
	 * expected that the zip file containing this information be placed in the
	 * file "/sdcard/tigr_icons.zip".
	 * 
	 */
	public boolean unzipAndPopulateCategories(final SQLiteDatabase db) {
		try {
			// If the folder exists, blow it away and recreate it.
			IncidentProviderBase.clearBlobCache("tigr", "png");

			// Read our zip folder and write the contents to the sd card.
			// File format is as follows...
			// Main+Category_Sub+Category.fileExtension
			// + signs serve as spaces in file name, underscores separate main
			// and sub categories.
			final File file = new File(Environment.getExternalStorageDirectory(), "tigr_icons.zip");

			final ZipFile zf = new ZipFile(file);
			final ArrayList<? extends ZipEntry> list = Collections.list(zf.entries());

			// Clear the category table in preparation for reloading it.
			final int rowDeleteCount = db.delete(Tables.CATEGORY_TBL, null, null);
			logger.debug(String.valueOf(rowDeleteCount) + " rows deleted.");

			for (final Object obj : list) {
				final ZipEntry entry = (ZipEntry) obj;
				logger.debug(entry.getName());

				// Get the file data from the stream.
				final InputStream is = zf.getInputStream(entry);
				// long size = entry.getSize();
				final byte[] buffer = new byte[(int) entry.getSize()];
				int ret = 0;
				while (ret >= 0) {
					ret = is.read(buffer);
				}
				if (buffer.length < 1)
					continue;

				// Add entry to Content Provider.
				final ContentValues values = this.decodeIconFilePath(new File(entry.getName()));
				final long record = db.insert(Tables.CATEGORY_TBL, CategoryTableSchemaBase.MAIN_CATEGORY, values);

				final File filePath = blobFile("category", Integer.toString((int) record), "icon");
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(filePath);
					fos.write(buffer);
				} finally {
					if (fos != null) fos.close();
				}
			}

			return true;
		} catch (ZipException ex) {
			logger.error("could not open zip", ex);
		} catch (IOException ex) {
			logger.debug("could not open", ex);
		}
		return false;
	}

	/**
	 * Icon files are named as follows... Main+Category_Sub+Category.iconType
	 * 
	 * This method returns a hash map where the keys come from the category
	 * table schema. The encoding is parsed and encodings are removed and stored
	 * in the map.
	 * 
	 * @encoded the metadata for this icon encoded into a string
	 */
	private ContentValues decodeIconFilePath(File encoded) {
		final ContentValues values = new ContentValues();
		final String tempString = encoded.getName().replace("+", " ");

		final String[] categorySplitArray = tempString.split("_");
		if (categorySplitArray.length < 1) {
			logger.error("no parsable name provided" + tempString);
			return null;
		}
		final String mainCategory = categorySplitArray[0];
		String subCategory = MAIN_ICON;
		String iconType = "png";
		String tigrId = "";
		if (categorySplitArray.length > 1) {
			final String[] extensionSplitArray = categorySplitArray[1].split("\\.");
			if (extensionSplitArray.length > 0) {
				subCategory = extensionSplitArray[0];
				if (extensionSplitArray.length > 1) {
					tigrId = extensionSplitArray[1];
					iconType = extensionSplitArray[2];
				}
			}
		}

		values.put(CategoryTableSchemaBase.MAIN_CATEGORY, mainCategory);
		values.put(CategoryTableSchemaBase.SUB_CATEGORY, subCategory);
		values.put(CategoryTableSchemaBase.ICON_TYPE, iconType);
		values.put(CategoryTableSchemaBase.TIGR_ID, tigrId);
		// Uri uri = Uri.fromFile(encoded);
		values.put(CategoryTableSchemaBase.ICON, CategoryTableSchemaBase.ICON);

		return values;
	}

}
