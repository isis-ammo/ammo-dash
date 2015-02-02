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

public class IncidentSchema extends IncidentSchemaBase {

   public static final int DATABASE_VERSION = 4;
      
      public static final String DATABASE_NAME = "incident.db";

      public static class MediaTableSchema extends MediaTableSchemaBase {

         protected MediaTableSchema() { } // no instantiation

	 public static final String IMAGE_DATA_TYPE = "image/jpeg";
         public static final String AUDIO_DATA_TYPE = "audio/basic";
         public static final String TEXT_DATA_TYPE = "text/plain";
         public static final String VIDEO_DATA_TYPE = "video/3gpp";
         public static final String TEMPLATE_DATA_TYPE = "text/template";
      }    
      public static class EventTableSchema extends EventTableSchemaBase {

         public static final String TIGR_TOPIC = "ammo/edu.vu.isis.ammo.map.object";
         protected EventTableSchema() { super(); }

         public static final int STATUS_DRAFT = 1;
         public static final int STATUS_LOCAL_PENDING = 2;
         public static final int STATUS_SENT = 3;
         public static final int _DISPOSITION_DRAFT = 4;
         public static final int _DISPOSITION_LOCAL_PENDING = 5;
      }    
      public static class CategoryTableSchema extends CategoryTableSchemaBase {

         public static final String RELOAD = "edu.vu.isis.ammo.dash.provider.incident.category.action.RELOAD";
         public static final String RELOAD_FINISHED = "edu.vu.isis.ammo.dash.RELOAD_FINISHED";
         
         protected CategoryTableSchema() { super(); }
      }    
}
