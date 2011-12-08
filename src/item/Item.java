package item;


public class Item {
	
	private String descript;
	private String name;
	private String iconname;
	private int posImage;
	private int negImage;
	private boolean hasSeen;
	private boolean has;
    
    public Item(String d, String n, String i, int pos_img, int neg_img, boolean h, boolean b) {
    	this.descript = d;
    	this.name = n;
    	this.iconname = i;
    	this.posImage = pos_img;
    	this.negImage = neg_img;
    	this.hasSeen = h;
    	this.has = b;
    	//Log.d("DebugLog", "Item "+n+" created");
    }
    
    public Item(Item i) {
    	this.descript = i.descript;
    	this.name = i.name;
    	this.iconname = i.iconname;
    	this.posImage = i.posImage;
    	this.negImage = i.negImage;
    	this.hasSeen = i.hasSeen;
    	this.has = false;
    }
    
    public void setDescript(String s) { this.descript = s; }
    
    public String getName() { return name; }
    public String getDescript() { return descript; }
    public String getIconName() { return iconname; }
    public int getImage() {
    	return (this.has)?posImage:negImage;
    }
    public boolean hasSeenItem() { return hasSeen; }
    public boolean hasItem() { return has; }
    public void getItem() { this.hasSeen = true; this.has = true; }
    public void throwItem() { this.has = false; }
    public void reset() { this.has = false; this.hasSeen = false; }
}
