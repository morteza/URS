/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since July 26, 2012
 * @version 0.2.0-prototype
 */

package controllers;

import play.i18n.Messages;
import models.User;
import models.UserType;

public class Security extends Secure.Security {

    static boolean authenticate(String username, String password) {
    	User u = User.findByEmail(username);
    	return u != null && u.checkPassword(password);
    }
    
    static boolean check(String profile) {
        User user = User.findByEmail(connected());
    	if (UserType.ADMINISTRATOR.equals(profile) && user != null) {
            return user.isAdmin;
        }
    	if (UserType.REGISTERED.equals(profile) && user != null) {
    		return true;
    	}
        return false;
    }
    
    static void onDisconnected() {
    	flash.success(Messages.get("secure.logout"));
    	Application.index();
    }
    
    static void onAuthenticated() {
       
    	Administration.index();
    }
    
    static String getConfirmationURL(User user) {
    	String confirmationURL = "";
    	//TODO: generate link
    	return confirmationURL;
    }
    
    static User getConnectedUser() {
    	return User.findByEmail(connected());
    }

}

