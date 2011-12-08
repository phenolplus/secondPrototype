package item;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import second.prototype.R;

public class Backpack {
    
    // Library
    private static ArrayList<String> itemList = new ArrayList<String>();
    
    // Sharedpreferences
    private SharedPreferences progressData;
    private SharedPreferences.Editor editor;
    private boolean isFirstVisit = true;
    
    // Construction
    private ArrayList<String> nameList = new ArrayList<String>();
    private ArrayList<String> descriptionList = new ArrayList<String>();
    private ArrayList<String> iconnameList = new ArrayList<String>();
    private ArrayList<Boolean> hasItemList = new ArrayList<Boolean>();
    private ArrayList<Boolean> hasSeenList = new ArrayList<Boolean>();
    
    private ArrayList<String> itemListInStage = new ArrayList<String>();
    private static HashMap<String, Item> itemInStage_Item;
    
    
    private static void buildLib() {
    	Log.d("DebugLog", "Building library...");
    	for(int i = 0; i < itemName.length; i++) {
    		itemList.add(itemName[i]);
    	}
    }
	
    public Backpack() {
    	buildLib();
    }
    
    public Backpack(String _filename, Context _owner, JSONArray _json) {
    	buildLib();
    	
    	progressData = _owner.getSharedPreferences(_filename, Context.MODE_PRIVATE);
    	editor = progressData.edit();
    	isFirstVisit = progressData.getBoolean("FIRST_VISIT", true);
    	
    	if(isFirstVisit) {
    		// Use JSONArray to construct
    		Log.d("DebugLog", "First visit, using JSONArray to construct!");
    		for(int i = 0; i < _json.length(); i++) {
    			try {
					nameList.add(_json.getJSONObject(i).getString("Name"));
					Log.e("Item", "Item "+_json.getJSONObject(i).getString("Name")+" added!");
					descriptionList.add(_json.getJSONObject(i).getString("Description"));
					iconnameList.add(_json.getJSONObject(i).getString("Icon"));
					hasSeenList.add(false);
					hasItemList.add(false);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		isFirstVisit = false;
    	}
    	else {
    		// Use preference to construct
    		Log.d("DebugLog", "Using preference to construct!");
    		int itemListSize = progressData.getInt("ITEM_LIST_LENGTH", 0);
    		for(int i = 0; i < itemListSize; i++) {
    			nameList.add(progressData.getString("ITEM_NAME"+i, ""));
    			descriptionList.add(progressData.getString("DESCRIPT_ITEM"+i, ""));
    			iconnameList.add(progressData.getString("ITEM_ICON_NAME"+i, ""));
    			hasSeenList.add(progressData.getBoolean("HAS_SEEN"+i, false));
    			hasItemList.add(progressData.getBoolean("HAS_ITEM"+i, false));
    		}
    	}
    	construct();
    }
    
    private void construct() {
    	itemInStage_Item = new HashMap<String, Item>();
    	
    	for(int i = 0; i < nameList.size(); i++) {
    		String name = nameList.get(i);
    		String des = descriptionList.get(i);
    		String iconname = iconnameList.get(i);
    		if(itemList.contains(iconname)) {
    			int index = itemList.indexOf(iconname);
    			Item item = new Item(des, name, iconname, itemImage[index],
    						itemImageBlack[index], hasSeenList.get(i), hasItemList.get(i));
    			itemListInStage.add(name);
    			itemInStage_Item.put(name, item);
    		}
    	}
    }
    
    public void savePref() {
    	Log.e("DebugLog", "Saving preferences...");
    	
    	editor.putBoolean("FIRST_VISIT", isFirstVisit);
    	editor.putInt("ITEM_LIST_LENGTH", itemListInStage.size());
    	for(int i = 0; i < itemListInStage.size(); i++) {
    		String name = itemListInStage.get(i);
    		Item item = returnItem(name);
    		editor.putString("ITEM_NAME"+i, item.getName());
    		editor.putString("DESCRIPT_ITEM"+i, item.getDescript());
    		editor.putString("ITEM_ICON_NAME"+i, item.getIconName());
    		editor.putBoolean("HAS_SEEN"+i, item.hasSeenItem());
    		editor.putBoolean("HAS_ITEM"+i, item.hasItem());
    	}
    	editor.commit();
    }
    
    public int getItemLength() {
    	return itemListInStage.size();
    }
    
    public static Item returnItem(String name) {
    	if(itemInStage_Item.containsKey(name)) {
    		return itemInStage_Item.get(name);
    	}
    	else return null;
    }
    
    public static void getItem(String name) {
    	if(name.equals("NULL"))
    		return;
    	else returnItem(name).getItem();
    }
    
    public void throwItem(String name) {
    	if(returnItem(name).hasItem())
    		returnItem(name).throwItem();
    }
    
    public static boolean hasItem(String name) {
    	if(name.equals("NULL"))
    		return true;
    	else
    		return returnItem(name).hasItem();
    }
    
    public static boolean hasNoItem(String name) {
    	if(name.equals("NULL"))
    		return true;
    	else return !returnItem(name).hasItem();
    }
    
    public ArrayList<String> getItemList() {
    	return itemListInStage;
    }
    
    public void clearBackpack() {
    	for(int i = 0; i < nameList.size(); i++) {
    		returnItem(nameList.get(i)).reset();
    	}
    }
    
 // All available items
     
     private static final String[] itemName = new String[]
     {"compass", "goggles", "flashlight", "hammer", "telescope",
      "antenna", "bag", "alarmclock", "bat", "battery", 
      "battery2","bolt", "bomb", "bomb2", "bow", 
      "camera", "clock1", "clock2", "flask", "gear",
      "gear2", "gear3", "guitar", "gun", "handbell", 
      "handbell2", "helmet", "helmet2", "helmet3", "key", 
      "key2", "key3", "kunai", "lock", "magichat", 
      "magnifier", "paper", "pearl", "pickaxe", "pocketwatch", 
      "potion", "screwdriver", "shovel", "sword", "tools1", 
      "tools2", "tools3", "torch", "treasure", "video", 
      "witchhat", "ruby", "aapphire", "emerald", "topaz", 
      "amethyst", "diamond", "aquamarine", "citrine", "peridot"};
    
     
     private static final int[] itemImageBlack = new int[]
     { R.drawable.compass_silhouette, R.drawable.goggles_silhouette, 
    	 R.drawable.flashlight_silhouette, R.drawable.hammer_silhouette, 
    	 R.drawable.telescope_silhouette, R.drawable.antenna_silhouette,
		 R.drawable.bag_silhouette, R.drawable.alarmclock_silhouette, 
		 R.drawable.bat_silhouette, R.drawable.battery_silhouette,
		 R.drawable.battery2_silhouette, R.drawable.bolt_silhouette, 
		 R.drawable.bomb_silhouette, R.drawable.bomb2_silhouette,
		 R.drawable.bow_silhouette, R.drawable.camera_silhouette, 
		 R.drawable.clock1_silhouette, R.drawable.clock2_silhouette,
		 R.drawable.flask_silhouette, R.drawable.gear_silhouette, 
		 R.drawable.gear2_silhouette, R.drawable.gear3_silhouette,
		 R.drawable.guitar_silhouette, R.drawable.gun_silhouette, 
		 R.drawable.handbell_silhouette, R.drawable.handbell2_silhouette,
		 R.drawable.helmet_silhouette, R.drawable.helmet2_silhouette, 
		 R.drawable.helmet3_silhouette, R.drawable.key_silhouette, 
		 R.drawable.key2_silhouette, R.drawable.key3_silhouette, 
		 R.drawable.kunai_silhouette, R.drawable.lock_silhouette, 
		 R.drawable.magichat_silhouette, R.drawable.magnifier_silhouette,
		 R.drawable.paper_silhouette, R.drawable.pearl_silhouette, 
		 R.drawable.pickaxe_silhouette, R.drawable.pocketwatch_silhouette,
		 R.drawable.potion_silhouette, R.drawable.screwdriver_silhouette, 
		 R.drawable.shovel_silhouette, R.drawable.sword_silhouette,
		 R.drawable.tools1_silhouette, R.drawable.tools2_silhouette, 
		 R.drawable.tools3_silhouette, R.drawable.torch_silhouette,
		 R.drawable.treasure_silhouette, R.drawable.video_silhouette, 
		 R.drawable.witchhat_silhouette, R.drawable.ruby_silhouette, 
		 R.drawable.sapphire_silhouette, R.drawable.emerald_silhouette,
		 R.drawable.topaz_silhouette, R.drawable.amethyst_silhouette, 
		 R.drawable.diamond_silhouette, R.drawable.aquamarine_silhouette, 
		 R.drawable.citrine_silhouette, R.drawable.peridot_silhouette 
     };
     
     private static final int[] itemImage = new int[]
     { R.drawable.compass, R.drawable.goggles, R.drawable.flashlight,
      R.drawable.hammer, R.drawable.telescope, R.drawable.antenna,
      R.drawable.bag, R.drawable.alarmclock, R.drawable.bat, R.drawable.battery,
      R.drawable.battery2, R.drawable.bolt, R.drawable.bomb, R.drawable.bomb2,
      R.drawable.bow, R.drawable.camera, R.drawable.clock1, R.drawable.clock2,
      R.drawable.flask, R.drawable.gear, R.drawable.gear2, R.drawable.gear3,
      R.drawable.guitar, R.drawable.gun, R.drawable.handbell, R.drawable.handbell2,
      R.drawable.helmet, R.drawable.helmet2, R.drawable.helmet3,
      R.drawable.key, R.drawable.key2, R.drawable.key3, R.drawable.kunai,
      R.drawable.lock, R.drawable.magichat, R.drawable.magnifier,
      R.drawable.paper, R.drawable.pearl, R.drawable.pickaxe, R.drawable.pocketwatch,
      R.drawable.potion, R.drawable.screwdriver, R.drawable.shovel, R.drawable.sword,
      R.drawable.tools1, R.drawable.tools2, R.drawable.tools3, R.drawable.torch,
      R.drawable.treasure, R.drawable.video, R.drawable.witchhat,
      R.drawable.ruby, R.drawable.sapphire, R.drawable.emerald,
      R.drawable.topaz, R.drawable.amethyst, R.drawable.diamond, 
      R.drawable.aquamarine, R.drawable.citrine, R.drawable.peridot };
}


