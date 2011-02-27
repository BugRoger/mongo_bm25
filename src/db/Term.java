package db;

import com.mongodb.BasicDBObject;

public class Term extends BasicDBObject {
	private static final long serialVersionUID = -113773567177610988L;

	public Term(String text, float weight) {
		this.put("t", text);
		this.put("w", weight);
	}
	
}
