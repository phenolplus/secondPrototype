package control.stage;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import second.prototype.ContainerBox;

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
	public boolean hasVisited = false;
	private String name;
	private String story;
	private final int bLeng = ContainerBox.isTab?18:10;
	
	public String image;
	public static final String[] targetList = {
		"targets01","targets02","targets03",
		"targets04","targets05","targets06"
	};
	
	public ArrayList<GameEvent> eventList = new ArrayList<GameEvent>();
	
	public PointBox(JSONObject json) throws NumberFormatException, JSONException {
		x = Float.parseFloat(json.getString("Position X"));
		y = Float.parseFloat(json.getString("Position Y"));
		name = json.getString("Name");
		story = json.getString("Story");
		order = json.getInt("Order");
		
		image = json.optString("Image");
		
		for(int i=0;i<json.getJSONArray("Events").length();i++){
			GameEvent event = new GameEvent(json.getJSONArray("Events").getJSONObject(i));
			eventList.add(event);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getStory() {
		return story;
	}
	
	public String getBrief() {
		if(story.length()<bLeng){
			return story;
		} else {
			return story.substring(0,bLeng)+" ...";
		}
	}
	
	public void checkVisable(int progress) {
		isVisible =  (progress>=order);
	}
	
	public void checkRange(float myX, float myY, float range) {
		double distance = Math.pow((myX-x), 2.0) + Math.pow((myY-y), 2.0);
		inRange = (distance<Math.pow(range, 2.0));
	}
	
	public String[] getNextPoints() {
		String[] names = new String[eventList.size()];
		for(int i=0;i<names.length;i++){
			names[i] = eventList.get(i).nextPoint;
		}
		return names;
	}
	
	public int numOfEvents() {
		return eventList.size();
	}
	
	public int getTarget() {
		for(int i=0;i<targetList.length;i++){
			if(image.contentEquals(targetList[i])){
				return i;
			}
		}
		return 0;
	}
}
