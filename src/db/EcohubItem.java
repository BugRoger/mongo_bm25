package db;

public class EcohubItem extends SearchableDBObject {

    private static final long serialVersionUID = -6462198786313032776L;

    public EcohubItem() {
	super();
    }
    
    public EcohubItem(String title, String summary, String keyFeatures, String description) {
	this.put("title", title);
	this.put("summary", summary);
	this.put("features", keyFeatures);
	this.put("description", description);
	this.index("title", "summary", "features", "description");
    }
    
    public String toString() {
	return this.getString("title")+"\n"+this.get("summary")+"\n"+this.get("features");
    }
    
}
