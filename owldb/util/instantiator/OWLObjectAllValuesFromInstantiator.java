package owldb.util.instantiator;

import java.io.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * Instantiates an OWL object all values from.
 * 
 * @author Pawel Kaplanski
 */
public class OWLObjectAllValuesFromInstantiator implements IInstantiator
{
	/** {@inheritDoc} */
	@Override
	public Object instantiate (final OWLOntologyManager ontologyManager, final String entityName, final IRI iri, final Serializable id)
	{
		return new OWLObjectAllValuesFromImpl (ontologyManager.getOWLDataFactory (), null, null);
	}
}
