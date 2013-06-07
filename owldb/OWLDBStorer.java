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
 * An implementation of the Storer interface that is capable of storing
 * ontologies in a database.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 * @author Yongchun Xu; (FZI)
 */
public class OWLDBStorer implements OWLOntologyStorer
{
	/** {@inheritDoc} */
	@Override
	public boolean canStoreOntology (final OWLOntologyFormat ontologyFormat)
	{
		return ontologyFormat instanceof OWLDBOntologyFormat;
	}


	/** {@inheritDoc} */
	@Override
	public void storeOntology (final OWLOntologyManager manager, final OWLOntology ontology, final OWLOntologyDocumentTarget target, final OWLOntologyFormat format) throws OWLOntologyStorageException
	{
		if (!target.isDocumentIRIAvailable ())
			throw new OWLOntologyStorageException ("No IRI could be obtained to store the ontology: " + ontology.getOntologyID ());

		this.storeOntology (manager, ontology, target.getDocumentIRI (), format);
	}


	/** {@inheritDoc} */
	@Override
	public void storeOntology (final OWLOntologyManager manager, final OWLOntology ontology, final IRI documentIRI, final OWLOntologyFormat ontologyFormat) throws OWLOntologyStorageException
	{
		try
		{
			final OWLMutableOntology newOnto = (OWLMutableOntology) manager.createOntology (documentIRI);
			newOnto.applyChange (new SetOntologyID (newOnto, ontology.getOntologyID ()));
			final Set<OWLAxiom> axioms = ontology.getAxioms ();
			manager.addAxioms (newOnto, axioms);
		}
		catch (final OWLOntologyCreationException e)
		{
			throw new OWLOntologyStorageException (e);
		}
		catch (final OWLOntologyChangeException e)
		{
			throw new OWLOntologyStorageException (e);
		}
	}


	/**
	 * Store the ontology as the database format.
	 * 
	 * @param manager the ontology manager
	 * @param ontology the ontology
	 * @param targetIRI the target IRI
	 * @param props the database property
	 * @param format the Ontology format
	 * @throws OWLOntologyCreationException will be thrown if there is error in
	 *           Ontology creation
	 * @throws OWLOntologyStorageException will be thrown if the ontology format
	 *           is not OWLDB
	 */
	public void storeOntology (final OWLDBOntologyManager manager, final OWLOntology ontology, final IRI targetIRI, final Properties props, final OWLOntologyFormat format) throws OWLOntologyCreationException, OWLOntologyStorageException
	{
		if (!(format instanceof OWLDBOntologyFormat))
			throw new OWLOntologyStorageException ("The Ontology can be saved only in owldb format but was " + (format == null ? "null" : format.getClass ().getName ()));

		final OWLOntologyID ontologyID = new OWLOntologyID (targetIRI);
		final OWLOntology newOnto = manager.createOntology (ontologyID, props);
		// We need to change the ontology ID now because originally it points to the
		// database connection string
		final OWLOntologyID realOntologyID = ontology.getOntologyID ();
		((OWLMutableOntology) newOnto).applyChange (new SetOntologyID (newOnto, realOntologyID));

		final Set<OWLAxiom> axioms = ontology.getAxioms ();
		manager.addAxioms (newOnto, axioms);
	}
}
