/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @author Arman Radmanesh <radmanesh@gmail.com>
 * @since July 26, 2012
 * @version 0.2.0-prototype
 */

package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jobs.LoadConfiguration;

import org.coode.owlapi.owlxmlparser.OWLClassElementHandler;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.hibernate.mapping.Array;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import models.Configuration;
import models.OntologyDetails;
import models.User;
import models.UserType;
import play.Logger;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Files;
import play.libs.WS;
import play.mvc.*;
import utils.Constants;
import utils.KnowledgeManager;
import utils.ReportUtils;
import utils.Version;
import utils.html_builders.Builder;

/**
 * Administrators controller and actions to render corresponding pages.
 */
@With(Secure.class)
@Check(UserType.REGISTERED)
public class Administration extends Controller implements Constants{

	/**
	 * Executes before any of administration
	 */
	@Before
	public static void logRequest() {
		Logger.info("Request Date: %s", request.date.toString());
		
	}
	
	@Before
	public static void checkConfiguration() {
		if ( !Configuration.isConfigured() ) {
			Setup.index();
		}
	}

	@Before
	public static void setConnectedUser() {
		if (Security.isConnected()) {
			renderArgs.put("connected", Security.connected());
			
			User user = User.findByEmail(Security.connected());
			renderArgs.put("username", user.name);
			renderArgs.put("userId", user.id);
		}
	}

	/**
	 * Manually upload ontology page
	 */
	@Check(UserType.ADMINISTRATOR)
    public static void uploadOntologyPage() {
		String version = KnowledgeManager.getInstance().version();
		render(version);
    }

	/**
	 * Manually upload file as ontology
	 */
	@Check(UserType.ADMINISTRATOR)
    public static void uploadOntology(File ontologyFile) {
    	
    	try {
    		(OWLManager.createOWLOntologyManager()).loadOntologyFromOntologyDocument(ontologyFile);
		} catch (Exception e1) {
			flash.error(Messages.get("urs.admin.uploadInvalidOntology"),e1);
			uploadOntologyPage();
		}
    	// create backup of current version
		String oldPath = KnowledgeManager.getInstance().getOntologyFilePath() + "_" + Codec.UUID() + ".bak"; 
		Files.copy(KnowledgeManager.getInstance().getOntologyFile(), new File(oldPath));
    	
		try {
    		// delete current version
    		Files.delete(KnowledgeManager.getInstance().getOntologyFile());
        	
    		// copy ontologyFile to the ontologiesDirectory and rename it
    		Files.copy(ontologyFile, KnowledgeManager.getInstance().getOntologyFile());
    		
    		KnowledgeManager.getInstance().reloadOntology();
    		//TODO: KBManager.getInstance().reloadKB();

        	flash.success(Messages.get("urs.admin.uploadOntologyDone"));
        	Application.index();
    	} catch (Exception e) {
    		//Copy back the backup file
    		Files.copy(new File(oldPath), KnowledgeManager.getInstance().getOntologyFile());
    		Files.delete(new File(oldPath));
			
    		flash.error(Messages.get("urs.admin.uploadOntologyFailed"), e);
			error(e);
		}

    }

	/**
	 * Lists all users.
	 */
	@Check(UserType.ADMINISTRATOR)
	public static void listAllUsers() {
		List<User> users = User.findAll();
		renderTemplate("UserManagement/listAllUsers.html",users);
	}
	
	/**
	 * Shows a user information by Id.
	 * @param id
	 */
	@Check(UserType.REGISTERED)
	public static void showUserProfile(Long id) {
		User user = User.findById(id);
		renderTemplate("UserManagement/showUserProfile.html",user);
	}
	
	@Check(UserType.ADMINISTRATOR)
	public static void deleteUser(Long id) {
		User user = User.findById(id);
		if (!user.equals(Security.getConnectedUser())) {
			String email = user.email;
			user.delete();
			flash.success(Messages.get("urs.admin.userManagement.deleteDone"), email);
		} else {
			flash.error(Messages.get("urs.admin.userManagement.deleteFailed"));
		}
		listAllUsers();
	}
	
	/**
	 * Confirms user removal.
	 */
	@Check(UserType.ADMINISTRATOR)
	public static void confirmDeleteUser(Long id) {
		User user = User.findById(id);
		if (user.equals(Security.getConnectedUser())) {
			flash.error(Messages.get("urs.admin.userManagement.deleteFailed"));
			listAllUsers();
		}
		renderTemplate("UserManagement/confirmDeleteUser.html",user);
	}
	
	/**
	 * Shows user details in form inputs
	 * @param id User's id
	 */
	@Check(UserType.REGISTERED)
	public static void editUserPage(Long id) {
		User user = User.findById(id);
		renderTemplate("UserManagement/editUser.html",user);
	}
	
	/**
	 * Updates an existing user.
	 */
	@Check(UserType.REGISTERED)
	public static void updateUser(Long id, String email, String name, String password, String verifyPassword, boolean isAdmin) {
		User user = User.findById(id);
		validation.valid(user);
		validation.required(password);
		validation.required(verifyPassword);
		validation.email(email);
		validation.equals(password, verifyPassword).message(Messages.get("urs.admin.userManagement.passwordsNotMatch"));

		// Check password security (for now, just size has to be 8 or more)
		if (password.length() < 8) {
			validation.addError("password", Messages.get("urs.admin.userManagement.shortPasswordError"));
		}
		
		// check if email is not used elsewhere
		if (!email.equals(user.email) && !User.isEmailAvailable(email)) {
			validation.addError("user.email", Messages.get("urs.admin.userManagement.emailIsNotAvailable"));
		}

		
		if(validation.hasErrors()) {
			renderTemplate("UserManagement/editUser.html",user);
		}
		user.email = email;
		user.name = name;
		user.passwordHash = Codec.hexMD5(password);
		user.isAdmin = isAdmin;
		
		user.save();
		flash.success(Messages.get("urs.admin.userManagement.userInfoUpdated"));
		
		if (User.findByEmail(Security.connected()).isAdmin)
			listAllUsers();
		Administration.showUserProfile(id);
	}
	
	@Check(UserType.ADMINISTRATOR)
	public static void newUserPage() {
		renderTemplate("UserManagement/newUser.html");
	}
	
	/**
	 * Forms for creating new user.
	 */
	@Check(UserType.ADMINISTRATOR)
	public static void newUser(String email, String name, String password, String verifyPassword, boolean isAdmin) {
		User user = new User(email, password, name);
		user.isAdmin = isAdmin;
		
		// check if email is not used!
		if (!User.isEmailAvailable(email)) {
			validation.addError("user.email", Messages.get("urs.admin.userManagement.emailIsNotAvailable"));
		}
		
		validation.valid(user);
		validation.required(verifyPassword);
		validation.required(email);
		validation.equals(password, verifyPassword).message(Messages.get("urs.admin.userManagement.passwordsNotMatch"));
				
		if(validation.hasErrors()) {
			user.delete();
			renderTemplate("UserManagement/newUser.html");
		}
		
		user.create();
		user.save();
		flash.success(Messages.get("urs.admin.userManagement.newUserCreated"));
		listAllUsers();
	}
	
	/**
	 * Show default home page for administrators of local servers.
	 */
	@Check(UserType.REGISTERED)
    public static void index() {
    	render();
    }

    /**
     * Show jOWL-based browser for the report ontology
     */
	@Check(UserType.ADMINISTRATOR)
    public static void browseOntology() {
    	String browserHTML = "";
    	OWLClass domainConcept = KnowledgeManager.getInstance().getOWLClass(":Domain_Concept");
    	OWLClass htmlTree = KnowledgeManager.getInstance().getOWLClass(":HTML_Tree");

		String oldTreeType = Configuration.getValue(CONFIG_TREEVIEW_TYPE, "simple");
    	try {
    		//FIXME workaround a bug caused by selected tree view of configuration
    		Configuration.setValue(CONFIG_TREEVIEW_TYPE, "jqx");
			browserHTML = new Builder(domainConcept, htmlTree).getHTML();
    		Configuration.setValue(CONFIG_TREEVIEW_TYPE, oldTreeType);	
    		
	    	render(browserHTML);
    	} catch (Exception e) {
			e.printStackTrace();
			error(e);
		}	
    }
    
	/**
	 * Show some details of the loaded ontology (e.g. version, size, etc)
	 */
	@Check(UserType.ADMINISTRATOR)
	public static void showOntologyDetails() {
		
		try {
			OntologyDetails details = new OntologyDetails();

			render(details);			
		} catch (Exception e) {
			error(e);
		}
	}
	
	@Check(UserType.ADMINISTRATOR)
    public static void acceptReport(String reportId) {
    	try {
    		ReportUtils.setReportAccepted(reportId);
    		flash.success(Messages.get("urs.admin.reports.acceptDone"));
    	} catch (Exception e) {
    		flash.error(Messages.get("urs.admin.reports.acceptFailed"));
    	}
    	listAllReports();
    }
    
	@Check(UserType.ADMINISTRATOR)
    public static void rejectReport(String reportId) {
    	try {
    		ReportUtils.setReportRejected(reportId);
    		flash.success(Messages.get("urs.admin.reports.rejectDone"));
    	} catch (Exception e) {
    		flash.error(Messages.get("urs.admin.reports.rejectFailed"));
    	}
    	listAllReports();
    }
    
	@Check(UserType.REGISTERED)
    public static void settings() {
    	Configuration config = Configuration.all().first();    	
    	render(config);
    }
    
	@Check(UserType.REGISTERED)
    public static void saveSettings(JsonObject body) {
    	JsonObject inputs = body.get("json").getAsJsonObject();
    	inputs.remove("authenticityToken");
    	Set<Entry<String, JsonElement>> jsonSet = inputs.entrySet();
    	//TODO: check if error occurs
    	// convert json to string map
    	for (Entry ent : jsonSet) {
    		JsonElement val = (JsonElement) ent.getValue();
    		//FIXME: support multi-level JSON
    		// Only check first level
    		if (val.isJsonPrimitive()) {
    			//System.out.println("JSON-> " + ent.getKey() + " : " +  val.getAsJsonPrimitive());
    			String configName = "URS."+ent.getKey().toString();
    			if(!configName.equals(null)){
    				Configuration.setValue(configName, val.getAsString());
    			}
    		}
    	}
    	flash.success(Messages.get("urs.admin.settings.success"));
    	
    }
    
	@Check(UserType.ADMINISTRATOR)
    public static void sparqldl(String query) {
    	render();
    }
    
	@Check(UserType.ADMINISTRATOR)
    public static void sparqldlPage() {
    	String defaultQuery = "PREFIX : <http://itrc.ac.ir/ReportOntology#>\n";
    	defaultQuery += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n\n";
    	defaultQuery += "SELECT * WHERE { subClassOf(?report, :Report) }\n";
;
    	render(defaultQuery);
    }
    
	@Check(UserType.ADMINISTRATOR)
    public static void listAllReports() {
    	try{
    		String tableHTML = ReportUtils.generateAllReportsHTML();
    		render(tableHTML);
    	} catch(Exception e) {
    		//error(e);
    		String tableHTML = "<table><tr><td>" + Messages.get("urs.admin.noReportSubmittedYet")+ "</td></tr></table>";
    		render(tableHTML);
    	}
    }
 
	@Check(UserType.ADMINISTRATOR)
    public static void showReportDetails(String reportId) {
    	String detailsTableHTML = ReportUtils.generateReportDetailsHTML(reportId);
    	render(detailsTableHTML);
    }

	public static void sendKnowledgeToCentral(){
		File ontologyFile=KnowledgeManager.getInstance().getOntologyFile();
	    WS.FileParam fp = new WS.FileParam(ontologyFile, "ontologyFile");
	    Map<String,Object> params = new HashMap<String, Object>();
	
	    //fetching central URL from configurations   
		String url = Configuration.getValue(CONFIG_ONTOLOGY_UPDATE_URL, null);
		if (url != null) {
			url += "/receiveKnowledge";
		}
		System.out.println(url);
	    //TODO: validate url
	    params.put(CONFIG_LOCAL_SERVER_ID, Configuration.getValue(CONFIG_LOCAL_SERVER_ID, Messages.get("urs.admin.settings.LocalServerID.default")));
	    try {
	    	String doc = WS.url( url)
	    		.params(params)
	    		.files( fp )
	    		.post()
	    		.getString();
	    	flash.success(Messages.get("urs.admin.settings.success.sendKnowledge"));			
		} catch (Exception e) {
			flash.error(Messages.get("urs.admin.settings.error.sendKnowledge"));
		}
	    Application.index();
	}

	public static void retreiveKnowledgeFromCentral(){
		try {
			LoadConfiguration.updateKnowledgeJob.now();
			flash.success(Messages.get("urs.admin.settings.success.retrieveKnowledge"));
			Application.index();
		} catch (Exception e) {
			flash.error(Messages.get("urs.admin.settings.error.retrieveKnowledge"),e);
			Application.index();
		}
	}
    
}
