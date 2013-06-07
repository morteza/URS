/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since August 20, 2012
 * @version 0.5.0
 */

package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import play.i18n.Messages;
import play.mvc.*;
import play.vfs.VirtualFile;

/**
 * Includes rendering actions for representing help pages. All help pages have to be
 * included in the 'public/help/' directory.
 *
 */
public class Help extends Controller {

	/**
	 * Shows default page for the help system, including list of available help pages.
	 * Titles are messages with id of <code>urs.help.pages.&lt;page_name&gt;</code> for each page.
	 */
    public static void index() {
    	List<String> pages = new ArrayList<String>();
    	List<String> titles = new ArrayList<String>();
    	
    	List<VirtualFile> pageFiles = VirtualFile.open("public/help/").list();
    	
    	// Template template;
    	for (VirtualFile file : pageFiles) {
    		String fileName = file.getName().toLowerCase();
    		if (fileName.endsWith("html")) {
    			fileName = fileName.substring(0, fileName.length()-5);
    			pages.add(fileName);
    			// template = TemplateLoader.load(file);
    			//TODO: get 'help.title' fast tag of the template and add it to the titles
    			titles.add("urs.help.pages."+fileName); //FIXME
    		}
    	}
    	
        render(pages, titles);
    }
        
    /**
     * Retrieves HTML of the requested help page from <code>/public/help/</code> directory,
     * and shows it after processing the file. Redirected to the index of the help system
     * and shows an error if help file does not exist.
     * 
     * @param page Requested page's file name without 'html' extension.
     */
    public static void showPage(String page) {
    	String templateFilePath = new StringBuilder("public/help/").append(page).append(".html").toString();
    	boolean templateFileExists = VirtualFile.open(templateFilePath).exists();
    	
    	if (templateFileExists)
    		renderTemplate(templateFilePath, page);
    	flash.error(Messages.get("urs.help.PageNotFound"));
    	flash.keep();
    	index();
    }
}
