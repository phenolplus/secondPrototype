package control.stage;

import item.Backpack;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import second.prototype.ContainerBox;

public class GameEvent {
	
	boolean rSatisfied = true;
	boolean rNSatisfied = true;
	Backpack backpack = ContainerBox.backback;
	
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
		 Log.e("Event","R = "+itemR+"  N = "+itemN);
		 //rSatisfied = backpack.hasItem(itemR);
		 //rNSatisfied = !backpack.hasItem(itemN);
		 
		if(rSatisfied&&rNSatisfied){
			return messageP;
		} else {
			return messageN;
		}
	}
	
}
