package db;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoDatastore {
    private static MongoDatastore INSTANCE;

    private DB mongoSearchDB, ecohubDB;

    private MongoDatastore() throws UnknownHostException, MongoException {
	Mongo m = new Mongo();
	mongoSearchDB = m.getDB("mongosearch");
	ecohubDB      = m.getDB("ecohub");
	getSamples().setObjectClass(Sample.class);
	getStatuses().setObjectClass(Status.class);
	getEcohubItems().setObjectClass(EcohubItem.class);
    }
    
    public DB getMongoSearchDB() {
	return mongoSearchDB;
    }
    
    public DB getEcohubDB() {
	return ecohubDB;
    }

    public DBCollection getSamples() {
	return mongoSearchDB.getCollection("samples");
    }

    public DBCollection getStatuses() {
	return mongoSearchDB.getCollection("statuses");
    }
    
    public DBCollection getEcohubItems() {
	return ecohubDB.getCollection("ecohubItems");
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
