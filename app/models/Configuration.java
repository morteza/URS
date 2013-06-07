/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since July 27, 2012
 * @version 0.2.0-prototype
 */

package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;
import utils.Constants;

import javax.persistence.*;
import java.util.*;

@Entity
public class Configuration extends Model implements Constants{
	
	@Required
    public String configKey;
	
	@Required
    public String configValue;

	public Configuration( String key, String value ) {
		this.configKey = key;
		this.configValue = value;
		create();
	}
	
    public static String getValue(String key, String defaultValue) {
        Configuration config = find("byConfigKey", key).first();
        if ( config != null )
        	return config.configValue;
        return defaultValue;
    }

    /**
     * Sets new value for a configuration key, and returns the old one if any.
     * @param key
     * @param value
     * @return old value
     */
    public static String setValue(String key, String value) {
    	Configuration config = find("byConfigKey", key).first();
    	if (config!=null) {
    		String oldConfigValue = new String(config.configValue);
    		//TODO: set the new value
    		config.configValue = value;
    		config.save();
    		
    		return oldConfigValue;
    	}
    	
    	config = new Configuration(key, value);
    	config.create();
    	
    	return null;
    }
    
    public String toString() {
    	return "Configuration(" + configKey + ")";
    }
    
    public static boolean isConfigured() {
		String isConfigured = Configuration.getValue(CONFIG_ISCONFIGURED, "false");
		if (Boolean.valueOf(isConfigured)) {
			return true;
		}

		return false;
    }
    
    public static void setConfigured(boolean value) {
    	Configuration.setValue(CONFIG_ISCONFIGURED, String.valueOf(value));
    }
    
}
