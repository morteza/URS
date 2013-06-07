package utils;

import models.User;


public class ConfigurationUtils {
	public static boolean createUser(String name,String email,String password, boolean isAdmin){
		//TODO: check for errors!
		if(name.equals(null) || email.equals(null) || password.equals(null))
			return false;
		User user = new User(email, password, name);
		user.isAdmin = isAdmin;
		user.create();
		user.save();
		return true;

	}
}
