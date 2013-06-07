package owldb.util.instantiator;

import java.io.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * Instantiates an OWL object complement of.
 * 
 * @author Pawel Kaplanski
 */
public class OWLObjectComplementOfInstantiator implements IInstantiator
{
	/** {@inheritDoc} */
	@Override
	public Object instantiate (final OWLOntologyManager ontologyManager, final String entityName, final IRI iri, final Serializable id)
	{
		return new OWLObjectComplementOfImpl (ontologyManager.getOWLDataFactory (), null);
	}
}
