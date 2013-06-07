/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since Aug 3, 2012
 * @version x.x.x
 */

package utils.html_builders;

import org.semanticweb.owlapi.model.OWLClass;

import utils.Constants;
import utils.KnowledgeManager;

/**
 *
 */
public class Builder implements Constants{
	
	protected OWLClass fieldClass;
	protected OWLClass htmlTypeClass;
	
	protected String result;
		
	public Builder(OWLClass fieldClass, OWLClass htmlTypeClass) {
		this.fieldClass = fieldClass;
		this.htmlTypeClass = htmlTypeClass;
	}
	
	public String getHTML() throws Exception {
		String type = KnowledgeManager.getInstance().getLabel(htmlTypeClass);
		return getHTML(type);
	}
	
	private String getHTML(String type) throws Exception {
		
		type = type.trim();
		
		// System.out.println("Field " + fieldClass + " of type " + htmlTypeClass);
		
		if (type.equals("HTML_DateTime")) {
			return new DateTimeBuilder(fieldClass, htmlTypeClass).getHTML();
		}else if (type.equals("HTML_Select")) {
			return new SelectBuilder(fieldClass, htmlTypeClass).getHTML();
		}else if (type.equals("HTML_Checkbox")) {
			return new CheckboxBuilder(fieldClass, htmlTypeClass).getHTML();
		}else if (type.equals("HTML_IndividualSelect")) {
			return new IndividualSelectBuilder(fieldClass, htmlTypeClass).getHTML();
		}else if (type.equals("HTML_Tree")) {
			return new ClassTreeBuilder(fieldClass, htmlTypeClass).getHTML();
		} else if (type.equals("HTML_Time")) {
			return new TimeBuilder(fieldClass, htmlTypeClass).getHTML();			
		} else if (type.equals("HTML_Text")) {
			return new TextBuilder(fieldClass, htmlTypeClass).getHTML();			
		} else if (type.equals("HTML_Textarea")) {
			return new TextareaBuilder(fieldClass, htmlTypeClass).getHTML();			
		} else if (type.equals("HTML_Hidden")) {
			return new HiddenBuilder(fieldClass, htmlTypeClass).getHTML();
		} else if (type.equals("HTML_File")) {
			return new FileBuilder(fieldClass, htmlTypeClass).getHTML();
		} else if (type.equals("HTML_AttackerInfo"))
			return new AttackerInfoBuilder(fieldClass, htmlTypeClass).getHTML();
		// builder for this is not available
		return null;
	}
}
