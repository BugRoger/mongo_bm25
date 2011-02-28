package db;


public class Sample extends SearchableDBObject {
    private static final long serialVersionUID = -6462198786313032776L;

    public Sample() {
	super();
    }
    
    public Sample(String text) {
	this.put("text", text);
	this.index("text");
    }

    public String getText() {
	return this.get("text").toString();
    }

}
