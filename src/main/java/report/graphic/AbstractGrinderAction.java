package report.graphic;

/**
 * Abstract class with functionality common to all Grinder actions.
 *
 * @author Eivind B Waaler
 */
public class AbstractGrinderAction {
	
   public String getIconFileName() {
      return GrinderPlugin.ICON_FILE_NAME;
   }

   public String getDisplayName() {
      return GrinderPlugin.DISPLAY_NAME;
   }

   public String getUrlName() {
      return GrinderPlugin.URL;
   }

}
