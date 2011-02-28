package db;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoDatastore {
    private static MongoDatastore INSTANCE;

    private DB db;

    private MongoDatastore() throws UnknownHostException, MongoException {
	Mongo m = new Mongo();
	db = m.getDB("mongosearch");
	getSamples().setObjectClass(Sample.class);
	getStatuses().setObjectClass(Status.class);
    }

    public DBCollection getSamples() {
	return db.getCollection("samples");
    }

    public DBCollection getStatuses() {
	return db.getCollection("statuses");

    }

    public void save(Sample sample) {
	getSamples().save(sample);
    }

    public void save(Status status) {
	getStatuses().save(status);
    }
    
    public static MongoDatastore getInstance() {
	if (INSTANCE == null) {
	    try {
		INSTANCE = new MongoDatastore();
	    } catch (UnknownHostException e) {
		e.printStackTrace();
		System.exit(-1);
	    } catch (MongoException e) {
		e.printStackTrace();
		System.exit(-1);
	    }
	}

	return INSTANCE;
    }
}
