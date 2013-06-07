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
import java.util.*;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;


/**
 * Test for loading classes from the FMA ontology.
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 */
public final class HierarchyBrowser
{
	static OWLMutableOntology	onto	= null;


	/**
	 * Loads the ontology class Anatomical_entity from the FMA ontology (running
	 * in a local MySql database which must be accessible via
	 * <em>jdbc:mysql://localhost/fmadb</em>) and prints all of its sub classes.
	 * 
	 * @param args Unused
	 * @throws OWLOntologyCreationException Could not create the ontology object
	 */
	public static void main (final String [] args) throws OWLOntologyCreationException
	{
		// At first we need an OWLOntologyManager for handling our ontologies
		final OWLOntologyManager manager = OWLDBManager.createOWLOntologyManager (OWLDataFactoryImpl.getInstance ());
		// This is the physical URI which will be used to load the ontology
		// It refers to a MySQL database already containing an ontology.
		final IRI dbIRI = IRI.create ("jdbc:mysql://localhost/fmadb");
		// URI dbURI =
		// URI.create("file:///Projekte/Hibernatekram/Ontologies/fma20OwlDlComponent_v1.4.owl");
		// Create a PhysicalURIInputSource with our provided URI
		// Do the loading using our inputsource
		onto = (OWLMutableOntology) manager.loadOntology (dbIRI);

		// This factory is required for the creation of axioms and entities
		final OWLDataFactory factory = manager.getOWLDataFactory ();

		final OWLClass rootClass = factory.getOWLClass (IRI.create ("http://bioontology.org/projects/ontologies/fma/fma20OwlDlComponent#Anatomical_entity"));
		printSubClasses (rootClass, 0);
	}


	/**
	 * Print all sub classes of the given class.
	 * 
	 * @param owlClass The class for which to print the sub classes
	 * @param level The level of sub classes to print
	 */
	public static void printSubClasses (final OWLClass owlClass, final int level)
	{
		if (level > 6)
			return;

		final Set<OWLSubClassOfAxiom> subclasses = onto.getSubClassAxiomsForSuperClass (owlClass);

		final StringBuilder spaceBuilder = new StringBuilder ();
		for (int i = 0; i < level; i++)
			spaceBuilder.append ('|');
		spaceBuilder.append ('+');
		final String spacer = spaceBuilder.toString ();

		for (final OWLSubClassOfAxiom subClassAx: subclasses)
		{
			final OWLClassExpression subClassAxiom = subClassAx.getSubClass ();
			if (!subClassAxiom.isAnonymous ())
			{
				final OWLClass subClass = subClassAxiom.asOWLClass ();
				System.out.println (spacer + subClass);
				printSubClasses (subClass, level + 1);
			}
		}
	}


	/** Due to final. */
	private HierarchyBrowser ()
	{
		// Empty by intention
	}
}
