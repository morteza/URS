package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

public class SetupConfigurations {
    public static int CENTRAL_SERVER_PORT;
    public static String CENTRAL_SERVER_ADDRESS;
    
    public static String UPLOAD_KB_INTERVAL;
    public static String UPDATE_ONTOLOGY_INTERVAL;
    
    public static String ORGANIZATION_URI;
}
