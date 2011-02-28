package example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import ranking.GenericBM25Ranking;
import db.MongoDatastore;
import db.Sample;

public class MongoSearch {

    private static final MongoDatastore DATA_STORE = MongoDatastore.getInstance();

    public static void main(String[] args) throws IOException, URISyntaxException {
	MongoSearch mongoSearch = new MongoSearch();
	mongoSearch.search("lion os");
    }

    private void search(String query) {
	List<Sample> result = GenericBM25Ranking.search(DATA_STORE.getSamples(), query);
		
	for (int i=0; i < 10 && i < result.size(); i++) {
	    System.out.println(String.format("%.2f", result.get(i).get("score")) +": "+ result.get(i).getText());
	}
    }

}
