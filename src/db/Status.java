package db;

import com.mongodb.BasicDBObject;

public class Status extends BasicDBObject {
	private static final long serialVersionUID = -6524957805242757564L;

	public Status(twitter4j.Status status) {
		this.put("created_at", status.getCreatedAt());
		this.put("screen_name", status.getUser().getScreenName());
		this.put("text", status.getText());
		this.put("id", status.getId());
	}
}
