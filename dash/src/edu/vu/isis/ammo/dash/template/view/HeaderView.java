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

import org.w3c.dom.Element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.vu.isis.ammo.dash.R;
import edu.vu.isis.ammo.dash.template.parsing.AmmoParser;

public class HeaderView implements GuiField {

	private ViewGroup viewGroup;
	private TextView label;
	private String id;
	private String labelValue;
	
	public HeaderView(Context context, Element eNode) {
		viewGroup = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.template_header_view, null);
		label = (TextView) viewGroup.findViewById(R.id.headerText);
		id = eNode.getAttribute(AmmoParser.ID_FIELD);
		labelValue = eNode.getAttribute(AmmoParser.LABEL_FIELD);
		if(labelValue == null || labelValue.length() == 0) {
			labelValue = id;
		}
		label.setText(labelValue);
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
	public String getTag() {
		return AmmoParser.HEADER_TAG;
	}

	@Override
	public String getLabel() {
		return labelValue;
	}

	@Override
	public String getValue() {
		return null;
	}

	@Override
	public void setValue(String value) {
		labelValue = value;
		label.setText(value);
	}

	@Override
	public void makeReadOnly() {
		viewGroup.setVisibility(View.GONE);
	}

}
