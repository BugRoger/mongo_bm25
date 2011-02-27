package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import token.LuceneTokenizer;

import com.mongodb.BasicDBObject;

public class Sample extends BasicDBObject {
	private static final long serialVersionUID = -6462198786313032776L;

	private static final LuceneTokenizer TOKENIZER = new LuceneTokenizer();
	
	public Sample(String text) {
		List<String> terms = TOKENIZER.tokenize(text);
		this.put("text",  text);
		this.put("terms", terms);
		this.put("tf",    unify(terms));
		this.put("length",terms.size());
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
	
}
