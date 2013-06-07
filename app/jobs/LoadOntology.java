/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since July 26, 2012
 * @version 0.2.0-prototype
 */

package jobs;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.Constants;
import utils.KnowledgeManager;

@OnApplicationStart
public class LoadOntology extends Job implements Constants {

	/**
	 * @throws Exception
	 */
	public final void doJob() throws Exception {

		String ontologyVersion = KnowledgeManager.getInstance().version();
		Logger.info("Ontology version: " + ontologyVersion);

	}
}
