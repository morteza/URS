package owldb.util.instantiator;

import java.io.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * Instantiates an OWL object some values from.
 * 
 * @author Pawel Kaplanski
 */

public class OWLObjectSomeValuesFromInstantiator implements IInstantiator
{
	/** {@inheritDoc} */
	@Override
	public Object instantiate (final OWLOntologyManager ontologyManager, final String entityName, final IRI iri, final Serializable id)
	{
		return new OWLObjectSomeValuesFromImpl (ontologyManager.getOWLDataFactory (), null, null);
	}
}
