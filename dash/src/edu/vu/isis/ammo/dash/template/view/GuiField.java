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

import android.view.View;

/**
 * Interface that should be implemented by all template widgets so proper data
 * may be acquired by interested parties.
 * 
 * @author adrian
 * 
 */
public interface GuiField {
	/**
	 * @return the View representing this field
	 */
	View getView();

	/**
	 * @return an ID that will be used to uniquely identify this GuiField. This
	 *         will typically come from an id attribute inside the XML element
	 *         that this field was created from.
	 */
	String getId();

	/**
	 * @return the XML tag that is used to trigger creation of this GuiField
	 */
	String getTag();

	/**
	 * @return the label for this GuiField
	 */
	String getLabel();

	/**
	 * @return the value bound to this GuiField.
	 */
	String getValue();

	/**
	 * If we are set to read only mode, then set the TextView to the value of
	 * the String given. Otherwise, parse the String to set the GuiField to have
	 * the appropriate state.
	 * 
	 * @param value
	 *            the value to which this GuiField will be set
	 */
	void setValue(String value);

	/**
	 * This replaces the field with a TextView.
	 */
	void makeReadOnly();
}
