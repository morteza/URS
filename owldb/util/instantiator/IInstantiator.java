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


/**
 * Interface for instantiating an OWL-API object.
 * 
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 * @author Morteza Ansarinia (ITRC)
 */
public interface IInstantiator
{
	/**
	 * Instantiate an object.
	 * 
	 * @param ontologyManager The manager to get the data factory from which to
	 *          retrieve objects
	 * @param entityName The name of the entity
	 * @param iri The IRI of the object
	 * @param id The id of the object
	 * @return The object
	 * @deprecated
	 */
	Object instantiate (final OWLOntologyManager ontologyManager, final String entityName, final IRI iri, final Serializable id);
	
	/**
	 * Instantiate an object.
	 * 
	 * @param dataFactory The data factory from which objects are retrieved.
	 * @param entityName The name of the entity
	 * @param iri The IRI of the object
	 * @param id The id of the object
	 * @return The object
	 */
	Object instantiate(final OWLDataFactory dataFactory, final String entityName, final IRI iri, final Serializable id);
}
