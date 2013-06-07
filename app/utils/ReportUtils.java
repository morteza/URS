/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since Aug 1, 2012
 * @version 0.3.0-PROTOTYPE
 */

package utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Configuration;
import models.User;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.NodeSet;

import sparqldl.QueryArgument;
import sparqldl.QueryBinding;
import sparqldl.QueryResult;
import sparqldl.types.QueryArgumentType;
import utils.html_builders.Builder;

import play.Logger;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Router;

/**
 *
 */
public class ReportUtils implements Constants{
	
	private static ReportUtils _instance;
	
	public static ReportUtils getInstance() {
		
		if (_instance == null) {
			_instance = new ReportUtils();
		}
		
		return _instance;
	}
	
	public ReportUtils() {
		
	}
		
	public List<String> getReportTypes() throws Exception{
		return KnowledgeManager.getInstance().getSubclasses(":Report");
	}

/*
	public List<String> getReportHTMLFields(String reportClassName) throws Exception{
		List<String> result = new ArrayList<String>();
		
		//GET all fields SPARQL query		
		String query = "SELECT ?field "
						+ "WHERE { :"
						+ reportClassName.trim()
						+ " rdfs:subClassOf ?restriction . "
						+ " ?restriction owl:onProperty :hasField . "
						+ " ?restriction owl:valuesFrom ?field}";
		
		QueryResults qResults, qTypes;

		qResults = KnowledgeManager.getInstance().query(query);
		while (qResults.hasNext()) {
			OWLClass cls = (OWLClass) qResults.next().get("field");

			query = "SELECT ?t "
					 + "WHERE { :"
					 + cls.getBrowserText()
					 + " rdfs:subClassOf ?restriction . "
					 + "?restriction owl:onProperty :hasType . "
					 + "?restriction owl:valuesFrom ?t}";

			qTypes = KnowledgeManager.getInstance().query(query);
			OWLClass htmlCls = (OWLClass) qTypes.next().get("t");

			//TODO: read htmlCls annotation for HTML template
			String htmlTemplate = "";
			//TODO: replace with correct parameters
			result.add(htmlCls.getBrowserText());
		}

	return result;
	
	}
*/	
	/**
	 * Generate HTML form fields for a given report.
	 * @param reportClassName OWL concept name for the desired report.
	 * @return An string includes form fields
	 */
	public static String generateNewReportFormHTML(String reportClassName) throws Exception {
		
		// set leading prefix of ':' for class names
		if ( !reportClassName.startsWith(":") )
			reportClassName = ":" + reportClassName.trim();
		
		String htmlResult = "";
		
		//GET all fields SPARQL-DL query		
		String query = "PREFIX : <http://itrc.ac.ir/ReportOntology#>\n";
        query += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";

		query += "SELECT ?field { PropertyValue(" + reportClassName + ", :hasField, ?field), SubClassOf(?field, :ReportField)}";

		QueryResult result = KnowledgeManager.getInstance().sparqldl(query);

		Iterator<QueryBinding> iterator = result.iterator();
		QueryArgument arg = new QueryArgument(QueryArgumentType.VAR, "field");
		QueryBinding binding;
		
		while (iterator.hasNext()) {
			binding = iterator.next();

			// get 'field' value of this query result
			String fieldIRI = binding.get(arg).getValue();

			// get corresponding OWLClass
			OWLClass cls = KnowledgeManager.getInstance().factory.getOWLClass(IRI.create(fieldIRI));
			
			htmlResult += getReportFieldHTML(cls);
		}
		return htmlResult;
	}
	
	public static void setReportAccepted(String reportId) throws Exception {
		//TODO remove RejectedReport subClass axiom if any
		OWLDataFactory factory = KnowledgeManager.getInstance().factory;
		PrefixManager pm = KnowledgeManager.getInstance().prefixManager;
		
		OWLClass acceptedReport = factory.getOWLClass(":AcceptedReport", pm);

		OWLNamedIndividual ind = KnowledgeManager.getInstance().factory.getOWLNamedIndividual(IRI.create(reportId));

		// Add subClassOf AcceptedReport axiom
		// Add report individual to the KB (TODO: use KBManager instead of KnowledgeManager)
		OWLClassAssertionAxiom rAxiom = factory.getOWLClassAssertionAxiom(acceptedReport, ind);
		KnowledgeManager.getInstance().manager.addAxiom(KnowledgeManager.getInstance().ontology, rAxiom);

		KnowledgeManager.getInstance().manager.saveOntology(KnowledgeManager.getInstance().ontology);

	}
	
	public static void setReportRejected(String reportId) throws Exception {
		//TODO remove AcceptedReport subClass axiom if any
		OWLDataFactory factory = KnowledgeManager.getInstance().factory;
		PrefixManager pm = KnowledgeManager.getInstance().prefixManager;
		
		OWLClass acceptedReport = factory.getOWLClass(":RejectedReport", pm);

		OWLNamedIndividual ind = KnowledgeManager.getInstance().factory.getOWLNamedIndividual(IRI.create(reportId));

		// Add subClassOf RejectedReport axiom
		// Add report individual to the KB (TODO: use KBManager instead of KnowledgeManager)
		OWLClassAssertionAxiom rAxiom = factory.getOWLClassAssertionAxiom(acceptedReport, ind);
		KnowledgeManager.getInstance().manager.addAxiom(KnowledgeManager.getInstance().ontology, rAxiom);

		KnowledgeManager.getInstance().manager.saveOntology(KnowledgeManager.getInstance().ontology);
	}
	
	public static String generateAllReportsHTML() throws Exception {
		String htmlResult = "";
		
		//GET all fields SPARQL-DL query		
		String query = "PREFIX : <http://itrc.ac.ir/ReportOntology#>\n";
        query += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";

		query += "SELECT ?report { Type(?report,:SubmittedReport) }";

		QueryResult result = KnowledgeManager.getInstance().sparqldl(query);

		Iterator<QueryBinding> iterator = result.iterator();
		QueryArgument arg = new QueryArgument(QueryArgumentType.VAR, "report");
		QueryBinding binding;
		
		htmlResult += 
				"<thead><tr><th>" + Messages.get("urs.admin.reportDetails.reportId") + "</th>"
				+ "<th>" + Messages.get("urs.admin.reportDetails.reportTypes") + "</th>"
				+ "<th>" + Messages.get("urs.admin.reportDetails.fieldSize") + "</th>"
				+ "<th>" + Messages.get("urs.admin.reportDetails.submittedDate") + "</th>"
				+ "<th colspan=\"2\" style=\"width:10px;\">" + Messages.get("urs.admin.reportDetails.state") + "</th>"
				+ "</tr></thead><tbody>";
		
		OWLObjectProperty hasField = KnowledgeManager.getInstance().factory.getOWLObjectProperty("hasField", KnowledgeManager.getInstance().prefixManager);
		OWLObjectProperty hasReportType = KnowledgeManager.getInstance().factory.getOWLObjectProperty("hasReportType", KnowledgeManager.getInstance().prefixManager);
		OWLDataProperty hasContent = KnowledgeManager.getInstance().factory.getOWLDataProperty("hasContent", KnowledgeManager.getInstance().prefixManager);
		
		while (iterator.hasNext()) {
			binding = iterator.next();

			// get 'field' value of this query result
			String fieldIRI = binding.get(arg).getValue();

			// get corresponding individual
			OWLNamedIndividual ind = KnowledgeManager.getInstance().factory.getOWLNamedIndividual(IRI.create(fieldIRI));
			
			boolean isAccepted = false;
			boolean isRejected = false;
			for (OWLClassExpression exp : ind.getTypes(KnowledgeManager.getInstance().ontology)) {
        		if (!exp.isAnonymous()) {
        			OWLClass expCls = exp.asOWLClass();
        			String strCls = KnowledgeManager.getInstance().getBrowserText(expCls);
        			if ("AcceptedReport".equals(strCls)) {
        				isAccepted = true;
        				break;
        			} else if ("RejectedReport".equals(strCls)) {
        				isRejected = true;
        				break;
        			}
        		}
        	}			
			// set row color based on accepted or rejected
			if (isAccepted)
				htmlResult += "<tr class=\"success\">";
			else if (isRejected)
				htmlResult += "<tr class=\"error\">";
			else
				htmlResult += "<tr class=\"\">";

				
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("reportId", KnowledgeManager.getInstance().getBrowserText(ind));
	        String route = Router.getFullUrl("Administration.showReportDetails", map);
	        htmlResult += "<td nowrap><a href=\"" + route + "\">" + KnowledgeManager.getInstance().getLabel(ind) + "</a></td>";

	        Set<OWLIndividual> fields = ind.getObjectPropertyValues(hasField, KnowledgeManager.getInstance().ontology);
	        
	        htmlResult += "<td>";
	        Set<OWLIndividual> types = ind.getObjectPropertyValues(hasReportType, KnowledgeManager.getInstance().ontology);
	        for (OWLIndividual type : types) {
	        	htmlResult += KnowledgeManager.getInstance().getLabel((OWLNamedIndividual)type) + "<br />";
	        }
	        htmlResult += "</td>";
	        
	        htmlResult += "<td>" + fields.size() + "</td>";

	        for (OWLIndividual f : fields) {
	        	for (OWLClassExpression exp : f.getTypes(KnowledgeManager.getInstance().ontology)) {
	        		if (!exp.isAnonymous()) {
	        			OWLClass expCls = exp.asOWLClass();
	        			String strCls = KnowledgeManager.getInstance().getBrowserText(expCls);
	        			if ("SubmitDateTimeField".equals(strCls)) {
	        				String s = 
	        						((OWLLiteral)f.getDataPropertyValues(hasContent, KnowledgeManager.getInstance().ontology).toArray()[0]).getLiteral();
	        				
	        				//FIXME: find out date format from db or ontology instead of settings, then check for the appopriate one
	        				if ("Jalali".equalsIgnoreCase(Configuration.getValue(CONFIG_CALENDAR_TYPE, "Jalali")))
	        					s = JalaliDateConverter.georgianToPersian(s);

	        				htmlResult += "<td>" + s + "</td>";       				
	        			}
	        		}
	        	}
	        }

	        if (!isAccepted && !isRejected) {
		        htmlResult += "<td style=\"text-align:center;width:10px;vertical-align: middle; \"><a href=\""
		        		+ Router.getFullUrl("Administration.acceptReport",map)
		        		+ "#\" class=\"btn btn-success btn-mini\"><i class=\"icon-ok icon-white\"></i></a></td>";
				htmlResult += "<td style=\"text-align:center;width:1px;vertical-align: middle; \"><a href=\""
		        		+ Router.getFullUrl("Administration.rejectReport",map)
						+ "\" class=\"btn btn-danger btn-mini\"><i class=\"icon-remove icon-white\"></i></a></td>";	        	
	        } else {
	        	String state = Messages.get("urs.admin.reportDetails.pendingState");
	        	if (isAccepted)
	        		//state = Messages.get("urs.admin.reportDetails.acceptedState");
	        		state = "<i class=\"icon-ok\"></i>";
	        	else if (isRejected)
	        		//state = Messages.get("urs.admin.reportDetails.rejectedState");
        			state = "<i class=\"icon-remove\"></i>";
	        	
	        	htmlResult += "<td colspan=\"2\" style=\"text-align:center;vertical-align:middle;\">" + state + "</td>";
	        }

	        htmlResult += "</tr>";
		}
		
		htmlResult += "</tbody>";
		
		return htmlResult;
	}
	
	public static String generateReportDetailsHTML(final String reportId) {
		StringBuilder result = new StringBuilder();

		OWLNamedIndividual ind = KnowledgeManager.getInstance().factory.getOWLNamedIndividual(IRI.create(reportId));
		
		result.append("<tbody>");
		result.append("<tr><td><strong>");
		result.append(Messages.get("urs.admin.reportDetails.reportId"));
		result.append("</strong></td><td>");
		result.append(KnowledgeManager.getInstance().getLabel(ind));
		result.append("</td></tr>");

		OWLObjectProperty hasField = KnowledgeManager.getInstance().factory.getOWLObjectProperty("hasField", KnowledgeManager.getInstance().prefixManager);
		OWLObjectProperty hasReportType = KnowledgeManager.getInstance().factory.getOWLObjectProperty("hasReportType", KnowledgeManager.getInstance().prefixManager);
		OWLDataProperty hasContent = KnowledgeManager.getInstance().factory.getOWLDataProperty("hasContent", KnowledgeManager.getInstance().prefixManager);

        Set<OWLIndividual> fields = ind.getObjectPropertyValues(hasField, KnowledgeManager.getInstance().ontology);
        
        //======= Report Acceptance/Rejection =======

		boolean isAccepted = false;
		boolean isRejected = false;
		for (OWLClassExpression exp : ind.getTypes(KnowledgeManager.getInstance().ontology)) {
    		if (!exp.isAnonymous()) {
    			OWLClass expCls = exp.asOWLClass();
    			String strCls = KnowledgeManager.getInstance().getBrowserText(expCls);
    			if ("AcceptedReport".equals(strCls)) {
    				isAccepted = true;
    				break;
    			} else if ("RejectedReport".equals(strCls)) {
    				isRejected = true;
    				break;
    			}
    		}
    	}			


		// set row color based on accepted or rejected
		if (isAccepted)
			result.append("<tr class=\"success\">");
		else if (isRejected)
			result.append("<tr class=\"error\">");
		else
			result.append("<tr class=\"\">");

		result.append("<td><strong>");
		result.append(Messages.get("urs.admin.reportDetails.state"));
		result.append("</strong></td>");
		
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("reportId", KnowledgeManager.getInstance().getBrowserText(ind));
        
        if (!isAccepted && !isRejected) {
        	result.append("<td style=\"vertical-align: middle; \"><a href=\"");
        	result.append(Router.getFullUrl("Administration.acceptReport",map));
        	result.append("\" class=\"btn btn-success btn-mini\"><i class=\"icon-ok icon-white\"></i>&nbsp;");
        	result.append(Messages.get("urs.admin.reportDetails.acceptedState"));
        	result.append("</a>&nbsp;<a href=\"");
        	result.append(Router.getFullUrl("Administration.rejectReport",map));
        	result.append("\" class=\"btn btn-danger btn-mini\"><i class=\"icon-remove icon-white\"></i>&nbsp;"); 	
        	result.append(Messages.get("urs.admin.reportDetails.rejectedState"));
        	result.append("</a></td></tr>");

        } else {
        	String state = Messages.get("urs.admin.reportDetails.pendingState");
        	if (isAccepted)
        		state = "<i class=\"icon-ok\"></i>&nbsp;" + Messages.get("urs.admin.reportDetails.acceptedState");
        	else if (isRejected)
    			state = "<i class=\"icon-remove\"></i>&nbsp;" + Messages.get("urs.admin.reportDetails.rejectedState");
        	
        	result.append("<td style=\"vertical-align:middle;\">" + state + "</td></tr>");
        }

        //======= Report Type =======
        result.append("<tr><td><strong>");
        result.append(Messages.get("urs.admin.reportDetails.reportTypes"));
        result.append("</strong></td><td>");
        
        Set<OWLIndividual> types = ind.getObjectPropertyValues(hasReportType, KnowledgeManager.getInstance().ontology);
        for (OWLIndividual type : types) {
        	result.append(KnowledgeManager.getInstance().getLabel((OWLNamedIndividual)type) + "<br />");
        }
        
        result.append("</td>");
        result.append("<tr><td><strong>");
        result.append(Messages.get("urs.admin.reportDetails.fieldSize"));
        result.append("</strong></td><td>");
        result.append(fields.size());
        result.append("</td></tr>");
        

		// get all the fields related using hasField object property

        for (OWLIndividual f : fields) {
        	if (f.isNamed()) {
      				String fValue = ((OWLLiteral)f.getDataPropertyValues(hasContent, KnowledgeManager.getInstance().ontology).toArray()[0]).getLiteral();

        	        result.append("<tr><td><strong>");

    	        	OWLClassExpression exp = (OWLClassExpression) f.getTypes(KnowledgeManager.getInstance().ontology).toArray()[0];
    	        	
    	        	if (!exp.isAnonymous()) {
    	        		OWLClass expCls = exp.asOWLClass();
    	        		if ("SubmittedReportField".equals(KnowledgeManager.getInstance().getLabel(expCls))) {
    	        			exp = (OWLClassExpression) f.getTypes(KnowledgeManager.getInstance().ontology).toArray()[1];
    	        			expCls = exp.asOWLClass();
    	        		}
    	        			
    	        		result.append(KnowledgeManager.getInstance().getLabel(expCls));
    	        	} else {
    	        		result.append(KnowledgeManager.getInstance().getLabel((OWLNamedIndividual)f));
    	        	}

        	       	result.append("</strong></td><td>");
        	       	result.append(fValue);
        	       	result.append("</td></tr>");    	        		
            }        		
        }
		
		result.append("</tbody>");
		return result.toString();
	}
	
	/**
	 * Generates HTML codes for a single report field based on its HTML input type.
	 * @param reportFieldClass OWLClass representing a report field
	 * @return String line for HTML codes of the field
	 */
	private static String getReportFieldHTML(final OWLClass reportFieldClass) throws Exception{
		
		String htmlResult = "";
		
		// Query type of the field in HTML
		
		//GET all fields SPARQL-DL query		
		String query = "PREFIX : <http://itrc.ac.ir/ReportOntology#>\n";
        query += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
		query += "SELECT ?input { PropertyValue(:" + KnowledgeManager.getInstance().getBrowserText(reportFieldClass) + ", :hasHTMLType, ?input), SubClassOf(?input, :HTML_Input)}";

		QueryResult result = KnowledgeManager.getInstance().sparqldl(query);
		
		Iterator<QueryBinding> iterator = result.iterator();
		QueryArgument arg = new QueryArgument(QueryArgumentType.VAR, "input");
		QueryBinding binding;

		//FIXME: resolve fieldId for classes with multiple fields
		while (iterator.hasNext()) {
			binding = iterator.next();

			//get 'input' value of this query result
			String filedIRI = binding.get(arg).getValue();

			// get  corresponding OWLClass
			OWLClass cls = KnowledgeManager.getInstance().factory.getOWLClass(IRI.create(filedIRI));

			
			// generate HTML codes based on the htmlCls's browser text
			String strHTML = new Builder(reportFieldClass, cls).getHTML();
			
			htmlResult += strHTML + "\r\n";
			
		}
		
		return htmlResult;
	}
	
	/**
	 * 
	 * @param ReportClassName
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static void saveReport(String reportClassName, User user, List<String> fields, List<String> values) throws Exception{
		
		OWLDataFactory factory = KnowledgeManager.getInstance().factory;
		PrefixManager pm = KnowledgeManager.getInstance().prefixManager;
		
		OWLClass submittedReportCls = factory.getOWLClass(":SubmittedReport", pm);
		OWLClass submittedFieldCls = factory.getOWLClass(":SubmittedReportField", pm);
		OWLClass reportCls = factory.getOWLClass(":"+reportClassName, pm);

		OWLObjectProperty propHasReportType = factory.getOWLObjectProperty(":hasReportType", pm);
		OWLDataProperty propHasContent = factory.getOWLDataProperty(":hasContent", pm);
		OWLObjectProperty propHasField = factory.getOWLObjectProperty(":hasField", pm);
		OWLDataProperty propSubmittedBy = factory.getOWLDataProperty(":submittedBy", pm);

		// Create report individual
		String reportId = reportClassName + "_" + Codec.UUID();
		OWLNamedIndividual rInd = factory.getOWLNamedIndividual(reportId, pm);

		// Set report type (hasReportType object property)
		OWLNamedIndividual rtInd = factory.getOWLNamedIndividual(":" + reportClassName, pm);
		OWLObjectPropertyAssertionAxiom hasReportTypeAxiom = factory.getOWLObjectPropertyAssertionAxiom(propHasReportType, rInd, rtInd);
		KnowledgeManager.getInstance().manager.addAxiom(KnowledgeManager.getInstance().ontology, hasReportTypeAxiom);
		
		// Add report individual to the KB
		OWLClassAssertionAxiom rAxiom = factory.getOWLClassAssertionAxiom( submittedReportCls, rInd );
		OWLClassAssertionAxiom rAxiom2 = factory.getOWLClassAssertionAxiom( reportCls, rInd );		
		KnowledgeManager.getInstance().manager.addAxiom(KnowledgeManager.getInstance().ontology, rAxiom);
		KnowledgeManager.getInstance().manager.addAxiom(KnowledgeManager.getInstance().ontology, rAxiom2);

		// user content for submmittedBy data property
		String userId = (user==null)? "owl:Nothing" : user.email;
		OWLLiteral userEmail = factory.getOWLLiteral(userId);
		OWLDataPropertyAssertionAxiom userAxiom = factory.getOWLDataPropertyAssertionAxiom(propSubmittedBy, rInd, userEmail);
		KnowledgeManager.getInstance().manager.addAxiom(KnowledgeManager.getInstance().ontology, userAxiom);
		
		for (int i = 0 ; i< fields.size() ; i++) {
			String fieldName = fields.get(i);
			String contentValue = values.get(i);
			//OWLClass fieldCls = KnowledgeManager.getInstance().getOWLClass(fieldName.trim());
			String fieldId = fieldName + "_" + Codec.UUID();
			
			OWLNamedIndividual fInd = factory.getOWLNamedIndividual(fieldId, pm);
			OWLClass fCls = factory.getOWLClass(":" + fieldName, pm);
			
			// Add field type assertion
			OWLClassAssertionAxiom ftAxiom = factory.getOWLClassAssertionAxiom( fCls, fInd );
			KnowledgeManager.getInstance().manager.addAxiom(KnowledgeManager.getInstance().ontology, ftAxiom);			
			
			// Add class assertion for the submitted field
			OWLClassAssertionAxiom fAxiom = factory.getOWLClassAssertionAxiom( submittedFieldCls, fInd );
			KnowledgeManager.getInstance().manager.addAxiom(KnowledgeManager.getInstance().ontology, fAxiom);
			
			// Add content to the field
			OWLLiteral content = factory.getOWLLiteral(contentValue);
			OWLDataPropertyAssertionAxiom contentAxiom = factory.getOWLDataPropertyAssertionAxiom(propHasContent, fInd, content);
			KnowledgeManager.getInstance().manager.addAxiom(KnowledgeManager.getInstance().ontology, contentAxiom);
			
			// Add object property between the report and this field
			OWLObjectPropertyAssertionAxiom hasFieldAxiom = factory.getOWLObjectPropertyAssertionAxiom(propHasField, rInd, fInd);
			KnowledgeManager.getInstance().manager.addAxiom(KnowledgeManager.getInstance().ontology, hasFieldAxiom);
		
			// user content for submmittedBy data property
			OWLDataPropertyAssertionAxiom fUserAxiom = factory.getOWLDataPropertyAssertionAxiom(propSubmittedBy, fInd, userEmail);
			KnowledgeManager.getInstance().manager.addAxiom(KnowledgeManager.getInstance().ontology, fUserAxiom);
		}
		
		KnowledgeManager.getInstance().manager.saveOntology(KnowledgeManager.getInstance().ontology);
		
        //TODO: compute using reasoner again
        KnowledgeManager.getInstance().reasoner.precomputeInferences();
        
        if (KnowledgeManager.getInstance().reasoner.isConsistent()){
        	//KnowledgeManager.getInstance().project.save(errors);
        	KnowledgeManager.getInstance().reloadOntology();
        } else {
        	//TODO: turn the ontology back to the previous version
        	KnowledgeManager.getInstance().reloadOntology();
    		Exception e = new Exception("Inconsistent!!");
    		Logger.error("Could not save inconsistent ontology!");
    		throw e;        	
        }

	}
	
	/**
	 * Returns all saved reports (individuals of type Report in the knowledge base)
	 * @return
	 */
	public List<String> getAllReports() throws Exception{
		OWLClass reportCls = KnowledgeManager.getInstance().getOWLClass(":Report");
		NodeSet<OWLNamedIndividual> inds = KnowledgeManager.getInstance().reasoner.getInstances(reportCls, true);
		List<String> result = new ArrayList<String>();
		
		//Iterator<OWLIndividual> iterator = inds.iterator();
		//while (iterator.hasNext()) {
		//	result.add(KnowledgeManager.getInstance().getBrowserText(iterator.next()));
			//TODO add other properties
		//}
		
		return result;
	}

	public static String generateListSubmittedReportsHTML(String reportType) throws Exception{
		String htmlResult = "";
		
		//GET all fields SPARQL-DL query		
		String query = "PREFIX : <http://itrc.ac.ir/ReportOntology#>\n";
        query += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";

		query += "SELECT ?report { Type(?report,:SubmittedReport), Type(?report,:"+reportType+") }";

		System.out.println("q: " + query);
		QueryResult result = KnowledgeManager.getInstance().sparqldl(query);

		if (result==null || result.size()<1){
			htmlResult = "<tbody><tr class=\"error\"><td>" 
							+ Messages.get("urs.admin.reportDetails.noSubmissionOfThisType")
							+ "</td></tr></tbody>";
						
			return htmlResult;
		}
		
		Iterator<QueryBinding> iterator = result.iterator();
		QueryArgument arg = new QueryArgument(QueryArgumentType.VAR, "report");
		QueryBinding binding;
		
		htmlResult += 
				"<thead><tr><th>" + Messages.get("urs.admin.reportDetails.reportId") + "</th>"
				+ "<th>" + Messages.get("urs.admin.reportDetails.reportTypes") + "</th>"
				+ "<th>" + Messages.get("urs.admin.reportDetails.fieldSize") + "</th>"
				+ "<th>" + Messages.get("urs.admin.reportDetails.submittedDate") + "</th>"
				+ "<th colspan=\"2\" style=\"width:10px;\">" + Messages.get("urs.admin.reportDetails.state") + "</th>"
				+ "</tr></thead><tbody>";
		
		OWLObjectProperty hasField = KnowledgeManager.getInstance().factory.getOWLObjectProperty("hasField", KnowledgeManager.getInstance().prefixManager);
		OWLObjectProperty hasReportType = KnowledgeManager.getInstance().factory.getOWLObjectProperty("hasReportType", KnowledgeManager.getInstance().prefixManager);
		OWLDataProperty hasContent = KnowledgeManager.getInstance().factory.getOWLDataProperty("hasContent", KnowledgeManager.getInstance().prefixManager);
		
		while (iterator.hasNext()) {
			binding = iterator.next();

			// get 'field' value of this query result
			String fieldIRI = binding.get(arg).getValue();

			// get corresponding individual
			OWLNamedIndividual ind = KnowledgeManager.getInstance().factory.getOWLNamedIndividual(IRI.create(fieldIRI));
			
			boolean isAccepted = false;
			boolean isRejected = false;
			for (OWLClassExpression exp : ind.getTypes(KnowledgeManager.getInstance().ontology)) {
        		if (!exp.isAnonymous()) {
        			OWLClass expCls = exp.asOWLClass();
        			String strCls = KnowledgeManager.getInstance().getBrowserText(expCls);
        			if ("AcceptedReport".equals(strCls)) {
        				isAccepted = true;
        				break;
        			} else if ("RejectedReport".equals(strCls)) {
        				isRejected = true;
        				break;
        			}
        		}
        	}			
			// set row color based on accepted or rejected
			if (isAccepted)
				htmlResult += "<tr class=\"success\">";
			else if (isRejected)
				htmlResult += "<tr class=\"error\">";
			else
				htmlResult += "<tr class=\"\">";

				
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("reportId", KnowledgeManager.getInstance().getBrowserText(ind));
	        String route = Router.getFullUrl("Administration.showReportDetails", map);
	        htmlResult += "<td><a href=\"" + route + "\">" + KnowledgeManager.getInstance().getLabel(ind) + "</a></td>";

	        Set<OWLIndividual> fields = ind.getObjectPropertyValues(hasField, KnowledgeManager.getInstance().ontology);
	        
	        htmlResult += "<td>";
	        Set<OWLIndividual> types = ind.getObjectPropertyValues(hasReportType, KnowledgeManager.getInstance().ontology);
	        for (OWLIndividual type : types) {
	        	htmlResult += KnowledgeManager.getInstance().getLabel((OWLNamedIndividual)type) + "<br />";
	        }
	        htmlResult += "</td>";
	        
	        htmlResult += "<td>" + fields.size() + "</td>";

	        for (OWLIndividual f : fields) {
	        	for (OWLClassExpression exp : f.getTypes(KnowledgeManager.getInstance().ontology)) {
	        		if (!exp.isAnonymous()) {
	        			OWLClass expCls = exp.asOWLClass();
	        			String strCls = KnowledgeManager.getInstance().getBrowserText(expCls);
	        			if ("SubmitDateTimeField".equals(strCls)) {
	        				String s = 
	        						((OWLLiteral)f.getDataPropertyValues(hasContent, KnowledgeManager.getInstance().ontology).toArray()[0]).getLiteral();
	        				
	        				//FIXME: find out date format from db or ontology instead of settings, then check for the appopriate one
	        				if ("Jalali".equalsIgnoreCase(Configuration.getValue(CONFIG_CALENDAR_TYPE, "Jalali")))
	        					s = JalaliDateConverter.georgianToPersian(s);

	        				htmlResult += "<td>" + s + "</td>";       				
	        			}
	        		}
	        	}
	        }

	        if (!isAccepted && !isRejected) {
		        htmlResult += "<td style=\"text-align:center;width:10px;vertical-align: middle; \"><a href=\""
		        		+ Router.getFullUrl("Administration.acceptReport",map)
		        		+ "#\" class=\"btn btn-success btn-mini\"><i class=\"icon-ok icon-white\"></i></a></td>";
				htmlResult += "<td style=\"text-align:center;width:1px;vertical-align: middle; \"><a href=\""
		        		+ Router.getFullUrl("Administration.rejectReport",map)
						+ "\" class=\"btn btn-danger btn-mini\"><i class=\"icon-remove icon-white\"></i></a></td>";	        	
	        } else {
	        	String state = Messages.get("urs.admin.reportDetails.pendingState");
	        	if (isAccepted)
	        		//state = Messages.get("urs.admin.reportDetails.acceptedState");
	        		state = "<i class=\"icon-ok\"></i>";
	        	else if (isRejected)
	        		//state = Messages.get("urs.admin.reportDetails.rejectedState");
        			state = "<i class=\"icon-remove\"></i>";
	        	
	        	htmlResult += "<td colspan=\"2\" style=\"text-align:center;vertical-align:middle;\">" + state + "</td>";
	        }

	        htmlResult += "</tr>";
		}
		
		htmlResult += "</tbody>";
		
		return htmlResult;
	}
}
