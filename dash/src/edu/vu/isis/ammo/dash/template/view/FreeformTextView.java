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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.vu.isis.ammo.dash.R;
import edu.vu.isis.ammo.dash.template.parsing.AmmoParser;

/**
 * This class is a linear layout containing a TextView label and EditText
 * freeform box.
 * 
 * @author Demetri Miller
 */
public class FreeformTextView implements GuiField {

	// =====================
	// Fields
	// =====================
	private ViewGroup viewGroup;
	private TextView editText;
	private String id, labelValue;

	// =====================
	// Lifecycle
	// =====================

	public FreeformTextView(Context context, Element eNode) {
		// Inflate the free-form text view and set this linear layout as the
		// parent.
		viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.template_freeform_text_view, null);
		TextView label = (TextView) viewGroup
				.findViewById(R.id.freeform_text_view_label);
		editText = (TextView) viewGroup
				.findViewById(R.id.freeform_text_view_field);

		id = eNode.getAttribute(AmmoParser.ID_FIELD);
		labelValue = eNode.getAttribute(AmmoParser.LABEL_FIELD);
		if (labelValue == null || labelValue.length() == 0) {
			labelValue = id;
		}
		label.setText(labelValue + ":");

		String textValue = eNode.getAttribute(AmmoParser.TEXT_FIELD);
		if (textValue != null && textValue.length() != 0) {
			editText.setText(textValue);
		}

		String hintValue = eNode.getAttribute(AmmoParser.HINT_FIELD);
		if (hintValue != null && hintValue.length() != 0) {
			editText.setHint(hintValue);
		}

		String prepopValue = eNode.getAttribute(AmmoParser.PREPOP_FIELD);
		if (prepopValue != null && prepopValue.length() != 0) {
			this.prepopulateField(prepopValue);
		}

		String typeValue = eNode.getAttribute(AmmoParser.TYPE_FIELD);
		if (typeValue != null && typeValue.length() != 0) {
			this.setType(typeValue);
		}
	}

	// ========================================
	// Prepopulation
	// ========================================
	private void prepopulateField(String prepopValue) {
		editText.setText(AmmoParser.getPrepopMap().get(prepopValue));
	}

	// ========================================
	// Getters and Setters
	// ========================================
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
		return editText.getText().toString();
	}

	@Override
	public void setValue(String value) {
		editText.setText(value);
	}

	@Override
	public String getLabel() {
		return labelValue;
	}

	public void setType(String type) {
		final int mask;
		if (type.equals("number")) {
			mask = InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_SIGNED;
		} else if (type.equals("numberDecimal")) {
			mask = InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL;
		} else {
			mask = InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_NORMAL;
		}
		editText.setInputType(mask);
	}

	@Override
	public void makeReadOnly() {
		// replace with a TextView
		String value = getValue();
		viewGroup.removeView(editText);
		editText = new TextView(viewGroup.getContext());
		viewGroup.addView(editText);
		setValue(value);
	}

	@Override
	public String getTag() {
		return AmmoParser.TEXT_FIELD;
	}

}
