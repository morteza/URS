/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since July 26, 2012
 * @version 0.2.0-prototype
 */

package jobs;

import models.*;
import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.Constants;

@OnApplicationStart
public class LoadConfiguration extends Job implements Constants{

	public static UpdateOntology updateKnowledgeJob;
	
	public void doJob() {
		
		boolean isConfigured = Configuration.isConfigured();
		
		System.out.println("configured: " + isConfigured);
		
		/*
		if (!isConfigured) {
			
			System.out.println("Setting default configurations...");
			
			// delete all users
			User.deleteAll();
			
			// delete all configurations
			Configuration.deleteAll();

			Configuration.setConfigured(true);
			
			System.out.println("Default configuration created!");
		}
		*/
		
		// add DEV-mode admin user
		if (Play.mode.isDev()) {
			if (User.isEmailAvailable("admin@local")) {
				// create default user if no user is defined
				User user = new User("admin@local", "admin", Messages.get("urs.devMode.adminName"));
				user.isAdmin = true;
				user.create();
				user.save();
			}
		} else {
			// Delete DEV-mode admin user
			if (!User.isEmailAvailable("admin@local")) {
				User user = User.findByEmail("admin@local");
				user.delete();
			}
		}

		// set the default language
		String language = Configuration.getValue( CONFIG_LANGUAGE, "fa" );
		if ( language != null )
			Lang.set(language);
		
		updateKnowledgeJob = new UpdateOntology();
		updateKnowledgeJob.every(Configuration.getValue(Constants.CONFIG_ONTOLOGY_UPDATE_INTERVAL, "1h"));

	}
}
