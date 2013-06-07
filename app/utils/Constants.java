/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since Jul 28, 2012
 * @version 0.7.0
 */

package utils;

public interface Constants {
	
	// Configuration keys

	/**
	 * Shows if the system is configured via setup.
	 * {@value true or false}
	 */
	String CONFIG_ISCONFIGURED	=	"URS.IsConfigured";
	
	String CONFIG_LOCAL_SERVER_ID = "URS.LocalServerID";
	
	// 'fa' and 'en'
	String CONFIG_LANGUAGE		=	"URS.Language";
	
	String CONFIG_ONTOLOGY_UPDATE_INTERVAL		=	"URS.OntologyUpdateInterval";
	String CONFIG_UPLOAD_KB_INTERVAL			=	"URS.UploadKBInterval";
	
	// Central URL for updated ontology
	String CONFIG_ONTOLOGY_UPDATE_URL			=	"URS.OntologyUpdateURL";
	String CONFIG_ONTOLOGY_SEND_URL				=	"URS.OntologySendURL"; // central url to send ontology to
	String CONFIG_ONTOLOGY_LATEST_VERSION_URL	=	"URS.OntologyVersionURL";	// central url for the latest ontology version
	String CONFIG_API_VERSION					=	"URS.APIVersion"; // currently 0.2.0-prototype
	String CONFIG_CALENDAR_TYPE					=	"URS.CalendarType"; // Georgian or Jalali
	String CONFIG_TREEVIEW_TYPE					=	"URS.TreeViewType"; // top-down tree, jqx-based, or list tree
	String CONFIG_ONTOLOGY_LANGUAGE				=	"URS.OntologyLanguage"; // Ontology Selected Language (fa, en)
	String CONFIG_UI_LANGUAGE					=	"URS.InterfaceLanguage"; // User Interface Selected Language (fa, en)
	String CONFIG_LOCAL_ONLY					=	"URS.LocalOnly"; // System onlyLocal
	

}
