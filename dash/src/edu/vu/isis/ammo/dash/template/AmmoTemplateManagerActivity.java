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
package edu.vu.isis.ammo.dash.template;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import edu.vu.isis.ammo.dash.DashAbstractActivity;
import edu.vu.isis.ammo.dash.R;
import edu.vu.isis.ammo.dash.WorkflowLogger;
import edu.vu.isis.ammo.dash.template.model.Record;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Main activity for DashTemplate. This activity lets a user select a template
 * (if none selected) and serialization of template data (actual parsing is
 * off-loaded to AmmoParser).
 * 
 * @author demetri
 * 
 */
public class AmmoTemplateManagerActivity extends DashAbstractActivity {

	private static final Logger logger = LoggerFactory
			.getLogger("class.TemplateManagerActivity");

	private static final String BUNDLE_DATA = "data";
	private long prevButtonTimestamp = 0;

	private TemplateView templateView;
	private boolean isTemplateLoaded = false;

	public static final String TEMPLATE_EXTRA = "TEMPLATE";
	public static final String JSON_DATA_EXTRA = "JSON_DATA";
	public static final String TEXT_DATA_EXTRA = "TEXT_DATA";
	public static final String LOCATION_EXTRA = "LOCATION_EXTRA";
	public static final String LOCATION_FIELD_ID_EXTRA = "LOCATION_FIELD_ID";
	public static final String TEMPLATE_NAME_KEY = "TEMPLATE_NAME";

	// =============================
	// Lifecycle
	// =============================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WorkflowLogger.log("AmmoTemplateManagerActivity - onCreate");
	}

	@Override
	public int getContentViewResourceId() {
		return R.layout.template_manager_activity;
	}

	@Override
	public void onBackPressed() {
		logger.debug("onBackPressed Called");
		long curTime = System.currentTimeMillis();
		if (curTime - prevButtonTimestamp <= 3500) {
			super.onBackPressed();
		} else {
			String msg = "Press back again to cancel";
			if (!isOpenForEdit()) {
				msg = "Press back again to leave template";
			}
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		}
		prevButtonTimestamp = curTime;
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		Record data = fromJson(bundle.getString(BUNDLE_DATA));
		templateView.setData(data);
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putString(BUNDLE_DATA, toJson(templateView.getData()));
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
	    super.onRetainNonConfigurationInstance();
	    if (isTemplateLoaded) {
    	    return toJson(templateView.getData());
	    } else {
	        return null;
	    }
	}

	@Override
	protected void toModel() {
		super.toModel();
		model.setDescription(templateView.getTemplateDisplayName());
		model.setTemplateData(toJson(templateView.getData()));
		
		Location loc = null;
		if (templateView.locationView != null) {
			loc = templateView.locationView.getLocation();
		} else {
			// Use the location of the device 
			// (Otherwise a lat,lon of (0,0) is reported, which is undesirable)
			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if(lm != null) {
				loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}
		}
		
		model.setLocation(loc);

	}

	@Override
	protected void fromModel() {
		super.fromModel();
		templateView.setData(fromJson(model.getTemplateData()));
	}

	// =============================
	// UI Management
	// =============================
	// Removes the file type extension from each array entry and converts to all
	// caps.
	private static String[] stripExtensionFromStringArray(String[] array) {
		String[] formattedArray = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			int index = array[i].lastIndexOf(".");
			formattedArray[i] = array[i].substring(0, index).toUpperCase();
		}
		return formattedArray;
	}

	/**
	 * Called after setContentView. Don't forget super.setupView();
	 */
	@Override
	protected void setupView() {
		super.setupView();
		WorkflowLogger.log("AmmoTemplateManagerActivity - setting up view");
		findViewById(R.id.saveButton).setVisibility(getEditVisibility());
		findViewById(R.id.cameraButton).setVisibility(getEditVisibility());
		findViewById(R.id.audioButton).setVisibility(getEditVisibility());

		templateView = new TemplateView(this, isOpenForEdit());

		String templateFilename = getIntent().getStringExtra(TEMPLATE_EXTRA);
		final Location location = getIntent()
				.getParcelableExtra(LOCATION_EXTRA);
		String jsonData = getIntent().getStringExtra(JSON_DATA_EXTRA);
		if (jsonData == null) {
		    // Try to get json from a configuration change
		    jsonData = (String) getLastNonConfigurationInstance();
		}

		if (jsonData != null && jsonData.length() != 0) {
			if (!templateView.loadTemplateFromJson(jsonData, location)) {
				finish();
			} else {
			    isTemplateLoaded = true;
			}

		} else if (templateFilename != null) {
			if (!templateView.loadTemplate(templateFilename, location)) {
				finish();
			} else {
			    isTemplateLoaded = true;
			}
		} else {

			final String[] displayTemplates = stripExtensionFromStringArray(getTemplateFiles(this));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick a form");
			builder.setItems(displayTemplates,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int item) {
							// Get the template based on item position.
							String[] templates = getTemplateFiles(AmmoTemplateManagerActivity.this);
							if (!templateView.loadTemplate(templates[item],
									location)) {
								finish();
							} else {
							    isTemplateLoaded = true;
							    templateView.getData().setField(TEMPLATE_NAME_KEY,
									templates[item]);
							    ((TextView) findViewById(R.id.ammo_template_manager_label))
									.setText(templateView
											.getTemplateDisplayName());
							}
						}
					});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

		// Set the header for the template.
		((TextView) findViewById(R.id.ammo_template_manager_label))
				.setText(templateView.getTemplateDisplayName());
		ViewGroup viewRoot = (ViewGroup) findViewById(R.id.ammo_template_manager_root);
		viewRoot.addView(templateView);
	}

	public static String[] getTemplateFiles(Context context) {
		checkFiles(context);
		return getTemplateFilesDoNotCheck();
	}

	public static String toJson(Record data) {
		return new Gson().toJson(data);
	}

	public static Record fromJson(String json) {
		return new Gson().fromJson(json, Record.class);
	}

	/**
	 * Gets the template files from assets and copies them to the sd card. We
	 * copy to the SD card for convenience purposes and it also allows us to
	 * display all templates in a list.
	 * 
	 * @param context
	 */
	/* package */static void checkFiles(Context context) {
		String[] templates = getTemplateAssets(context);
		for (String s : templates) {
			try {
				InputStream source = context.getAssets().open("templates/" + s);
				File destination = new File(DashAbstractActivity.TEMPLATE_DIR, s);

				// We always want to copy to the sdcard so any changes to the
				// templates in assets get pushed to the sdcard.
				// if(!toFile.exists()) {
				// FileUtils.copyInputStreamToFile(fromStream, toFile);
				try {
					if (destination.exists()) {
						if (destination.isDirectory()) {
							throw new IOException("File '" + destination
									+ "' exists but is a directory");
						}
						if (destination.canWrite() == false) {
							throw new IOException("File '" + destination
									+ "' cannot be written to");
						}
					} else {
						File parent = destination.getParentFile();
						if (parent != null) {
							if (!parent.mkdirs() && !parent.isDirectory()) {
								throw new IOException("Directory '" + parent
										+ "' could not be created");
							}
						}
					}
					final FileOutputStream output = new FileOutputStream(
							destination, false);
					try {
						copy(source, output);
					} finally {
						closeQuietly(output);
					}
				} finally {
					closeQuietly(source);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void closeQuietly(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (IOException ioe) {
			// ignore
		}
	}

	private static int copy(InputStream input, OutputStream output)
			throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	public static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * Pull the template files from the assets folders and return them.
	 * 
	 * @param context
	 * @return
	 */
	private static String[] getTemplateAssets(Context context) {
		String[] templates = null;
		try {
			templates = context.getAssets().list("templates");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return templates;
	}

	private static String[] getTemplateFilesDoNotCheck() {
		return DashAbstractActivity.TEMPLATE_DIR.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.toLowerCase().endsWith(".xml");
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		templateView.onActivityResult(requestCode, resultCode, data);
	}
}
