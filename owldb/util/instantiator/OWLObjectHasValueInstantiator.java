package owldb.util.instantiator;

import java.io.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * Instantiates an OWL object complement of.
 * 
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 */
public class OWLObjectHasValueInstantiator implements IInstantiator
{
	/** {@inheritDoc} */
	@Override
	public Object instantiate (final OWLOntologyManager ontologyManager, final String entityName, final IRI iri, final Serializable id)
	{
		return new OWLObjectHasValueImpl (ontologyManager.getOWLDataFactory (), null, null);
	}
}
