/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since Aug 3, 2012
 * @version x.x.x
 */

package utils.html_builders;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.semanticweb.owlapi.model.OWLClass;

import utils.KnowledgeManager;

/**
 *
 */
public class HiddenBuilder extends Builder {

	/**
	 * @param fieldClass
	 * @param htmlTypeClass
	 */
	public HiddenBuilder(OWLClass fieldClass, OWLClass htmlTypeClass) {
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
		result += "<input name=\"" + fieldClassName + "\" id=\"" + fieldClassName + "\" "
				+ "class=\"mini\" type=\"hidden\" value=\"" +  new SimpleDateFormat("MM/dd/yyyy").format(new Date()) + "\" />";
		return result;
	}

}
