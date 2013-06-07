/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since July 26, 2012
 * @version 0.2.0-prototype
 */

package jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import models.Configuration;
import play.Logger;
import play.jobs.Job;
import play.libs.Codec;
import play.libs.Files;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import utils.Constants;
import utils.KnowledgeManager;
import utils.Version;

public class UpdateOntology extends Job implements Constants{
	
	@Override
	public void doJob() throws Exception {
		
		String bakFile = KnowledgeManager.getInstance().getOntologyFilePath() + "_" + Codec.UUID() + ".bak";
		String oldOntologyFileName = KnowledgeManager.getInstance().getOntologyFilePath();
		Map<String,Object> params = new HashMap<String, Object>();
		params.put(CONFIG_LOCAL_SERVER_ID, Configuration.getValue(CONFIG_LOCAL_SERVER_ID, ""));

		// --- move the old ontology file to some safe place, then delete
		// the original
		Files.copy(KnowledgeManager.getInstance().getOntologyFile(), new File(bakFile));
		Files.delete(new File(oldOntologyFileName));

		try {
	    	String updateUrl = Configuration.getValue(CONFIG_ONTOLOGY_UPDATE_URL, null)+"/sendKnowledge";
			String latestVersionUrl = Configuration.getValue(CONFIG_ONTOLOGY_UPDATE_URL, null) + "/api/index";
			Version currentVersion = Version.parse(KnowledgeManager.getInstance().version());
			HttpResponse response = WS.url(latestVersionUrl).params(params).get();
			String latestVersion = response.getString();// response.getJson().getAsString();

	    	if (updateUrl == null || latestVersionUrl == null) {
	    		Exception e = new Exception("Invalid update or version URL.");
	    		Logger.error("Invalid ontology update/version URL", e);
	    		throw e;
	    	}

			System.out.println("current version:" + currentVersion + " latest version:" + latestVersion);
	    	
	    	// Check if update is available
	    	if (Version.parse(latestVersion).compareTo(currentVersion) <= 0) {
	    		// Current ontology is updated!
	    		System.out.println("already updated");
	    		return;
	    	}
	    	
	    	// get updated ontology and update local ontology
	    	response = null;
	    	response =  WS.url( updateUrl)
		    		.params(params)
		    		.get();		
			InputStream stream = response.getStream();
			
			File ontologyFile = new File(oldOntologyFileName);
			OutputStream out = new FileOutputStream(ontologyFile);

			// copy stream into the main file
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = stream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();

			// --- copy new owl file into the place
			// Files.delete(KnowledgeManager.getInstance().getOntologyFile());
			Files.copy(ontologyFile, KnowledgeManager.getInstance().getOntologyFile());

		} catch(Exception e) {
			if (new File(bakFile).exists()) {
				Files.delete(KnowledgeManager.getInstance().getOntologyFile());
				Files.copy(new File(bakFile), KnowledgeManager.getInstance().getOntologyFile());
			}
			
		}
		
	}
	
}
