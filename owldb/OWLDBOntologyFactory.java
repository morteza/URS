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

package owldb;

import java.util.*;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;


/**
 * Ontology factory for <code>OWLDBOntology</code>.
 * 
 * @author Yongchun Xu (FZI)
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 */
public class OWLDBOntologyFactory implements OWLOntologyFactory
{
	private OWLOntologyManager				ontologyManager;
	private final static Set<String>	SUPPORTED_SCHEMES	= new HashSet<String> ();

	static
	{
		SUPPORTED_SCHEMES.add ("jdbc");
	}


	/** {@inheritDoc} */
	@Override
	public void setOWLOntologyManager (final OWLOntologyManager owlOntologyManager)
	{
		this.ontologyManager = owlOntologyManager;
	}


	/** {@inheritDoc} */
	@Override
	public boolean canCreateFromDocumentIRI (final IRI documentIRI)
	{
		return SUPPORTED_SCHEMES.contains (documentIRI.getScheme ());
	}


	/** {@inheritDoc} */
	@Override
	public boolean canLoad (final OWLOntologyDocumentSource documentSource)
	{
		return SUPPORTED_SCHEMES.contains (documentSource.getDocumentIRI ().getScheme ());
	}


	/** {@inheritDoc} */
	@Override
	public OWLOntology createOWLOntology (final OWLOntologyID ontologyID, final IRI documentIRI, final OWLOntologyCreationHandler handler) throws OWLOntologyCreationException
	{
		final Properties props = new Properties ();
		return this.createOWLOntology (ontologyID, documentIRI, handler, props);
	}


	/**
	 * Creates an (empty) ontology.
	 * 
	 * @param ontologyID The ontologyURI
	 * @param iri The physical URI
	 * @param handler The Handler
	 * @param props The Properties used for Hibernate
	 * @return The created Ontology
	 * @throws OWLOntologyCreationException Crash
	 */
	public OWLOntology createOWLOntology (final OWLOntologyID ontologyID, final IRI iri, final OWLOntologyCreationHandler handler, final Properties props) throws OWLOntologyCreationException
	{
		props.setProperty ("hibernate.connection.url", iri.toString ());
		props.setProperty ("hibernate.hbm2ddl.auto", "create");

		final OWLOntology instance = new OWLDBOntology (ontologyID, iri, this.ontologyManager, props);
		handler.ontologyCreated (instance);
		handler.setOntologyFormat (instance, new OWLDBOntologyFormat ());
		return instance;
	}


	/** {@inheritDoc} */
	@Override
	public OWLOntology loadOWLOntology (final OWLOntologyDocumentSource documentSource, final OWLOntologyCreationHandler handler) throws OWLOntologyCreationException
	{
		return this.loadOWLOntology (documentSource, handler, new Properties ());
	}


	/** {@inheritDoc} */
	@Override
	public OWLOntology loadOWLOntology (final OWLOntologyDocumentSource documentSource, final OWLOntologyCreationHandler handler, final OWLOntologyLoaderConfiguration configuration) throws OWLOntologyCreationException
	{
		// configuration parameter is only needed for configuring a parser
		return this.loadOWLOntology (documentSource, handler, new Properties ());
	}


	/**
	 * Load an existing ontology.
	 * 
	 * @param inputSource The InputSource
	 * @param handler The Handler to notifie
	 * @param props The Properties used for Hibernate
	 * @return The loaded Ontology
	 * @throws OWLOntologyCreationException Crash
	 */
	public OWLOntology loadOWLOntology (final OWLOntologyDocumentSource inputSource, final OWLOntologyCreationHandler handler, final Properties props) throws OWLOntologyCreationException
	{
		final IRI iri = inputSource.getDocumentIRI ();
		props.setProperty ("hibernate.connection.url", iri.toString ());

		final OWLOntology instance = new OWLDBOntology (null, iri, this.ontologyManager, props);
		final OWLOntologyID ontologyID = instance.getOntologyID ();
		if (ontologyID == null)
			throw new OWLOntologyCreationException ("The ontology with the IRI '" + iri.toString () + "' does not exist in the database.");

		handler.ontologyCreated (instance);
		handler.setOntologyFormat (instance, new OWLDBOntologyFormat ());
		return instance;
	}


	/**
	 * Returns current instance of the ontology manager.
	 */
	@Override
	public OWLOntologyManager getOWLOntologyManager() {

		return this.ontologyManager;
	}
}
