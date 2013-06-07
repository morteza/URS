package owldb.util.instantiator;

import java.io.*;
import java.util.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * Instantiates an OWL data type restriction.
 * 
 * @author Pawel Kaplanski
 */
public class OWLDatatypeRestrictionInstantiator implements IInstantiator
{
	/** {@inheritDoc} */
	@Override
	public Object instantiate (final OWLOntologyManager ontologyManager, final String entityName, final IRI iri, final Serializable id)
	{
		return new OWLDatatypeRestrictionImpl (ontologyManager.getOWLDataFactory (), null, new HashSet<OWLFacetRestriction> ());
	}
}
