/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since July 27, 2012
 * @version 0.2.0-prototype
 */

package controllers;

import java.util.Map.Entry;
import java.util.Set;

import models.Configuration;
import models.User;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Setup extends Controller {

	@Before
	public static void checkConfiguration() {
		if (Configuration.isConfigured()) {
			flash.keep();
			Application.index();
		}
	}
	@Before
	public static void selectPage() {
		String strPage = session.get("setup.nextPage");
		if ("one".equalsIgnoreCase(strPage)) {
			//TODO: add stuff to session.put(key, arg);
		} else if ("two".equalsIgnoreCase(strPage)) {
			//TODO: add stuff to session.put(key, arg);
		} else if ("three".equalsIgnoreCase(strPage)) {
			//TODO: add stuff to session.put(key, arg);
		}
		
	}

	public static void index() {
		render();
	}

	public static void configureSystem(JsonObject body) {
		JsonObject inputs = body.get("json").getAsJsonObject();
		inputs.remove("authenticityToken");
		Set<Entry<String, JsonElement>> jsonSet = inputs.entrySet();
		String userName = null, userEmail = null, userPassword = null, verifyPassword = null;
		
		//FIXME: turn around for default value of tree type
		Configuration.setValue("URS.TreeViewType", "jqx");

		// convert json to string map
		for (Entry ent : jsonSet) {
			JsonElement val = (JsonElement) ent.getValue();
			// FIXME: support multi-level JSON
			// Only check first level
			

			if (val.isJsonPrimitive()) {
				// System.out.println("JSON-> " + ent.getKey() + " : " +
				// val.getAsString());
				String configName = ent.getKey().toString();
				if (!configName.equals(null)) {
					// Storing user settings in strings and configuring the
					// system
					if (configName.equals("userName"))
						userName = val.getAsString();
					else if (configName.equals("userEmail"))
						userEmail = val.getAsString();
					else if (configName.equals("userPassword"))
						userPassword = val.getAsString();
					else if (configName.equals("verifyPassword"))
						verifyPassword= val.getAsString();
					else if (configName.equals("verifyInfo"))
						;
					else {
						Configuration.setValue(configName, val.getAsString());
					}
				}
			}
		}

		User user = new User(userEmail, userPassword, userName);
		user.isAdmin = true;

		// check if email is not used!
		if (!User.isEmailAvailable(userEmail)) {
			validation.addError("user.email", Messages
					.get("urs.admin.userManagement.emailIsNotAvailable"));
		}

		validation.valid(user);
		validation.required(userPassword);
		validation.required(userEmail);
		validation.equals(userPassword, verifyPassword).message(
				Messages.get("urs.admin.userManagement.passwordsNotMatch"));

		if (validation.hasErrors()) {
			user.delete();
			flash.error(Messages.get("urs.setup.error"));
		} else {
			user.create();
			user.save();
			flash.success(Messages.get("urs.setup.success"));
			Configuration.setConfigured(true);
		}

	}

	public static void pageOne() {
		session.put("setup.nextPage", "two");
		render();
	}

	public static void pageTwo() {
		session.put("setup.nextPage", "three");
		render();
	}

	public static void pageThree() {
		session.remove("setup.nextPage");
		render();
	}
}
