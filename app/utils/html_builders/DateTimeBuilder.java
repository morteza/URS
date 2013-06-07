/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @author Arman Radmanesh <radmanesh@gmail.com>
 * @since September 1, 2012
 * @version 0.9.0
 */

package utils.html_builders;

import java.text.SimpleDateFormat;
import java.util.Date;

import models.Configuration;

import org.semanticweb.owlapi.model.OWLClass;

import utils.JalaliDateConverter;
import utils.KnowledgeManager;

/**
 *
 */
public class DateTimeBuilder extends Builder {

	/**
	 * @param fieldClass
	 * @param htmlTypeClass
	 */
	public DateTimeBuilder(final OWLClass fieldClass, final OWLClass htmlTypeClass) {
		super(fieldClass, htmlTypeClass);
	}

	/* (non-Javadoc)
	 * @see utils.field_builders.FieldBuilder#getHTML()
	 */
	@Override
	public String getHTML() throws Exception {
		
		String fieldClassName = KnowledgeManager.getInstance().getBrowserText(fieldClass);
		String label = KnowledgeManager.getInstance().getLabel(fieldClass);

		String todayStr = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
		
		if ("Jalali".equalsIgnoreCase(Configuration.getValue(CONFIG_CALENDAR_TYPE, "Jalali")))
			todayStr = JalaliDateConverter.georgianToPersian(todayStr);
		result  = "";
		result += "<div class=\"input\">";
		result += "<label class=\"control-label\" for=\"" + fieldClassName + "\"><strong>" + label + "</strong></label>";
		result += "<div class=\"controls\"><span class=\"input-append\">"
				+ "<input name=\"" + fieldClassName + "\" class=\"datepicker span2\" type=\"text\" style=\"width: 85px;\""
				+ "value=\"" + todayStr + "\"></span>"
				+"<span class=\"input-prepend\"><input name\"\" class=\"timepicker\" type=\"text\" style=\"width: 75px;\"></span>";
		result += "</div></div>\r\n";

		return result;
	}

}
