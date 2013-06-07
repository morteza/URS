/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since Aug 3, 2012
 * @version 0.3.0
 * @since 0.3.0
 */

package utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import play.data.binding.Global;
import play.data.binding.TypeBinder;

/**
 * JSon object request binder for Play!
 */
@Global
public class JsonObjectBinder implements TypeBinder<JsonObject> {

	/* (non-Javadoc)
	 * @see play.data.binding.TypeBinder#bind(java.lang.String, java.lang.annotation.Annotation[], java.lang.String, java.lang.Class, java.lang.reflect.Type)
	 */
	//@Override
    public Object bind(String name, Annotation[] annotations, String value, Class actualClass, Type genericType) throws Exception {
		return new JsonParser().parse(value);		
	}

}