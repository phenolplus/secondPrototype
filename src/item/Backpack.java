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
    
    public void getItem(String name) {
    	returnItem(name).getItem();
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
    
    public ArrayList<String> getItemList() {
    	return itemListInStage;
    }
    
 // All available items
     
     private static final String[] itemName = new String[]
     {"Compass", "Goggles", "Flashlight", "Hammer", "Telescope",
      "Ruby", "Sapphire", "Emerald", "Topaz", "Amethyst", 
      "Diamond", "Aquamarine", "Citrine", "Peridot"};
     
     private static final int[] itemImageBlack = new int[]
     { R.drawable.compass_silhouette, R.drawable.goggles_silhouette, 
       R.drawable.flashlight_silhouette, R.drawable.hammer_silhouette, 
       R.drawable.telescope_silhouette,
       R.drawable.ruby_silhouette, second.prototype.R.drawable.sapphire_silhouette, 
       R.drawable.emerald_silhouette, second.prototype.R.drawable.topaz_silhouette, 
       R.drawable.amethyst_silhouette, R.drawable.diamond_silhouette, 
       R.drawable.aquamarine_silhouette, R.drawable.citrine_silhouette, 
       R.drawable.peridot_silhouette
     };
     
     private static final int[] itemImage = new int[]
     {R.drawable.compass, R.drawable.goggles, R.drawable.flashlight,
      R.drawable.hammer, R.drawable.telescope,
      R.drawable.ruby, R.drawable.sapphire, second.prototype.R.drawable.emerald,
      R.drawable.topaz, R.drawable.amethyst, R.drawable.diamond, 
      R.drawable.aquamarine, R.drawable.citrine, R.drawable.peridot};
}


