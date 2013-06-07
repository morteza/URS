/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since Aug 3, 2012
 * @version x.x.x
 */

package utils.html_builders;

import java.util.Iterator;
import java.util.Set;

import models.Configuration;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

import play.i18n.Messages;

import sparqldl.QueryArgument;
import sparqldl.QueryBinding;
import sparqldl.QueryResult;
import sparqldl.types.QueryArgumentType;
import utils.Constants;
import utils.KnowledgeManager;

/**
 *
 */
public class ClassTreeBuilder extends Builder implements Constants{

	/**
	 * @param fieldClass
	 * @param htmlTypeClass
	 */
	
	private int numOfClasses = 0;
	
	public ClassTreeBuilder(final OWLClass fieldClass, final OWLClass htmlTypeClass) {
		super(fieldClass, htmlTypeClass);
	}

	/* (non-Javadoc)
	 * @see utils.field_builders.FieldBuilder#getHTML()
	 */
	@Override
	public String getHTML() throws Exception {
		
		String fieldClassName = KnowledgeManager.getInstance().getBrowserText(fieldClass);
		String label = KnowledgeManager.getInstance().getLabel(fieldClass);
		
		result = "";
		result += "<div class=\"input\">"
				+ "<label class=\"control-label\" for=\"" + fieldClassName + "\"><strong>" + label + "</strong> ("+Messages.get("urs.tree.selectedItem") +"<span style=\"direction:rtl\" class='help-inline' id='selected" + fieldClassName + "'></span>"+")</label></div>"
				
				+ "<div class=\"tree\" style=\"\" id=\"" + fieldClassName + "_tree\">";

		String query = "PREFIX : <http://itrc.ac.ir/ReportOntology#>\n";
        query += "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n";
        
        //FIXME: this is for the ontology browser capability (shows all subclasses)
        if ("Domain_Concept".equals(fieldClassName))
        	query += "SELECT ?cls { SubClassOf(?cls, :Domain_Concept) }";
        else
        	query += "SELECT ?cls { PropertyValue(:" + fieldClassName + ", :hasClassesReference, ?cls), SubClassOf(?cls, :Domain_Concept) }";

		QueryResult qResult = KnowledgeManager.getInstance().sparqldl(query);
		
		Iterator<QueryBinding> iterator = qResult.iterator();
		QueryArgument arg = new QueryArgument(QueryArgumentType.VAR, "cls");
		QueryBinding binding;
		
		while (iterator.hasNext()) {
			binding = iterator.next();

			// get 'field' value of this query result
			String filedIRI = binding.get(arg).getValue();

			// get corresponding OWLClass
			OWLClass cls = KnowledgeManager.getInstance().factory.getOWLClass(IRI.create(filedIRI));

			numOfClasses = 0;
			// Recursively add UL (sub-tree) and LI (class)
			result += "<ul class=\"tree\">" + getSubTreeHTML(cls) + "</ul>";
			
			// if number of all classes is not that much use top-down, otherwise use jqx, default is simple
			String treeViewType = Configuration.getValue(CONFIG_TREEVIEW_TYPE, "simple");
			//String treeViewType = "jqx";
			if ("topdown".equalsIgnoreCase(treeViewType)){
				// top-down view
				result += "<link href=\"/public/css/topdown-tree.css\" rel=\"stylesheet\" media=\"screen\">";
			} else if ("jqx".equalsIgnoreCase(treeViewType)) {
				// Add jQX list view
				result +="<link href=\"/public/css/jqx/jqx.base.css\" rel=\"stylesheet\" media=\"screen\">"
			        	+ "<script src=\"/public/js/jqx/jqxcore.js\"></script>"
			        	+ "<script src=\"/public/js/jqx/jqxscrollbar.js\"></script>"
			        	+ "<script src=\"/public/js/jqx/jqxbuttons.js\"></script>"
			        	+ "<script src=\"/public/js/jqx/jqxtree.js\"></script>"
			        	+ "<script src=\"/public/js/jqx/jqxpanel.js\"></script>\n";
			    
			    result += "<script type=\"text/javascript\">\n"
			    		+ "$(document).ready(function () {\n"
			    		+ "$(\"#" + fieldClassName + "_tree\").jqxTree({ toggleMode: 'click'});\n"
			    		+ "$('#" + fieldClassName + "_tree').bind('select', function (event) {\n"
			    		+ "var args = event.args;\n"
						+ "var item = $('#jqxTree').jqxTree('getItem', args.element);\n" + "$('#"
						+ fieldClassName + "').val('0')});\n" 
						+"$(\"#"+fieldClassName+"_tree  a \").click( function(event) {\n"
						+"$(\"#" + fieldClassName + "_tree\").jqxTree('collapseAll');"
						+"$('#selected"+fieldClassName+"').html($(this).attr('data-name'));"
						+"});});\n" + "</script>\n";
			} else {
				// Simple list view
				result += "<link href=\"/public/css/simple-tree.css\" rel=\"stylesheet\" media=\"screen\">";
			}
			
		}

		result += "</div>"; 
		result += "<input type=\"hidden\" name=\"" + fieldClassName + "\" id=\"" + fieldClassName + "\"/>";
		result += "<p>&nbsp;</p> <!-- dummy placeholder -->";
		return result;
	}
	
	private String getSubTreeHTML(OWLClass cls) {
		StringBuilder result = new StringBuilder();
		
		
		Set<OWLClassExpression> subClss = cls.getSubClasses(KnowledgeManager.getInstance().ontology);

		numOfClasses += 1;

		if (subClss==null || subClss.size()==0) {
			String cssClass = (numOfClasses==1) ? "root" : ""; 
				
			result.append("<li class=\""+ cssClass +"\"><a href=\"#\" data-name=\""+KnowledgeManager.getInstance().getLabel(cls)+"\" data-target=\"" + KnowledgeManager.getInstance().getBrowserText(fieldClass) + "\"" +
					" data-value=\"" + ":" + KnowledgeManager.getInstance().getBrowserText(cls) + "\">"
					+ KnowledgeManager.getInstance().getLabel(cls) + "</a></li>");
		} else {
			String cssClass = (numOfClasses==1) ? "root" : ""; 
			result.append("<li class=\""+ cssClass +"\"><a href=\"#\" data-name=\""+KnowledgeManager.getInstance().getLabel(cls)+"\" data-target=\"" + KnowledgeManager.getInstance().getBrowserText(fieldClass) + "\"" +
						" data-value=\"" + ":" + KnowledgeManager.getInstance().getBrowserText(cls) + "\">"
						+ KnowledgeManager.getInstance().getLabel(cls) + "</a><ul>");
			for (OWLClassExpression subClsExp : cls.getSubClasses(KnowledgeManager.getInstance().ontology)) {
				if (!subClsExp.isAnonymous()) {
					result.append(getSubTreeHTML(subClsExp.asOWLClass()));
				}
			}		
			result.append("</ul></li>");			
		}
			
		
		
		return result.toString();
	}

}
