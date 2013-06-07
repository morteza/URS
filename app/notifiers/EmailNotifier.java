/**
 * @author Morteza Ansarinia <ansarinia@me.com>
 * @since July 26, 2012
 * @version 0.2.0-prototype
 */

package notifiers;

import play.mvc.*;

import javax.mail.internet.*;

import models.*;

public class EmailNotifier extends Mailer {

    public static boolean newUser(User user) throws Exception {
        setFrom(new InternetAddress("admin@itrc.ac.ir", "Central Administrator"));
        setReplyTo(new InternetAddress("help@itrc.ac.ir", "Help"));
        setSubject("Welcome %s", user.name);
        setCharset("utf-8");
        addRecipient(user.email, new InternetAddress("new-users@itrc.ac.ir", "New users notice"));
        return sendAndWait(user);
    }

}

