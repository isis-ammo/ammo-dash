// ST:BODY:start

package edu.vu.isis.ammo.dash.incident.provider;

public class IncidentSchemaBase {

   public static final int DATABASE_VERSION = 1;

   public static final String DATABASE_NAME = "incident.db";

   // ST:createRelationEnum:inline
   /**
    * add elements to the enumeration as needed
    */
   public static class MediaConstants {
	   public enum DataTypeEnum {
	       IMAGE("image/jpeg"),
	       AUDIO("audio/basic"),
	       TEXT("text/plain"),
	       VIDEO("video/3gpp"),
	       TEMPLATE("text/template");

	       final public String code;
	       private DataTypeEnum(String code) {
	         this.code = code;
	       }
	       public boolean equals(String that) {
	           if (that == null) return false;
	           return (this.code.equals(that));
	       }
	     }
   } 
   /**
    * add elements to the enumeration as needed
    */
   public static class EventConstants {
	   public static final String TIGR_TOPIC = "ammo/edu.vu.isis.ammo.map.object";

       public static final int STATUS_DRAFT = 1;
       public static final int STATUS_LOCAL_PENDING = 2;
       public static final int STATUS_SENT = 3;
       public static final int _DISPOSITION_DRAFT = 4;
       public static final int _DISPOSITION_LOCAL_PENDING = 5;

   } 
   /**
    * add elements to the enumeration as needed
    */
   public static class StatusConstants {
   } 
   /**
    * add elements to the enumeration as needed
    */
   public static class CategoryConstants {
	     public static final String RELOAD = "edu.vu.isis.ammo.dash.provider.incident.category.action.RELOAD";
	     public static final String RELOAD_FINISHED = "edu.vu.isis.ammo.dash.RELOAD_FINISHED";  
	 
   } 
   // ST:createRelationEnum:complete
}
// ST:BODY:end