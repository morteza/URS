/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since July 26, 2012
 * @version 0.2.0-prototype
 */

package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.ResultSet;

import jobs.UpdateOntology;
import models.Configuration;

import play.Logger;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.F.Promise;
import play.libs.Files;
import play.mvc.*;
import sparqldl.QueryResult;
import utils.Constants;
import utils.KnowledgeManager;

/**
 * Provides programming interfaces and web services for unified reporting local servers.
 * 
 */
@With(Secure.class)
@Check("administrator")
public class API extends Controller implements Constants {

	@Before
	public static void checkConfiguration() {
		if ( !Configuration.isConfigured() ) {
			Setup.index();
		}
	}

	@Before
	public static void checkVersion(String version) {
		
		String currentVersion = Configuration.getValue(CONFIG_API_VERSION, null);
		if ( currentVersion != null )
			assert version.equals(currentVersion) : "Invalid API version.";
	}
	
    public static void index() {
    	
		String apiVersion = Configuration.getValue(CONFIG_API_VERSION, null);
		String ontologyVersion = KnowledgeManager.getInstance().version();
    	renderText("API Version: " + apiVersion + "\nOntology: " + ontologyVersion);
    }
        
    public static void updateOntology(File owlFile) {
    	
    	try {
    		Promise ongoingUpdate = new UpdateOntology().now();
    		await(ongoingUpdate);
    		
    		//FIXME KBManager.getInstance().reloadKB();
    		KnowledgeManager.getInstance().reloadOntology();
    		String version = KnowledgeManager.getInstance().version();
    		Configuration.setValue(CONFIG_API_VERSION, version);
    		renderText("Ontology Updated (New Version: " + version + ")");
    	} catch (Exception e) {
    		Logger.error("Error while updating ontology!", e);
    		error("Error while updating ontology!");
    	}
    }

    public static void sparqldl(String body) {
    	try{
    		QueryResult result = KnowledgeManager.getInstance().sparqldl(body);
    		//TODO: uncomment this JSON output
    		//renderJSON(result.toJSON());
    		String html = result.toString().replace("\n", "<br />");
    		renderText(html);
    	} catch (Exception e) {
    		Logger.error("Error while executing SPARQL-DL query!", e);
    		error("Error while executing SPARQL-DL query!");
    	}
    }
    
    /**
     * same as {@link Administration.uploadOntology}
     * @param ontologyFile
     */
    public static void uploadOntology(File ontologyFile) {

    	// create backup of current version
		String oldPath = KnowledgeManager.getInstance().getOntologyFilePath() + "_" + Codec.UUID() + ".bak"; 
		Files.copy(KnowledgeManager.getInstance().getOntologyFile(), new File(oldPath));
    	
		try {
    		// delete current version
    		Files.delete(KnowledgeManager.getInstance().getOntologyFile());
        	
    		// copy ontologyFile to the ontologiesDirectory and rename it
    		Files.copy(ontologyFile, KnowledgeManager.getInstance().getOntologyFile());
    		
    		KnowledgeManager.getInstance().reloadOntology();
    		//FIXME: KBManager.getInstance().reloadKB();

        	renderText("Ontology updated!");
    	} catch (Exception e) {
    		//Copy back the backup file
    		Files.copy(new File(oldPath), KnowledgeManager.getInstance().getOntologyFile());
    		Files.delete(new File(oldPath));
			
			error(e);
		}
    }
    
    public static void downloadKnowledgeBase() {
		try {
			InputStream owlFile = new FileInputStream(KnowledgeManager.getInstance().getOntologyFilePath());
			
			// send file
			renderBinary(owlFile,"knowledge-" + Codec.UUID() + ".owl");
		} catch(Exception e) {
			Logger.error("Ontology file error.", e);
		}
		error();
    }
    
}
