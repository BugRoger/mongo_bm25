package example;

import java.io.IOException;
import java.net.URISyntaxException;

import com.mongodb.BasicDBList;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import db.EcohubItem;
import db.MongoDatastore;

public class EcohubIndexer {

    private static final MongoDatastore DATA_STORE = MongoDatastore.getInstance();

    public static void main(String[] args) throws IOException, URISyntaxException {
	DATA_STORE.getEcohubItems().drop();

	DBCursor statusCursor = DATA_STORE.getEcohubDB().getCollection("ecohub_source").find();

	while (statusCursor.hasNext()) {
	    DBObject status = statusCursor.next();
	    
	    String title       = (String) status.get("title");
	    String summary     = (String) status.get("summary");
	    String features    = ((BasicDBList)((DBObject)status.get("details")).get("keyFeatures")).toString();
	    String description = (String) ((DBObject)status.get("details")).get("description");
	    
	    DATA_STORE.getEcohubItems().save(new EcohubItem(title, summary, features, description));
	}
    }

}
