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
public class DateBuilder extends Builder {

	/**
	 * @param fieldClass
	 * @param htmlTypeClass
	 */
	public DateBuilder(final OWLClass fieldClass, final OWLClass htmlTypeClass) {
		super(fieldClass, htmlTypeClass);
	}

	/* (non-Javadoc)
	 * @see utils.field_builders.FieldBuilder#getHTML()
	 */
	@Override
	public String getHTML() throws Exception {
		
		String fieldClassName = KnowledgeManager.getInstance().getBrowserText(fieldClass);
		String label = KnowledgeManager.getInstance().getLabel(fieldClass);

		result  = "";
		result += "<div class=\"input\">";
		result += "<label class=\"control-label\" for=\"" + fieldClassName + "\"><strong>" + label + "</strong></label>";
		result += "<div class=\"controls\"><input name=\"" + fieldClassName + "\" id=\"datepicker\" "
				+ "class=\"span2\" type=\"text\" "
				+ "value=\"" + /*new SimpleDateFormat("MM/dd/yyyy").format(new Date()) + */ "1390/6/20\" />";
		result += "</div></div>\r\n";
		
		return result;
	}

}
