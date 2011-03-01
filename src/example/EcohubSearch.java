package example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import ranking.GenericBM25Ranking;
import db.EcohubItem;
import db.MongoDatastore;

public class EcohubSearch {

    private static final MongoDatastore DATA_STORE = MongoDatastore.getInstance();

    public static void main(String[] args) throws IOException, URISyntaxException {
	EcohubSearch mongoSearch = new EcohubSearch();
	mongoSearch.search("Driven Business Processes");
    }

    private void search(String query) {
	List<EcohubItem> result = GenericBM25Ranking.search(DATA_STORE.getEcohubItems(), query);
		
	for (int i=0; i < 10 && i < result.size(); i++) {
	    System.out.println(String.format("%.2f", result.get(i).get("score")) +": "+ result.get(i).toString());
	}
    }

}
