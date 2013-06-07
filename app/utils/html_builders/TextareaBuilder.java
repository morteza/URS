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
public class TextareaBuilder extends Builder {

	/**
	 * @param fieldClass
	 * @param htmlTypeClass
	 */
	public TextareaBuilder(OWLClass fieldClass, OWLClass htmlTypeClass) {
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
		result += "<div class=\"controls\"><textarea name=\"" + fieldClassName + "\" id=\"" + fieldClassName + "\" "
				+ "class=\"span4 xxlarge\" rows=\"4\"></textarea>";
		
		result += "</div></div>\r\n";
		return result;
	}

}
