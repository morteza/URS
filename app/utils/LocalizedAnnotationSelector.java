package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.model.OWLEntity;

import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;

/** 
 * Helper class for extracting labels, comments and other annotations in preferred languages. 
 * Selects the first literal annotation matching the given languages in the given order. 
 */ 
public class LocalizedAnnotationSelector { 
	private final List<String> langs; 
	private final OWLOntology ontology; 
	private final OWLDataFactory factory; 
	private final OWLObjectRenderer renderer;

	/** 
	 * Constructor. 
	 * 
	 * @param ontology ontology 
	 * @param factory  data factory 
	 * @param langs    list of preferred languages; if none is provided the Locale.getDefault() is used 
	 */ 
	public LocalizedAnnotationSelector(OWLOntology ontology, OWLDataFactory factory, String... langs) { 
		this.langs = (langs == null) ? Arrays.asList(Locale.getDefault().toString()) : Arrays.asList(langs); 
		this.ontology = ontology; 
		this.factory = factory; 
		renderer = new DLSyntaxObjectRenderer();

	} 

	/** 
	 * Provides the first label in the first matching language. 
	 * 
	 * @param ind individual 
	 * @return label in one of preferred languages or null if not available 
	 */ 
	public String getLabel(OWLNamedIndividual ind) {
		String label = getAnnotationString(ind, OWLRDFVocabulary.RDFS_LABEL.getIRI()); 
		if (label == null) {
			return renderer.render(ind);
		}
		return label;
	} 

	public String getDescription(OWLNamedIndividual ind) { 
		String desc = getAnnotationString(ind, OWLRDFVocabulary.RDFS_COMMENT.getIRI()); 
		if (desc == null) {
			return renderer.render(ind);
		}
		return desc;		
	} 

	public String getAnnotationString(OWLNamedIndividual ind, IRI annotationIRI) { 
		return getLocalizedString(ind.getAnnotations(ontology, factory.getOWLAnnotationProperty(annotationIRI))); 
	} 

	/** 
	 * Provides the first label in the first matching language. 
	 * 
	 * @param cls OWL class 
	 * @return label in one of preferred languages or null if not available 
	 */ 
	public String getLabel(OWLEntity ent) { 
		String label = getAnnotationString(ent, OWLRDFVocabulary.RDFS_LABEL.getIRI());
		if (label == null) {
			return renderer.render(ent);
		}
		return label;
	} 

	public String getDescription(OWLEntity ent) { 
		String desc = getAnnotationString(ent, OWLRDFVocabulary.RDFS_COMMENT.getIRI());
		if (desc == null) {
			return renderer.render(ent);
		}
		return desc;
	} 

	public String getAnnotationString(OWLEntity ent, IRI annotationIRI) { 
		return getLocalizedString(ent.getAnnotations(ontology, factory.getOWLAnnotationProperty(annotationIRI))); 
	} 

	private String getLocalizedString(Set<OWLAnnotation> annotations) { 
		List<OWLLiteral> literalLabels = new ArrayList<OWLLiteral>(annotations.size()); 
		for (OWLAnnotation label : annotations) {
			if (label.getValue() instanceof OWLLiteral) {

				literalLabels.add((OWLLiteral) label.getValue()); 
			} 
		} 
		for (String lang : langs) { 
			for (OWLLiteral literal : literalLabels) { 
				if (literal.hasLang(lang)) return literal.getLiteral(); 
			} 
		} 
		for (OWLLiteral literal : literalLabels) { 
			if (!literal.hasLang()) return literal.getLiteral(); 
		} 
		return null; 
	}

	public String getBrowserText(OWLClass cls) {
		return renderer.render(cls);
	}

	public String getBrowserText(OWLIndividual ind) {
		return ind.toStringID();
	}

}