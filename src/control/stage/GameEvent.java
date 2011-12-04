package control.stage;

import org.json.JSONException;
import org.json.JSONObject;

public class GameEvent {
	
	boolean rSatisfied = true;
	boolean rNSatisfied = true;
	
	String itemToGet;
	String messageP,messageN;
	String itemR,itemN;
	String nextPoint;
	
	public GameEvent(JSONObject object) throws JSONException {
		itemToGet = object.getString("Get Item");
		itemR = object.getString("Require Item");
		itemN = object.getString("Require No Item");
		
		messageP = object.getString("Message Positive");
		messageN = object.getString("Message Negative");
		nextPoint = object.getString("Next Point");
	}
	
	public String postMessage() {
		/*
		 * rSatisfied = backpack.has(itemR);
		 * rNSatisfied = !backpack.has(itemN);
		 * 
		 */
		if(rSatisfied&&rNSatisfied){
			return messageP;
		} else {
			return messageN;
		}
	}
	
}
