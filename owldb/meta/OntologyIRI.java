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

package owldb.meta;

import org.semanticweb.owlapi.model.*;


/**
 * The meta object for an ontologies IRI.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 */
public class OntologyIRI
{
	private long	id;
	private IRI		ontologyIRI;


	/**
	 * Default Constructor.
	 */
	public OntologyIRI ()
	{
		// Empty by intention
	}


	/**
	 * Convenience constructor.
	 * 
	 * @param ontologyIRI The IRI of the ontology
	 */
	public OntologyIRI (final IRI ontologyIRI)
	{
		this.setOntologyIRI (ontologyIRI);
	}


	/**
	 * Constructor.
	 * 
	 * @param ontologyID The ontology ID
	 */
	public OntologyIRI (final OWLOntologyID ontologyID)
	{
		this.ontologyIRI = ontologyID.getOntologyIRI ();
	}


	/**
	 * Get the ID.
	 * 
	 * @return The id
	 */
	public long getId ()
	{
		return this.id;
	}


	/**
	 * Set the ID.
	 * 
	 * @param id the id to set
	 */
	public void setId (final long id)
	{
		this.id = id;
	}


	/**
	 * Get the ontologies IRI.
	 * 
	 * @return The ontologies IRI
	 */
	public IRI getOntologyIRI ()
	{
		return this.ontologyIRI;
	}


	/**
	 * Set the ontology URI.
	 * 
	 * @param ontologyIRI The ontologies IRI to set
	 */
	public final void setOntologyIRI (final IRI ontologyIRI)
	{
		this.ontologyIRI = ontologyIRI;
	}


	/**
	 * Create an ontology ID from the URI.
	 * 
	 * @return An ontology ID
	 */
	public OWLOntologyID getOntologyID ()
	{
		return new OWLOntologyID (this.ontologyIRI);
	}
}
