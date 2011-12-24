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
    	     {"alarmclock", "alarmclock2", "antenna", "bat", "beaker",
    	      "bell", "bomb", "bomb2", "book", "book2",
    	      "briefcase", "calculator", "camera", "cd", "cellphone",
    	      "clock", "clock2", "compass", "compass2", "crown",
    	      "cup", "delete", "diamond", "diamond2", "disk",
    	      "document", "document2", "drum", "fire", "flag",
    	      "flashlight", "folder", "garland", "gear", "gear2",
    	      "gift", "gramophone", "guitar", "gun", "hammer",
    	      "hat", "hat2", "helmet", "helmet2", "helmet3",
    	      "hourglass", "jukebox", "key", "key2", "lock",
    	      "lock2", "magnifier", "map", "medicine", "medikit",
    	      "mic", "mic2", "mic3", "mirror", "monitor",
    	      "movie", "music", "newspaper", "nipper", "nuclear",
    	      "paint", "pallet", "pallet2", "pencil", "radar",
    	      "radio", "ribbon", "safe", "satellite", "search",
    	      "shovel", "snowflake", "snowman", "spanner", "star",
    	      "star2", "stethoscope", "sun", "sunglasses", "syringe",
    	      "telescope", "tellurian", "thermometer", "tool", "torch",
    	      "treasure", "tree", "trumpet", "umbrella", "video",
    	      "video2", "violin", "violin2", "watch", "whistle",
    	      "witchhat"};
    	    
    	     
    	     private static final int[] itemImageBlack = new int[]
    	     { R.drawable.alarmclock_silhouette, 	R.drawable.alarmclock2_silhouette,
    	       R.drawable.antenna_silhouette, 		R.drawable.bat_silhouette,
    	       R.drawable.beaker_silhouette, 		R.drawable.bell,
    	       R.drawable.bomb_silhouette, 			R.drawable.bomb2_silhouette,
    	       R.drawable.book_silhouette, 			R.drawable.book2_silhouette,
    	       R.drawable.briefcase_silhouette, 	R.drawable.calculator_silhouette,
    	       R.drawable.camera_silhouette, 		R.drawable.cd_silhouette,
    	       R.drawable.cellphone_silhouette, 	R.drawable.clock_silhouette,
    	       R.drawable.clock2_silhouette, 		R.drawable.compass_silhouette,
    	       R.drawable.compass2_silhouette, 		R.drawable.crown_silhouette,
    	       R.drawable.cup_silhouette, 			R.drawable.delete_silhouette,
    	       R.drawable.diamond_silhouette, 		R.drawable.diamond2_silhouette,
    	       R.drawable.disk_silhouette, 			R.drawable.document_silhouette,
    	       R.drawable.document2_silhouette, 	R.drawable.drum_silhouette,
    	       R.drawable.fire_silhouette, 			R.drawable.flag_silhouette,
    	       R.drawable.flashlight_silhouette, 	R.drawable.folder_silhouette,
    	       R.drawable.garland_silhouette, 		R.drawable.gear_silhouette,
    	       R.drawable.gear2_silhouette, 		R.drawable.gift_silhouette,
    	       R.drawable.gramophone_silhouette, 	R.drawable.guitar_silhouette,
    	       R.drawable.gun_silhouette, 			R.drawable.hammer_silhouette,
    	       R.drawable.hat_silhouette, 			R.drawable.hat2_silhouette, 
    	       R.drawable.helmet_silhouette, 		R.drawable.helmet2_silhouette,
    	       R.drawable.helmet3_silhouette, 		R.drawable.hourglass_silhouette, 
    	       R.drawable.jukebox_silhouette, 		R.drawable.key_silhouette,
    	       R.drawable.key2_silhouette, 			R.drawable.lock_silhouette,
    	       R.drawable.lock2_silhouette, 		R.drawable.magnifier_silhouette, 
    	       R.drawable.map_silhouette, 			R.drawable.medicine_silhouette, 
    	       R.drawable.medikit_silhouette, 		R.drawable.mic_silhouette, 
    	       R.drawable.mic2_silhouette, 			R.drawable.mic3_silhouette,
    	       R.drawable.mirror_silhouette, 		R.drawable.monitor_silhouette,
    	       R.drawable.movie_silhouette, 		R.drawable.music_silhouette, 
    	       R.drawable.newspaper_silhouette, 	R.drawable.nipper_silhouette,
    	       R.drawable.nuclear_silhouette, 		R.drawable.paint_silhouette,
    	       R.drawable.pallet_silhouette, 		R.drawable.pallet2_silhouette,
    	       R.drawable.pencil_silhouette, 		R.drawable.radar_silhouette, 
    	       R.drawable.radio_silhouette, 		R.drawable.ribbon_silhouette,
    	       R.drawable.safe_silhouette, 			R.drawable.satellite_silhouette, 
    	       R.drawable.search_silhouette, 		R.drawable.shovel_silhouette,
    	       R.drawable.snowflake_silhouette, 	R.drawable.snowman_silhouette,
    	       R.drawable.spanner_silhouette, 		R.drawable.star_silhouette,
    	       R.drawable.star2_silhouette, 		R.drawable.stethoscope_silhouette,
    	       R.drawable.sun_silhouette, 			R.drawable.sunglasses_silhouette,
    	       R.drawable.syringe_silhouette, 		R.drawable.telescope_silhouette,
    	       R.drawable.tellurian_silhouette, 	R.drawable.thermometer_silhouette,
    	       R.drawable.tool_silhouette, 			R.drawable.torch_silhouette,
    	       R.drawable.treasure_silhouette, 		R.drawable.tree_silhouette,
    	       R.drawable.trumpet_silhouette, 		R.drawable.umbrella_silhouette,
    	       R.drawable.video_silhouette, 		R.drawable.video2_silhouette,
    	       R.drawable.violin_silhouette, 		R.drawable.violin_silhouette,
    	       R.drawable.watch_silhouette, 		R.drawable.whistle_silhouette,
    	       R.drawable.witchhat_silhouette
    	     };
    	     
    	     private static final int[] itemImage = new int[]
    	     { 
    	    	 R.drawable.alarmclock, 	R.drawable.alarmclock2,
    			 R.drawable.antenna, 		R.drawable.bat,
    			 R.drawable.beaker, 		R.drawable.bell,
    			 R.drawable.bomb, 			R.drawable.bomb2,
    			 R.drawable.book, 			R.drawable.book2,
    			 R.drawable.briefcase, 		R.drawable.calculator,
    			 R.drawable.camera, 		R.drawable.cd,
    			 R.drawable.cellphone, 		R.drawable.clock,
    			 R.drawable.clock2, 		R.drawable.compass,
    			 R.drawable.compass2, 		R.drawable.crown,
    			 R.drawable.cup, 			R.drawable.delete,
    			 R.drawable.diamond, 		R.drawable.diamond2,
    			 R.drawable.disk, 			R.drawable.document,
    			 R.drawable.document2, 		R.drawable.drum,
    			 R.drawable.fire, 			R.drawable.flag,
    			 R.drawable.flashlight, 	R.drawable.folder,
    			 R.drawable.garland, 		R.drawable.gear,
    			 R.drawable.gear2, 			R.drawable.gift,
    			 R.drawable.gramophone, 	R.drawable.guitar,
    			 R.drawable.gun, 			R.drawable.hammer,
    			 R.drawable.hat, 			R.drawable.hat2, 
    			 R.drawable.helmet, 		R.drawable.helmet2,
    			 R.drawable.helmet3, 		R.drawable.hourglass, 
    			 R.drawable.jukebox, 		R.drawable.key,
    			 R.drawable.key2, 			R.drawable.lock,
    			 R.drawable.lock2, 			R.drawable.magnifier, 
    			 R.drawable.map, 			R.drawable.medicine, 
    			 R.drawable.medikit, 		R.drawable.mic, 
    			 R.drawable.mic2, 			R.drawable.mic3,
    			 R.drawable.mirror, 		R.drawable.monitor,
    			 R.drawable.movie, 			R.drawable.music, 
    			 R.drawable.newspaper, 		R.drawable.nipper,
    			 R.drawable.nuclear, 		R.drawable.paint,
    			 R.drawable.pallet, 		R.drawable.pallet2,
    			 R.drawable.pencil, 		R.drawable.radar, 
    			 R.drawable.radio, 			R.drawable.ribbon,
    			 R.drawable.safe, 			R.drawable.satellite, 
    			 R.drawable.search, 		R.drawable.shovel,
    			 R.drawable.snowflake, 		R.drawable.snowman,
    			 R.drawable.spanner, 		R.drawable.star,
    			 R.drawable.star2, 			R.drawable.stethoscope,
    			 R.drawable.sun, 			R.drawable.sunglasses,
    			 R.drawable.syringe, 		R.drawable.telescope,
    			 R.drawable.tellurian, 		R.drawable.thermometer,
    			 R.drawable.tool, 			R.drawable.torch,
    			 R.drawable.treasure, 		R.drawable.tree,
    			 R.drawable.trumpet, 		R.drawable.umbrella,
    			 R.drawable.video, 			R.drawable.video2,
    			 R.drawable.violin, 		R.drawable.violin,
    			 R.drawable.watch, 			R.drawable.whistle,
    			 R.drawable.witchhat};
    	}


