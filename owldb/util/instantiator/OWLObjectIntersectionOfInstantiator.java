package owldb.util.instantiator;

import java.io.*;
import java.util.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * Instantiates an OWL object intersection of.
 * 
 * @author Pawel Kaplanski
 */
public class OWLObjectIntersectionOfInstantiator implements IInstantiator
{
	/** {@inheritDoc} */
	@Override
	public Object instantiate (final OWLOntologyManager ontologyManager, final String entityName, final IRI iri, final Serializable id)
	{
		return new OWLObjectIntersectionOfImpl (ontologyManager.getOWLDataFactory (), new HashSet<OWLClassExpression> ());
	}
}
