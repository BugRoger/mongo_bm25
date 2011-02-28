package example;

import java.io.IOException;
import java.net.URISyntaxException;

import com.mongodb.DBCursor;

import db.MongoDatastore;
import db.Sample;
import db.Status;

public class StatusIndexer {

    private static final MongoDatastore DATA_STORE = MongoDatastore.getInstance();

    public static void main(String[] args) throws IOException, URISyntaxException {
	DATA_STORE.getSamples().drop();

	DBCursor statusCursor = DATA_STORE.getStatuses().find();

	while (statusCursor.hasNext()) {
	    Status status = (Status) statusCursor.next();
	    DATA_STORE.save(new Sample(status.getText()));
	}
    }

}
