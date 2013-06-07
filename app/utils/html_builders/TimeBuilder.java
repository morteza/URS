/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since Aug 3, 2012
 * @version x.x.x
 */

package utils.html_builders;

import org.semanticweb.owlapi.model.OWLClass;

import utils.KnowledgeManager;

/**
 *
 */
public class TimeBuilder extends Builder {

	/**
	 * @param fieldClass
	 * @param htmlTypeClass
	 */
	public TimeBuilder(OWLClass fieldClass, OWLClass htmlTypeClass) {
		super(fieldClass, htmlTypeClass);
	}

	/* (non-Javadoc)
	 * @see utils.field_builders.FieldBuilder#getHTML()
	 */
	@Override
	public final String getHTML() {
		
		String fieldClassName = KnowledgeManager.getInstance().getBrowserText(fieldClass);
		String label = KnowledgeManager.getInstance().getLabel(fieldClass);

		result  = "";
		result += "<div class=\"input\">";
		result += "<label class=\"control-label\" for=\"" + fieldClassName + "\"><strong>" + label + "</strong></label>";
		result += "<input name=\"" + fieldClassName + "\" id=\"" + fieldClassName + "\" "
				+ "class=\"mini\" type=\"text\" value=\"12:00pm\" />";
		result += "</div></div>\r\n";
		return result;
	}

}
