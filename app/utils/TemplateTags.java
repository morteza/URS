/**
 * 
 */
package utils;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;

import play.templates.FastTags;
import play.templates.TagContext;
import play.templates.GroovyTemplate.ExecutableTemplate;

/**
 * Support for HTML template tags.
 */

@FastTags.Namespace("help")
public class TemplateTags extends FastTags {
	public static void _title(Map<?, ?> args, Closure body, PrintWriter out, 
			   ExecutableTemplate template, int fromLine) {
		Object value = args.get("arg");
		//out.println(value);
	}
}
