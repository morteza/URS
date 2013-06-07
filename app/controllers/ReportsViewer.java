/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since September 24, 2012
 * @version 1.0.0
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

import org.eclipse.jdt.core.dom.ThisExpression;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import models.Configuration;
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
import utils.html_builders.Builder;

/**
 * Pages and tools for report controller and actions to render corresponding pages.
 */
@With(Secure.class)
@Check(UserType.REGISTERED)
public class ReportsViewer extends Controller implements Constants{

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
	 * Show default home page for administrators of local servers.
	 */
	@Check(UserType.REGISTERED)
    public static void index() {
    	render();
    }
 
	@Check(UserType.ADMINISTRATOR)
    public static void listSubmittedReportTypes() {
		try {
			List<String> types = ReportUtils.getInstance().getReportTypes();
			int size = types.size();
	
			List<String> titles = new ArrayList<String>();
			List<Integer> counts = new ArrayList<Integer>();
	
			OWLClass cls;
			for(int i = 0 ; i<size ; i++) {
				titles.add(KnowledgeManager.getInstance().getLabel(types.get(i)));
				
				cls = KnowledgeManager.getInstance().getOWLClass(types.get(i));
				int cSize = cls.getIndividuals(KnowledgeManager.getInstance().ontology).size();
				
				// Report types includes themselves as individuals, so ignore one from the counts
				cSize = cSize -1;
				counts.add(cSize);
			}
			
	    	render(size, types, titles, counts);
		} catch (Exception e) {
			error(e);
		}
    }
	
	@Check(UserType.ADMINISTRATOR)
	public static void listSubmittedReports(String type) {
    	try{
    		// get individuals of submitted type instead of the whole
    		String tableHTML = ReportUtils.generateListSubmittedReportsHTML(type);
    		OWLClass cls = KnowledgeManager.getInstance().getOWLClass(type);
    		String reportType = KnowledgeManager.getInstance().getLabel(cls);
    		render(reportType, tableHTML);
    	} catch(Exception e) {
    		error(e);
    	}
	}	

}
