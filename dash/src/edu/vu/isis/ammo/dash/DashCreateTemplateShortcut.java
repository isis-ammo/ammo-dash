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

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import edu.vu.isis.ammo.dash.DashCreateShortcut.ShortcutRow;
import edu.vu.isis.ammo.dash.template.AmmoTemplateManagerActivity;

public class DashCreateTemplateShortcut extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setListAdapter();

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent data = ((ShortcutRow)parent.getItemAtPosition(position)).getData();
				
				//user selected, setResult and finish
				setResult(RESULT_OK, data);
				finish();
			}
		});

	}
	
	private void setListAdapter() {
		List<ShortcutRow> list = new ArrayList<ShortcutRow>();
		
		for(String file : AmmoTemplateManagerActivity.getTemplateFiles(this)) {
			String name = file;
			name = name.replaceFirst("\\.xml$", "");  //case sensitive
			ShortcutRow row = new ShortcutRow(this, R.drawable.template_button2, name + " template", name, setupIntent(file));
			list.add(row);
		}

		setListAdapter(new SimpleAdapter(this, list,
				R.layout.pick_item, DashCreateShortcut.DASH_FIELD_NAMES, DashCreateShortcut.DASH_FIELD_RESOURCES));
	}

	private Intent setupIntent(String template) {
		return new Intent(this, AmmoTemplateManagerActivity.class)
				.putExtra(AmmoTemplateManagerActivity.TEMPLATE_EXTRA, template)
				.putExtra(DashAbstractActivity.OPEN_FOR_EDIT_EXTRA, true);
	}
}
