package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import token.LuceneTokenizer;
import token.Tokenizer;

import com.mongodb.BasicDBObject;

public class SearchableDBObject extends BasicDBObject {

    private static final long serialVersionUID = 8296982422300101384L;
    private static final Tokenizer TOKENIZER = new LuceneTokenizer();

    public SearchableDBObject() {
	super();
    }
    
    public void index(String ... fields) {
	List<String> terms = new ArrayList<String>();
	for (String field: fields) {
	    terms.addAll(TOKENIZER.tokenize(this.getString(field)));
	}
	
	this.put("tf", unify(terms));
	this.put("length", terms.size());
    }
    
    public int getLength() {
	return this.getInt("length");
    }
    
    
    private List<Term> unify(List<String> terms) {
	Map<String, Integer> uniqueTerms = new HashMap<String, Integer>();

	for (String term : terms) {
	    if (uniqueTerms.containsKey(term)) {
		uniqueTerms.put(term, uniqueTerms.get(term) + 1);
	    } else {
		uniqueTerms.put(term, 1);
	    }
	}

	List<Term> uniqueTermsList = new ArrayList<Term>();
	for (String term : uniqueTerms.keySet()) {
	    uniqueTermsList.add(new Term(term, uniqueTerms.get(term)));
	}

	return uniqueTermsList;
    }
    
    @SuppressWarnings("unchecked")
    public double getTermFrequency(String term) {
	List<BasicDBObject> terms = (List<BasicDBObject>)this.get("tf");
	for (BasicDBObject t: terms) {
	    if (t.get("t").equals(term)) {
		return (Double)t.get("w");
	    }
	}
	
	return 0;
    }
    
}
