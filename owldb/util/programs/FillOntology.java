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

package owldb.util.programs;

import owldb.*;
import java.io.*;
import java.util.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * Stores one or more OWL files into an OWLDB database.
 * 
 * @author J&uuml;rgen Mo&szlig;graber (Fraunhofer IOSB)
 */
public final class FillOntology
{
	/**
	 * Main method.
	 * 
	 * @param args A number of ontology files
	 */
	public static void main (final String [] args)
	{
		try
		{
			if (args.length < 2)
			{
				System.out.println ("Usage: jdbc_connect_string file1.owl file2.owl ... fileN.owl");
				return;
			}

			// Collect all given files
			final Collection<File> ontologyFiles = new ArrayList<File> ();
			for (int i = 1; i < args.length; i++)
			{
				final File owlFile = new File (args[i]);
				if (!owlFile.exists ())
				{
					System.out.println ("The given ontoloy file '" + owlFile.getAbsolutePath () + "' was not found. Exiting.");
					return;
				}
				ontologyFiles.add (owlFile);
			}

			// Load all files
			final OWLOntologyManager manager = OWLDBManager.createOWLOntologyManager (OWLDataFactoryImpl.getInstance ());
			final OWLOntology ontology = loadOntologies (manager, ontologyFiles);
			System.out.println ("Axioms: " + ontology.getAxiomCount ());

			// Store the ontology
			System.out.println ("Connecting to: " + args[0]);
			final IRI targetIri = IRI.create (args[0]);
			final OWLDBOntologyOutputTarget target = new OWLDBOntologyOutputTarget (targetIri);
			final OWLDBOntologyFormat format = new OWLDBOntologyFormat ();
			System.out.println ("Storing ontology...");
			manager.saveOntology (ontology, format, target);
			System.out.println ("Done.");
		}
		catch (final Throwable t)
		{
			t.printStackTrace ();
		}
	}


	/**
	 * Load the given ontology files into one ontology object.
	 * 
	 * @param manager The ontology manager
	 * @param ontologyFiles The OWL files to load
	 * @return The ontology object containing all loaded ontologies
	 * @throws OWLOntologyCreationException Could not create the ontology
	 */
	public static OWLOntology loadOntologies (final OWLOntologyManager manager, final Collection<File> ontologyFiles) throws OWLOntologyCreationException
	{
		OWLOntology onto = null;
		for (final File curFile: ontologyFiles)
		{
			System.out.print ("Loading " + curFile.getAbsolutePath ());
			final OWLOntology loaded = manager.loadOntologyFromOntologyDocument (curFile);
			final Set<OWLAxiom> axioms = loaded.getAxioms ();
			System.out.println (" (" + axioms.size () + " axioms)");
			if (onto == null)
				onto = loaded;
			else
				manager.addAxioms (onto, axioms);
		}
		return onto;
	}


	/** Due to final. */
	private FillOntology ()
	{
		// Empty by intention
	}
}
