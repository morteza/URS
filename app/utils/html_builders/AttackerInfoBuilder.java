/**
 * @author Arman Radmanesh <radmanesh@gmail.com>
 * @since Oct 22, 2012
 * @version x.x.x
 */

package utils.html_builders;

import org.semanticweb.owlapi.model.OWLClass;

import utils.KnowledgeManager;

/**
 *
 */
public class AttackerInfoBuilder extends Builder {

	/**
	 * @param fieldClass
	 * @param htmlTypeClass
	 */
	public AttackerInfoBuilder(OWLClass fieldClass, OWLClass htmlTypeClass) {
		super(fieldClass, htmlTypeClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see utils.field_builders.FieldBuilder#getHTML()
	 */
	@Override
	public final String getHTML() {

		String fieldClassName = KnowledgeManager.getInstance().getBrowserText(fieldClass);
		String label = KnowledgeManager.getInstance().getLabel(fieldClass);
		String result = "";
		result += "<div class=\"input\">";
		result += "<label class=\"control-label\"><strong>" + label
				+ "</strong></label>";
		result += "<div class=\"controls\"><input type=\"text\" style=\"direction:ltr\" name=\"attackerIP\" id=\"attackerIP\" class=\"span3\" placeholder=\"IP\"/></div>"
				+ "<div class=\"controls\"><input type=\"text\" name=\"attackerPort\" style=\"direction:ltr\" id=\"attackerPort\" class=\"span3\" placeholder=\"پورت\" /></div>"
				+ "<div class=\"controls\"><input type=\"text\" name=\"attackerDomain\" style=\"direction:ltr\" id=\"attackerDomain\" class=\"span3\" placeholder=\"Domain\" /></div></div>";
		return result;
	}

}
