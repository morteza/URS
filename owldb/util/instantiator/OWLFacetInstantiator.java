/*
 * This file is part of OWLDB.
 * 
 * OWLDB is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * OWLDB is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with OWLDB. If not, see <http://www.gnu.org/licenses/>.
 */

package owldb.util.instantiator;

import java.io.*;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.*;


/**
 * Instantiates an OWL Facet.
 * 
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 * @author Morteza Ansarinia (ITRC)
 */
public class OWLFacetInstantiator implements IInstantiator
{
	/** {@inheritDoc} */
	@Override
	public Object instantiate (final OWLOntologyManager ontologyManager, final String entityName, final IRI iri, final Serializable id)
	{
		return null;//OWLFacet.LENGTH;
	}

	@Override
	public Object instantiate(OWLDataFactory dataFactory, String entityName,
			IRI iri, Serializable id) {
		return OWLFacet.LENGTH;
	}
}
