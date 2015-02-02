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

import java.util.Arrays;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Creates a shortcut to
 * For more information on android shortcuts, read:
 * http://developer.android.com/reference/android/content/Intent.html#ACTION_CREATE_SHORTCUT
 */
public class DashCreateShortcut extends ListActivity {

	public static final String[] DASH_FIELD_NAMES = new String[]{"icon", "text1"};
	public static final int[] DASH_FIELD_RESOURCES = new int[]{android.R.id.icon, android.R.id.text1};
	private ShortcutRow templateRow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter();

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent data = ((ShortcutRow)parent.getItemAtPosition(position)).getData();
				
				if(((ShortcutRow)parent.getItemAtPosition(position)) == templateRow) {
					Intent intent = (Intent)data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
					if(intent != null) {
						startActivityForResult(intent, 0);
					}
					return;
				}
				
				//user selected, setResult and finish
				setResult(RESULT_OK, data);
				finish();
			}
		});

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_CANCELED) {
			setResult(RESULT_OK, data);
			finish();
		}
	}

	private void setListAdapter() {
		ShortcutRow cameraRow = new ShortcutRow(this, R.drawable.camera_button2, "Take pictures", "Camera", setupIntent(DashAbstractActivity.IMAGE_TYPE));
		ShortcutRow audioRow = new ShortcutRow(this, R.drawable.audio_button2, "Record audio", "Audio", setupIntent(DashAbstractActivity.AUDIO_TYPE));
		templateRow = new ShortcutRow(this, R.drawable.template_button2, "Fill in templates", "Template", setupCreateTemplateShortcut());

		setListAdapter(new SimpleAdapter(this, Arrays.asList(cameraRow, audioRow, templateRow),
				R.layout.pick_item, DASH_FIELD_NAMES, DASH_FIELD_RESOURCES));
	}

	private Intent setupIntent(int dashMode) {
		return new Intent(this, Dash.class).putExtra(DashAbstractActivity.MODE, dashMode);
	}
	
	private Intent setupCreateTemplateShortcut() {
		return new Intent(this, DashCreateTemplateShortcut.class);
	}
	
	static class ShortcutRow extends HashMap<String, Object> {
		private static final long serialVersionUID = 1L;
		private Intent data = new Intent();
		/**
		 * @param icon the icon to display in the list and the final icon
		 * @param listText the text to put in the list
		 * @param shortcutText the text to put on the icon
		 * @param shortcutIntent the intent to start
		 */
		public ShortcutRow(Context context, int icon, String listText, String shortcutText, Intent shortcutIntent) {
			put("icon", icon);
			put("text1", listText);
			data.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			data.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutText);
			data.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, ShortcutIconResource.fromContext(context, icon));
		}
		
		public Intent getData() {
			return data;
		}
	}
}
