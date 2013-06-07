/**
 * @author Arman Radmanesh <radmanesh@gmail.com>
 * @since Oct 22, 2012
 * @version x.x.x
 */

package utils.html_builders;

import org.semanticweb.owlapi.model.OWLClass;

import play.i18n.Messages;
import utils.KnowledgeManager;

/**
 *
 */
public class FileBuilder extends Builder {

	/**
	 * @param fieldClass
	 * @param htmlTypeClass
	 */
	public FileBuilder(OWLClass fieldClass, OWLClass htmlTypeClass) {
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
		// result += "<div class=\"input\">";
		// result += "<label class=\"control-label\" for=\"" + fieldClassName +
		// "\"><strong>" + label
		// + "</strong></label>";
		// result += "<div class=\"controls\"><input type=\"text\" name=\"" +
		// fieldClassName + "\" id=\""
		// + fieldClassName + "\" " + "class=\"span4 xxlarge\" rows=\"4\" />";

		result += "<div class=\"input\">";
		result += "<label class=\"control-label\" for=\"" + fieldClassName + "\"><strong>" + label
				+ "</strong></label>";
		result += "<div class=\"controls upload_file_container btn\">"
				+ Messages.get("urs.report.selectFile")
				+ "<input type=\"file\" class=\"span4\" id=\"" + fieldClassName + "\" name=\""
 + fieldClassName + "\"></div>&nbsp;<span id=\"attachment"
				+ fieldClassName + "\"></span></div><br/>";
		result +="\r\n"+"<script>"+"\r\n"
 + "$('#" + fieldClassName + "').change(function(){" + "\r\n"
				+"var file = this.files[0];"+"\r\n"
				+"name = file.name;" +"\r\n"
 + "$('#attachment"
				+ fieldClassName + "').html('<code>'+name+'/<code>');" + "\r\n"
				+"});\r\n</script>\r\n";
		return result;
	}

}
