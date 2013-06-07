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
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * FmaContoUtilities: 1. Allows for FMA ontology loading via DB and OWLApi
 * {@link #loadFMAOntologyFromDB()} 2. Allows for FMA ontology loading via DB
 * and OWLApi {@link #loadFMAOntologyFromFile()} 3. Store Conto ontology in DB
 * {@link #storeContoInDB()}
 * 
 * Also use main method {@link #main(String[])}
 * 
 * @author Joachim Kleb (FZI)
 * @version 1.0
 */
public final class FmaContoUtilities
{
	static long	startTime;


	/**
	 * Load the FMA ontology from a datatbase.
	 * 
	 * @throws OWLOntologyCreationException Crash
	 */
	public static void loadFMAOntologyFromDB () throws OWLOntologyCreationException
	{
		// At first we need an OWLOntologyManager for handling our ontologies
		final OWLOntologyManager manager = OWLDBManager.createOWLOntologyManager (OWLDataFactoryImpl.getInstance ());
		// This is the physical URI which will be used to load the ontology
		// It refers to a MySQL database already containing an ontology.
		System.out.println ("Begin of ontology loading!");
		final IRI dbIRI = IRI.create ("jdbc:mysql://localhost/fmadb");
		// Create a PhysicalURIInputSource with our provided URI
		System.out.println ("Ontology to load: " + dbIRI);
		// Do the loading using our inputsource
		startTime = System.currentTimeMillis ();
		System.out.println ("Timer: 0sec");
		System.out.println ("Loading completed!");
		System.out.println ("Timer: " + (System.currentTimeMillis () - startTime) / 1000 + "sec");

		final OWLMutableOntology onto = (OWLMutableOntology) manager.loadOntology (dbIRI);

		// This factory is required for the creation of axioms and entities
		final OWLDataFactory factory = manager.getOWLDataFactory ();

		final OWLClass thingClass = factory.getOWLClass (IRI.create ("http://bioontology.org/projects/ontologies/fma/fma20OwlDlComponent#Anatomical_entity"));
		System.out.println ("Top-concept: " + thingClass);
		getSubClassesRecursive (thingClass, onto, "|-");
	}


	/**
	 * Get all sub classes recursivly.
	 * 
	 * @param clazz The class for which to get the subclasses.
	 * @param onto The ontology which contains the classes
	 * @param tab Spacing
	 */
	private static void getSubClassesRecursive (final OWLClass clazz, final OWLOntology onto, final String tab)
	{
		for (final OWLClassExpression description: clazz.getSubClasses (onto))
		{
			final OWLClass subclazz = description.asOWLClass ();
			System.out.println (tab + subclazz);
			getSubClassesRecursive (subclazz, onto, tab.replace ("-", "|") + "|-");
		}
	}


	/**
	 * Load the FMA ontology from a file.
	 * 
	 * @throws OWLOntologyCreationException Crash
	 */
	public static void loadFMAOntologyFromFile () throws OWLOntologyCreationException
	{
		// At first we need an OWLOntologyManager for handling our ontologies
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager ();
		// We load an ontology from a physical URI - in this case we'll load the
		// pizza ontology.
		System.out.println ("Begin of ontology loading!");
		final IRI iri = IRI.create ("file:/" + System.getProperty ("user.dir").replace ("\\", "/") + "/Ontologies/fmaOwlDlComponent_2_0.owl");
		System.out.println ("Ontology to load: " + iri);
		// Now ask the manager to load the ontology
		startTime = System.currentTimeMillis ();
		System.out.println ("Timer: 0sec");
		final OWLOntology ontology = manager.loadOntology (iri);
		System.out.println ("Loading completed!");
		System.out.println ("Timer: " + (System.currentTimeMillis () - startTime) / 1000 + "sec");
		// Print out all of the classes which are referenced in the ontology
		for (final OWLClass cls: ontology.getClassesInSignature ())
			System.out.println (cls);
	}


	/**
	 * Stores contodb.owl in folder 'Ontologies' in database.
	 * 
	 * @throws OWLOntologyCreationException Crash
	 * @throws OWLOntologyStorageException Crash
	 */
	public static void storeContoInDB () throws OWLOntologyCreationException, OWLOntologyStorageException
	{
		// At first we need an OWLOntologyManager for handling our ontologies.
		final OWLOntologyManager manager = OWLDBManager.createOWLOntologyManager (OWLDataFactoryImpl.getInstance ());
		// We load an ontology from a physical URI - in this case we'll load the
		// wine ontology.
		final IRI iri = IRI.create ("file:/" + System.getProperty ("user.dir").replace ("\\", "/") + "/Ontologies/CONTO.owl");
		// Now ask the manager to load the ontology
		final OWLOntology ontology = manager.loadOntologyFromOntologyDocument (iri);

		// This is the physical URI which will be used as target for saving
		final IRI targetIri = IRI.create ("jdbc:mysql://localhost/contodb");
		// We must select the OWLDBOntologyFormat when storing in a database
		final OWLDBOntologyFormat format = new OWLDBOntologyFormat ();
		// Lets create a target with the provided target URI
		final OWLDBOntologyOutputTarget target = new OWLDBOntologyOutputTarget (targetIri);
		// Store the loaded ontology using our target and format
		manager.saveOntology (ontology, format, target);
	}


	/**
	 * Main method.
	 * 
	 * @param args - 'loadFMAOntologyFromFile' for
	 *          {@link #loadFMAOntologyFromFile()} - 'loadFMAOntologyFromDB' for
	 *          {@link #loadFMAOntologyFromDB()} - 'storeContoInDB' for
	 *          {@link #storeContoInDB()}
	 * @throws Exception Crash
	 */
	public static void main (final String [] args) throws Exception
	{
		if (args.length != 1)
		{
			System.out.println ("No parameters given. Either 'loadOntologyFromFile', 'loadOntologyFromDB' or 'storeContoInDB'");
			return;
		}

		if ("loadFMAOntologyFromFile".equals (args[0]))
			loadFMAOntologyFromFile ();
		else if ("loadFMAOntologyFromDB".equals (args[0]))
			loadFMAOntologyFromDB ();
		else if ("storeContoInDB".equals (args[0]))
			storeContoInDB ();
	}


	/** Due to final. */
	private FmaContoUtilities ()
	{
		// Empty by intention
	}
}
