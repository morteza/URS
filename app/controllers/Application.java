/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since July 27, 2012
 * @version 0.2.0-prototype
 */

package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import models.Configuration;
import models.User;
import models.UserType;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import utils.KnowledgeManager;
import utils.ReportUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
*
*/
@With(Secure.class)
@Check(UserType.REGISTERED)
public class Application extends Controller {

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
			if(user==null)
				return;
			renderArgs.put("username", user.name);
			renderArgs.put("userId", user.id);
		}
	}
	
    public static void index() {
    	//Version v = Version.parse(KnowledgeManager.getInstance().version());
    	//flash.success("Ontology Version: " + v.toString());
    	
    	//TODO: just a fix around showind a message after ajax double redirect
    	if ("true".equals(flash.get("hasMessage"))) {
        	//TODO: resolve keeping the success message all the time
    		flash.remove("hasMessage");
			flash.keep();
    	}
    	render();
    }

    public static void showNewReportForm(String reportClassName) {
		
    	try{
			String strReportFields  = ReportUtils.generateNewReportFormHTML(reportClassName);
			String reportTitle = KnowledgeManager.getInstance().getLabel(":"+reportClassName);
			String reportDescription = KnowledgeManager.getInstance().getDescription(":"+reportClassName);
			
			render(reportClassName, reportTitle, reportDescription, strReportFields);
			
		}catch(Exception e) {
			e.printStackTrace();
			error("Error while extracting html of report fields.");
		}
    }
    
    public static void listReportTypes() {
    	    	
    	try {
    		List<String> types = ReportUtils.getInstance().getReportTypes();
    		int size = types.size();
    		
    		List<String> titles = new ArrayList<String>();
    		List<String> descriptions = new ArrayList<String>();
    		
    		for(int i = 0 ; i<size ; i++) {
    			titles.add(KnowledgeManager.getInstance().getLabel(types.get(i)));
    			descriptions.add(KnowledgeManager.getInstance().getDescription(types.get(i)));		
    		}
    			
        	render(types, titles, descriptions, size);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		error("Error while retriving report types.");
    	}	
    }
    
    public static void saveReport(JsonObject body) {
    	JsonObject inputs = body.get("json").getAsJsonObject();
    	inputs.remove("authenticityToken");
    	Set<Entry<String, JsonElement>> jsonSet = inputs.entrySet();

    	List<String> fields = new ArrayList<String>();
    	List<String> values = new ArrayList<String>();
    	    	
    	// convert json to string map
    	for (Entry ent : jsonSet) {
    		JsonElement val = (JsonElement) ent.getValue();
    		
    		//FIXME: support multi-level JSON
    		// Only check first level
    		if (val.isJsonPrimitive()) {
    			// System.out.println("JSON-> " + ent.getKey() + " : " +  val.getAsJsonPrimitive());
    			String value = val.getAsJsonPrimitive().getAsString();
    			if (value!=null && value.trim().length()>0) {
    				fields.add((String) ent.getKey());
    				values.add(value);
    			}
    		}
    	}

    	int reportClassNameIndex = fields.indexOf("ReportClassName");

    	// Remove ReportClassName field and its value
    	String reportClass = values.remove(reportClassNameIndex);
    	fields.remove(reportClassNameIndex);

		try {
			ReportUtils.saveReport(reportClass, User.findByEmail(Security.connected()), fields, values);
			params.flash();
			flash.success(Messages.get("urs.report.SuccessfulSaveMessage"));
			flash.put("hasMessage", "true");
			//flash.keep();
			//redirect("Application.index");
			index();
			//String result = "Report saved!";
			//renderText(result);			
		} catch (Exception e) {
			error(e);
		}
    }

}