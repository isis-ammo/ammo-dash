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

import edu.vu.isis.ammo.dash.template.parsing.AmmoParser;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RadioGroupView extends CompoundGroupView {

	public RadioGroupView(Context context, Element eNode) {
		super(context, eNode);
		RadioGroup radioGroup = new RadioGroup(context);
		LayoutParams params = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		radioGroup.setLayoutParams(params);
		mLinearLayout.addView(radioGroup);

		for (String value : mValues) {
			RadioButton rb = new RadioButton(context);
			rb.setLayoutParams(params);
			rb.setText(value);
			radioGroup.addView(rb);
			mCompoundButtons.add(rb);
		}
	}
	
	@Override
	public String getTag() {
		return AmmoParser.RADIOGROUP_TAG;
	}

}
