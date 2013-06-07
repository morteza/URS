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

import de.uulm.ecs.ai.owlapi.krssrenderer.*;
import org.coode.owlapi.functionalrenderer.*;
import org.coode.owlapi.latex.*;
import org.coode.owlapi.obo.renderer.*;
import org.coode.owlapi.owlxml.renderer.*;
import org.coode.owlapi.rdf.rdfxml.*;
import org.coode.owlapi.rdfxml.parser.*;
import org.coode.owlapi.turtle.*;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import uk.ac.manchester.cs.owl.owlapi.*;
import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.*;


/**
 * Provides a point of convenience for creating an
 * <code>OWLDBOntologyManager</code> with commonly required features (such as an
 * RDF parser for example).
 * 
 * @author J&ouml;rg Hen&szlig; (KIT)
 */
public final class OWLDBManager
{
	static
	{
		// Register useful parsers
		final OWLParserFactoryRegistry registry = OWLParserFactoryRegistry.getInstance ();
		// registry.registerParserFactory(new KRSS2OWLParserFactory());
		// registry.registerParserFactory(new OBOParserFactory());
		// registry.registerParserFactory(new ManchesterOWLSyntaxParserFactory());
		// registry.registerParserFactory(new TurtleOntologyParserFactory());
		// registry.registerParserFactory(new OWLFunctionalSyntaxParserFactory());
		// registry.registerParserFactory(new OWLXMLParserFactory());
		registry.registerParserFactory (new RDFXMLParserFactory ());
	}


	/**
	 * Create the ontology manager and add ontology factories, mappers and
	 * storers.
	 * 
	 * @param dataFactory The data factory to use
	 * @return <code>OWLDBOntologyManager</code>
	 */
	public static OWLOntologyManager createOWLOntologyManager (final OWLDataFactory dataFactory)
	{
		final OWLOntologyManager ontologyManager = new OWLDBOntologyManager (dataFactory);
		ontologyManager.addOntologyStorer (new RDFXMLOntologyStorer ());
		ontologyManager.addOntologyStorer (new OWLXMLOntologyStorer ());
		ontologyManager.addOntologyStorer (new OWLFunctionalSyntaxOntologyStorer ());
		ontologyManager.addOntologyStorer (new ManchesterOWLSyntaxOntologyStorer ());
		ontologyManager.addOntologyStorer (new OBOFlatFileOntologyStorer ());
		ontologyManager.addOntologyStorer (new KRSS2OWLSyntaxOntologyStorer ());
		ontologyManager.addOntologyStorer (new TurtleOntologyStorer ());
		ontologyManager.addOntologyStorer (new LatexOntologyStorer ());
		ontologyManager.addOntologyStorer (new OWLDBStorer ());

		ontologyManager.addIRIMapper (new NonMappingOntologyIRIMapper ());

		ontologyManager.addOntologyFactory (new EmptyInMemOWLOntologyFactory ());
		ontologyManager.addOntologyFactory (new ParsableOWLOntologyFactory ());
		ontologyManager.addOntologyFactory (new OWLDBOntologyFactory ());

		return ontologyManager;
	}


	/** Due to final. */
	private OWLDBManager ()
	{
		// Empty by intention
	}
}
