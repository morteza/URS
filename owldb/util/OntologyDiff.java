package owldb.util;

import java.util.*;
import java.util.logging.*;
import org.semanticweb.owlapi.model.*;


/**
 * Lists differences between snapshots of an ontology.
 * 
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class OntologyDiff
{
	private final static Logger	LOGGER		= Logger.getLogger ("services.pescado.knowledgebaseaccessservice");

	private final OWLOntology		ontology;
	private Set<OWLAxiom>				snapshot	= null;


	/**
	 * Constructor.
	 * 
	 * @param ontology The ontology to work on.
	 */
	public OntologyDiff (final OWLOntology ontology)
	{
		this.ontology = ontology;
	}


	/**
	 * If called the first time a snapshot of the current state of the ontology is
	 * taken. If a snapshot is present the differences between the snapshot and
	 * the current state is dumped. The current state will be the new snapshot
	 * after the function exits.
	 */
	public void dumpDiff ()
	{
		final Set<OWLAxiom> currentAxioms = this.ontology.getAxioms ();
		if (this.snapshot != null)
		{
			final Set<OWLAxiom> addedAxioms = new HashSet<OWLAxiom> (currentAxioms);
			final Set<OWLAxiom> removedAxioms = new HashSet<OWLAxiom> ();
			for (final OWLAxiom ax: this.snapshot)
			{
				if (!addedAxioms.remove (ax))
					removedAxioms.add (ax);
			}
			LOGGER.info ("OntologyDiff: Removed axioms:");
			for (final OWLAxiom ax: removedAxioms)
				LOGGER.info ("  " + ax);
			LOGGER.info ("OntologyDiff: Added axioms:");
			for (final OWLAxiom ax: addedAxioms)
				LOGGER.info ("  " + ax);
		}
		else
			LOGGER.info ("OntologyDiff: Snapshot taken.");
		this.snapshot = currentAxioms;
	}
}
