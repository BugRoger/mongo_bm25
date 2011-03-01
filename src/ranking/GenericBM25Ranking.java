package ranking;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import token.LuceneTokenizer;
import token.Tokenizer;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import db.SearchableDBObject;

public class GenericBM25Ranking<T extends SearchableDBObject> {
    private static final double K = 2.0;
    private static final double B = 0.75;

    private static final Tokenizer TOKENIZER = new LuceneTokenizer();

    private final List<String> queryTerms;
    private final Map<String, Double> IDF;
    private final double averageLength;
    private final DBCollection collection;

    private GenericBM25Ranking(DBCollection collection, String query) {
	this.collection = collection;
	
	System.out.println("Ensuring that there's a term index");
	collection.ensureIndex(new BasicDBObject("tf.t", 1));
	
	preCalculateTermDocumentCounts();
	averageLength = preCalculateAverageDocumentLength();

	System.out.println("Analysing Query");
	queryTerms = TOKENIZER.tokenize(query);
	
	IDF           = preCalculateIDFs();
    }

    @SuppressWarnings("unchecked")
    private List<T> search() {
	List<T> result = new ArrayList<T>();

	System.out.println("Executing Search Query: "+queryTerms);
	BasicDBObject dbQuery = new BasicDBObject("tf.t", new BasicDBObject("$all", queryTerms));

	DBCursor searchResult = collection.find(dbQuery);

	System.out.println("Found " + searchResult.count() + " results");
	System.out.println("Scoring Result");
	while (searchResult.hasNext()) {
	    result.add(score((T) searchResult.next()));
	}

	Collections.sort(result, new Comparator<T>() {
	    @Override
	    public int compare(T a, T b) {
		return Double.compare(b.getDouble("score"), a.getDouble("score"));
	    }
	});

	return result;
    }

    public static <B extends SearchableDBObject> List<B> search(DBCollection collection, String query) {
	GenericBM25Ranking<B> ranker = new GenericBM25Ranking<B>(collection, query);
	return ranker.search();
    }

    private T score(T sample) {
	double score = 0;
	for (String term : queryTerms) {
	    score += scoreTerm(sample, term);
	}

	sample.put("score", score);
	return sample;
    }

    private double scoreTerm(T sample, String term) {
	double fqd = sample.getTermFrequency(term);
	double idf = IDF.get(term);

	double dividend = fqd * (K + 1);
	double divisor = fqd + K * (1 - B + B * ((double) sample.getLength() / averageLength));

	return idf * (dividend / divisor);
    }

    private Map<String, Double> preCalculateIDFs() {
	System.out.println("Calculating IDF values");

	Map<String, Double> IDF = new HashMap<String, Double>();

	long totalDocs = collection.count();
	for (String term : queryTerms) {
	    BasicDBObject nqi = (BasicDBObject)collection.getDB().getCollection(collection.getName()+"_nqi").findOne(new BasicDBObject("_id", term));
	    if (nqi == null) {
		continue;
	    }
	    long foundDocs = ((BasicDBObject)nqi.get("value")).getLong("count");
	    IDF.put(term, idf(totalDocs, foundDocs));
	}
	
	return IDF;
    }

    
    private double idf(long totalDocs, long foundDocs) {
	return Math.log((totalDocs - foundDocs + 0.5) / (foundDocs + 0.5));
    }

    
    private double preCalculateAverageDocumentLength(){
	DBCollection averageDocCollection = collection.getDB().getCollection(collection.getName()+"_avg");
	if (averageDocCollection.count() == 0) {
	    System.out.println("Calculating average document length");
	    String map;
	    String reduce;
	    try {
		map    = readFile("src/ranking/avgDocLength_map.js");
		reduce = readFile("src/ranking/avgDocLength_reduce.js");
	    } catch (IOException e) {
		e.printStackTrace();
		return 12.0;
	    } catch (URISyntaxException e) {
		e.printStackTrace();
		return 12.0;
	    } 
	    DBObject query = new BasicDBObject();
	    collection.mapReduce(map, reduce, collection.getName()+"_avg", query);
	}

	return (Double) collection.getDB().getCollection(collection.getName()+"_avg").findOne().get("value");
    }

    
    private void preCalculateTermDocumentCounts()  {
	DBCollection nqiCollection = collection.getDB().getCollection(collection.getName()+"_nqi");
	if (nqiCollection.count() == 0) {
	    System.out.println("Reindexing term counts");

	    String map;
	    String reduce;
	    
	    try {
		map     = readFile("src/ranking/nqi_map.js");
		reduce  = readFile("src/ranking/nqi_reduce.js");
		DBObject query = new BasicDBObject();

		collection.mapReduce(map, reduce, collection.getName()+"_nqi", query);
	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (URISyntaxException e) {
		e.printStackTrace();
	    } 
	}
    }
    
    
    
    private static String readFile(String path) throws IOException, URISyntaxException {
	FileInputStream stream = new FileInputStream(new File(path));
	try {
	    FileChannel fc = stream.getChannel();
	    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
	    /* Instead of using default, pass in a decoder. */
	    return Charset.defaultCharset().decode(bb).toString();
	} finally {
	    stream.close();
	}
    }
}
