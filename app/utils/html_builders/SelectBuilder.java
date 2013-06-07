/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since Aug 3, 2012
 * @version x.x.x
 */

package utils.html_builders;

import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

import sparqldl.QueryArgument;
import sparqldl.QueryBinding;
import sparqldl.QueryResult;
import sparqldl.types.QueryArgumentType;
import utils.KnowledgeManager;

/**
 *
 */
public class SelectBuilder extends Builder {

	/**
	 * @param fieldClass
	 * @param htmlTypeClass
	 */
	public SelectBuilder(final OWLClass fieldClass, final OWLClass htmlTypeClass) {
		super(fieldClass, htmlTypeClass);
	}

	/* (non-Javadoc)
	 * @see utils.field_builders.FieldBuilder#getHTML()
	 */
	@Override
	public final String getHTML() throws Exception {

		String fieldClassName = KnowledgeManager.getInstance().getBrowserText(fieldClass);
		String label = KnowledgeManager.getInstance().getLabel(fieldClass);
				
		result = "";
		result += "<div class=\"input\">";
		result += "<label class=\"control-label\" for=\"" + fieldClassName + "\"><strong>" + label + "</strong></label>";
		result += "<div class=\"controls\">";

		//GET all individuals SPARQL-DL query		
		String query = "PREFIX : <http://itrc.ac.ir/ReportOntology#>\n";
        query += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
		query += "SELECT ?cls { PropertyValue(:" + fieldClassName + ", :hasClassesReference, ?cls), SubClassOf(?cls, :Domain_Concept)}";

		QueryResult qResult = KnowledgeManager.getInstance().sparqldl(query);
		
		Iterator<QueryBinding> iterator = qResult.iterator();
		QueryArgument arg = new QueryArgument(QueryArgumentType.VAR, "cls");
		QueryBinding binding;
		
		while (iterator.hasNext()) {
			binding = iterator.next();

			// get 'cls' value of this query result
			String clsIRI = binding.get(arg).getValue();

			// get corresponding OWLClass
			OWLClass cls = KnowledgeManager.getInstance().factory.getOWLClass(IRI.create(clsIRI));

			// Recursively add UL (sub-tree) and LI (class)
			result += getSubClassesHTML(cls);
		}

		result += "</div>\r\n";
		result += "</div>\r\n";

		return result;
	}
	
	/**
	 * return html options structure of individuals of a chosen OWL class.
	 * @param parent Parent node in the class hierarchy.
	 * @return HTML-formatted tree structure
	 */
	private String getSubClassesHTML(OWLClass cls) {
		
		String fieldClassName = KnowledgeManager.getInstance().getBrowserText(fieldClass);
		
		String result = "<select class=\"span4\" name=\"" + fieldClassName + "\" id=\"" + fieldClassName + "\" >";
		Set<OWLClassExpression> exps = cls.getSubClasses(KnowledgeManager.getInstance().ontology);
		
		String label, value;
		for (OWLClassExpression exp : exps) {
			if (!exp.isAnonymous()) {
				OWLClass namedCls = (OWLClass) exp;
				label = KnowledgeManager.getInstance().getLabel(namedCls);
				value = KnowledgeManager.getInstance().getBrowserText(namedCls);
				result += "<option value=\"" + value + "\">" + label + "</option>";				
			}
		}
		
		result += "</select>";
		return result;
		
	}
}
