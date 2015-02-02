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
package edu.vu.isis.ammo.dash.template.view;

import java.util.List;

import org.w3c.dom.Element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import edu.vu.isis.ammo.dash.R;
import edu.vu.isis.ammo.dash.template.parsing.AmmoParser;
import edu.vu.isis.ammo.dash.template.parsing.ViewParsers;

/**
 * Collapseable view used to display a predefined set of values.
 * @author demetri
 *
 */
public class PulldownView implements GuiField {

	// =====================
	// Fields
	// =====================
	private Spinner spinner;
	private TextView textView;
	private ViewGroup viewGroup;
	private List<String> values;
	private String id, labelValue;
	
	// =====================
	// Lifecycle
	// =====================
	public PulldownView(Context context, Element eNode) {
		values = ViewParsers.getTextFromChildNodes(eNode);
		
		viewGroup = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.template_pulldown_view, null);
		TextView label = (TextView)viewGroup.findViewById(R.id.pulldown_view_label);
		
		id = eNode.getAttribute(AmmoParser.ID_FIELD);
		labelValue = eNode.getAttribute(AmmoParser.LABEL_FIELD);
		if(labelValue == null || labelValue.length() == 0) {
			labelValue = id;
		}
		label.setText(labelValue + ":");
		
		if (values.isEmpty()) return;
		spinner = (Spinner)viewGroup.findViewById(R.id.pulldown_view_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, values);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	@Override
	public View getView() {
		return viewGroup;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getValue() {
		if(spinner != null) {
			return (String)spinner.getSelectedItem();
		}
		if(textView != null) {
			return textView.getText().toString();
		}
		return null;
	}
	
	@Override
	public void setValue(String value) {
		if(spinner != null) {
			int index = values.indexOf(value);
			spinner.setSelection(index >= 0 ? index : 0);
		}
		if(textView != null) {
			textView.setText(value);
		}
	}

	@Override
	public String getLabel() {
		return labelValue;
	}
	
	@Override
	public void makeReadOnly() {
		//replace with a TextView
		String value = getValue();
		viewGroup.removeView(spinner);
		textView = new TextView(viewGroup.getContext());
		spinner = null;
		viewGroup.addView(textView);
		setValue(value);
	}

	@Override
	public String getTag() {
		return AmmoParser.PULLDOWN_TAG;
	}
}
