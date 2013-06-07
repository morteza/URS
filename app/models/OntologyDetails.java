/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since October 19, 2012
 * @version 1.0.0
 */


package models;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import utils.KnowledgeManager;
import utils.ReportUtils;
import utils.Version;

public class OntologyDetails {
	
	public String version;
	public String iri;
	
	public int axioms;
	public int logicalAxioms;
	
	public List<String> reportTypes;
	public List<String> reportTypeTitles;
	public List<Integer> numOfSubmittedReports;
	public int numOfAllSubmittedReports;
	public List<String> reportFields;
	public List<Integer> numOfSubmittedReportFields;
	
	public List<String> reasoners;
	public List<String> consistencyResults;
	
	/**
	 * Extract some details from the loaded ontology (e.g. version, size, etc)
	 */
	
	public OntologyDetails() throws Exception {
		OWLOntology ont = KnowledgeManager.getInstance().ontology;
		
		// Overall ontology-related details
		iri = ont.getOntologyID().getOntologyIRI().toString();
		version = Version.parse(KnowledgeManager.getInstance().version()).toString();
		axioms = ont.getAxiomCount();
		logicalAxioms = ont.getLogicalAxiomCount();
		reportTypes = ReportUtils.getInstance().getReportTypes();
		
		
		// Extract logical details
		reasoners = new ArrayList<String>();
		consistencyResults = new ArrayList<String>();
		
		// HermiT
		if (KnowledgeManager.getInstance().hermit!=null) {
			reasoners.add("HermiT");
			// Check consistency for hermit
			if (KnowledgeManager.getInstance().hermit.isConsistent()) {
				consistencyResults.add("TRUE");
			} else {
				consistencyResults.add("FALSE");
			}
		}
		
		// Structured Reasoner
		if (KnowledgeManager.getInstance().reasoner!=null) {
			reasoners.add("Built-in Structured");
			// Check consistency for hermit
			if (KnowledgeManager.getInstance().reasoner.isConsistent()) {
				consistencyResults.add("TRUE");
			} else {
				consistencyResults.add("FALSE");
			}
		}
		
		
		// Extract report-related details from the loaded ontology
		reportTypeTitles = new ArrayList<String>();
		numOfSubmittedReports = new ArrayList<Integer>();
		numOfAllSubmittedReports = 0;
		
		OWLClass cls;
		String clsId;
		for(int i = 0 ; i<reportTypes.size() ; i++) {
			clsId = reportTypes.get(i);
			reportTypeTitles.add(KnowledgeManager.getInstance().getLabel(clsId));
			
			cls = KnowledgeManager.getInstance().getOWLClass(clsId);
			int cSize = cls.getIndividuals(ont).size();
			
			// Report types includes themselves as individuals, so ignore one from the overall counts
			cSize = cSize -1;
			numOfSubmittedReports.add(cSize);
			numOfAllSubmittedReports += cSize;
		}
		
		// Extract report fields
		reportFields = new ArrayList<String>();
		numOfSubmittedReportFields = new ArrayList<Integer>();
		cls = KnowledgeManager.getInstance().getOWLClass(":ReportField");
		for (OWLClassExpression eCls : cls.getSubClasses(ont)) {
			if (!eCls.isAnonymous()) {
				OWLClass fCls = eCls.asOWLClass();
				reportFields.add(KnowledgeManager.getInstance().getLabel(fCls));
				numOfSubmittedReportFields.add(fCls.getIndividuals(ont).size()-1);
			}
		}

	}
}
