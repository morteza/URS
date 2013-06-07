/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since July 27, 2012
 * @version 0.2.0-prototype
 */

package models;

import javax.persistence.*;
import java.util.*;

import play.*;
import play.db.jpa.*;
import play.libs.*;
import play.data.validation.*;

@Entity
public class User extends Model {

    @Email
    @Required
    public String email;
    
    @Password
    @Required
    public String passwordHash;
    
    @Required
    public String name;
        
    public boolean isAdmin;
    public boolean isEnabled;
    public boolean isConfirmed;
    
    
    public User(String email, String password, String name) {
        this.email = email;
        this.passwordHash = Codec.hexMD5(password);
        this.name = name;
        this.isConfirmed = true;
        this.isEnabled = false;
    }
    
    public boolean checkPassword(String password) {
        return passwordHash.equals(Codec.hexMD5(password));
    }

    public static User findByEmail(String email) {
        return find("byEmail", email).first();
    }

    public static boolean isEmailAvailable(String email) {
        return findByEmail(email) == null;
    }
    
    public String toString() {
    	return "User(" + email + ")";
    }

}

