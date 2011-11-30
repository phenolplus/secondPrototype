package control.stage;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * This class manages a single point
 * 
 * @author vincentlee
 *
 */

public class PointBox {
	public float x,y;
	public int order;
	public boolean inRange = false;
	public boolean isVisible = false;
	private String name;
	private String story;
	
	public HashMap<String,String> media = new HashMap<String,String>();
	
	public PointBox(JSONObject json) throws NumberFormatException, JSONException {
		x = Float.parseFloat(json.getString("Position X"));
		y = Float.parseFloat(json.getString("Position Y"));
		name = json.getString("Name");
		story = json.getString("Story");
		order = json.getInt("Order");
		
		media.put("Image",json.optString("Image"));
		media.put("Movie",json.optString("Movie"));
	}
	
	public String getName() {
		return name;
	}
	
	public String getStory() {
		return story;
	}
	
	public String getBrief() {
		return story.substring(0, 10)+" ...";
	}
	
	public void checkVisable(int progress) {
		isVisible =  (progress>order);
	}
	
	public void checkRange(float myX, float myY, float range) {
		double distance = Math.pow(myX, 2.0) + Math.pow(myY, 2.0);
		inRange = (distance<Math.pow(range, 2.0));
	}
	
	public void onClick() {
		// TODO
		Log.e("Point event",name+" Clicked");
		
	}
}
