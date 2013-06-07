/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since July 26, 2012
 * @version 0.2.0-prototype
 */

package utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import play.Logger;
import play.Play;
import sparqldl.Query;
import sparqldl.QueryEngine;
import sparqldl.QueryResult;

/**
 * Provides common interfaces to work with ontologies.
 * TODO: add timeout based on Pellet KB's internal timers
 */
public class KnowledgeManager {
	
	private static KnowledgeManager _instance;
	
	public OWLOntologyManager manager;
	public OWLOntology ontology;
	public OWLDataFactory factory;
	public PrefixManager prefixManager;
    public String prefix;
	public IRI iri;
	
	public OWLReasoner reasoner;
	public Reasoner hermit;
	
	private LocalizedAnnotationSelector annotationSelector;

	private QueryEngine queryEngine;
		
	public KnowledgeManager() {
		try {
			loadOntology(getOntologyFilePath());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static KnowledgeManager getInstance() {
		
		if (_instance == null) {
			_instance = new KnowledgeManager();
		}
		
		return _instance;
	}
	
	
	public QueryResult sparqldl(String query) throws Exception {
		// Execute the query and generate the result set
		QueryResult result = queryEngine.execute(query);
		
		// Check if anything provided as result, otherwise return null
		if (!result.ask())
			return null;
		
		return result;

	}
	
	public void loadOntology(String ontologyFilePath) throws Exception {
		
        // Get hold of an ontology manager
        manager = OWLManager.createOWLOntologyManager();

        File owlFile = new File(ontologyFilePath);
		
	    // Load default ontology from the file
        ontology = manager.loadOntologyFromOntologyDocument(owlFile);
        System.out.println("Loading ontology: " + ontology);

        // Initialize HermiT reasoner
        hermit = new Reasoner(ontology);
		
        // Create an instance of an OWL API built-in StructuralReasoner
		reasoner = new StructuralReasonerFactory().createReasoner(ontology);
        
		// show consistency checking results for both HermiT and build-in
		System.out.println("Ontology consistency (HermiT): " + hermit.isConsistent());
        System.out.println("Ontology consistency (built-in): " + reasoner.isConsistent());
        
        // Optionally let the reasoner compute the most relevant inferences in advance
		reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS, InferenceType.OBJECT_PROPERTY_ASSERTIONS);
        
		// Create an instance of the SPARQL-DL query engine
		queryEngine = QueryEngine.create(manager, reasoner, true);

        // obtain the location where the ontology was loaded from
        iri = manager.getOntologyDocumentIRI(ontology);

        // defines IRI prefix for accessing entities
        prefix = ontology.getOntologyID().getOntologyIRI() + "#";
        
        prefixManager = new DefaultPrefixManager(prefix);
        
        // The data factory provides a point for creating OWL API objects such as classes, properties and individuals.
        factory = manager.getOWLDataFactory();

        // Add mapping for the local ontology
        manager.addIRIMapper(new SimpleIRIMapper(iri, IRI.create(owlFile)));
        
        //TODO: read languages from configs
        annotationSelector = new LocalizedAnnotationSelector(ontology, factory, "fa", "en");
    	
	}
	
	public void reloadOntology() throws Exception {

		// Remove the ontology from model, so we can reload it
        manager.removeOntology(ontology);
        loadOntology(getOntologyFilePath());

	}
	
	/**
	 * Returns an String holding file path for the loaded ontology.
	 * @return
	 */
	public String getOntologyFilePath() {
		String ontologyFilePath = Play.configuration.getProperty("urs.OntologiesDirectory", "ontologies");
		ontologyFilePath += "/" + Play.configuration.getProperty("urs.Ontology.FileName", "ReportOntology.owl");
		return ontologyFilePath;
	}

	/**
	 * Returns a file for the loaded ontology.
	 * @return
	 */
	public File getOntologyFile() {
		String ontologyFilePath = Play.configuration.getProperty("urs.OntologiesDirectory", "ontologies");
		ontologyFilePath += "/" + Play.configuration.getProperty("urs.Ontology.FileName", "ReportOntology.owl");
		return new File(ontologyFilePath);
	}

	/**
	 * Returns an String holding file path for the protege
	 * project file of the ontology.
	 * @return
	 */
	public String getProtegeProjectFilePath() {
		String ontologyFilePath = Play.configuration.getProperty("urs.OntologiesDirectory", "ontologies");
		ontologyFilePath += "/" + Play.configuration.getProperty("urs.Ontology.ProjectFileName", "ReportOntology.pprj");
		return ontologyFilePath;
	}
	
	
	/**
	 * Provides subclasses of a desired class from ontology taxonomy using reasoner.
	 * @param className desired class to be queried
	 * @return List of subclasses names in String format
	 * @throws Exception
	 */
	public final List<String> getSubclasses(String className) throws Exception {
		List<String> result = new ArrayList<String>();
				
		OWLClass cls = factory.getOWLClass(className, prefixManager);
		Set<OWLClassExpression> subClasses = cls.getSubClasses(ontology);
		
		for (OWLClassExpression exp : subClasses) {
			if (!exp.isAnonymous())
				result.add(getBrowserText(exp.asOWLClass()));
		}
		
		return result;

	}
	
	public final OWLClass getOWLClass(String className) {
		return factory.getOWLClass(className, prefixManager);
	}
	
	public final String getBrowserText(OWLClass cls) {
		return annotationSelector.getBrowserText(cls);
	}

	public final String getBrowserText(OWLNamedIndividual ind) {
		return annotationSelector.getBrowserText(ind);
	}

	public final String getLabel(OWLClass cls) {
		return annotationSelector.getLabel(cls);
	}

	public final String getLabel(String className) {
		OWLClass cls = getOWLClass(className);
		return getLabel(cls);
	}

	public final String getLabel(OWLNamedIndividual ind) {
		return annotationSelector.getLabel(ind);
	}

	public final String getDescription(OWLClass cls) {
		return annotationSelector.getDescription(cls);
	}
	
	public final String getDescription(String className) {
		OWLClass cls = getOWLClass(className);
		return getDescription(cls);
	}

	/**
	 * Reads versionInfo annotation from the ontology model.
	 * @return A string representing semantic version of the ontology
	 */
	public String version() {
		//TODO: read owl:versionInfo annotation property instead
		IRI versionIRI = ontology.getOntologyID().getVersionIRI();
		if (versionIRI == null)
			return "0.0.0";
		return versionIRI.toString();
	}
}
