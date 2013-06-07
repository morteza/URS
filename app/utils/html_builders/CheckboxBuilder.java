/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since Aug 3, 2012
 * @version x.x.x
 */

package utils.html_builders;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import sparqldl.QueryArgument;
import sparqldl.QueryBinding;
import sparqldl.QueryResult;
import sparqldl.types.QueryArgumentType;
import utils.KnowledgeManager;

/**
 *
 */
public class CheckboxBuilder extends Builder {

	/**
	 * @param fieldClass
	 * @param htmlTypeClass
	 */
	public CheckboxBuilder(final OWLClass fieldClass, final OWLClass htmlTypeClass) {
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
		result += "<label class=\"checkbox\" style=\"height:30px\" for=\"" + fieldClassName + "\">";

	
		result += "<input type=\"checkbox\" name=\"" + fieldClassName + "\" id=\"" + fieldClassName +"\">";
		result += "<span style=\"cursor: pointer;\">" + label + "</span>";

		result += "</label>\r\n";
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
