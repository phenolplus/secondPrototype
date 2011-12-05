package sys.item;

import item.Backpack;
import item.Item;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import second.prototype.ContainerBox;
import second.prototype.R;

public class ItemSystem extends Activity {
    
	private GridView grid;
    private ImageView image;
    private ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    private SimpleAdapter adapter;
    private Button itemBtn, stoneBtn, eventBtn;
    private int page = 0;
    
    // for construction purpose
    private static boolean isFirstVisit;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<String> descript = new ArrayList<String>();
    private ArrayList<Boolean> hasItem = new ArrayList<Boolean>();
    private Backpack backpack = ContainerBox.backback;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main);
        
        grid = (GridView) findViewById(R.id.gridView1);
        stoneBtn = (Button) findViewById(R.id.button1);
        itemBtn = (Button) findViewById(R.id.button2);
        eventBtn = (Button) findViewById(R.id.button3);
        image = (ImageView) findViewById(R.id.imageView1);
        
        /************************************************/
        
        /*SharedPreferences settings = getSharedPreferences("PREF_LIST", 0);
        isFirstVisit = settings.getBoolean("FIRST_VISIT", true);
        Log.d("DebugLog", "Frist time visit = "+((isFirstVisit)?"True":"False"));
        
        if(isFirstVisit) {
	        Log.d("DebugLog", "First time visit!");
        	for(int i = 0; i < stone.length; i++) {
	        	list.add(stone[i]);
	        	descript.add(des[i]);
	        }
	        backpack = new Backpack(list, descript);
	        isFirstVisit = false;
        }
        
        else {
        	Log.d("DebugLog", "Restoring preferences...");
        	restorePref();
        	backpack = new Backpack(list, descript, hasItem);
        }*/
        
        /************************************************/
        
        adapter = new SimpleAdapter(this, listItem, R.layout.grid_item,
        		new String[] {"ItemImage"},
        		new int[] {R.id.itemImage});
        grid.setAdapter(adapter);
        
        changeView();
        
        stoneBtn.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		page = 0;
        		changeView();
        	}
        });
        itemBtn.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		page = 1;
        		changeView();
        	}
        });
        eventBtn.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		page = 2;
        		changeView();
        	}
        });
        
        grid.setOnItemClickListener(new GridView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String name = null;
				/*switch(page) {
				case 0:
					name = backpack.getStoneList().get(arg2);
					if(backpack.hasItem(name))
						backpack.returnItem(name).throwItem();
					else
						backpack.returnItem(name).getItem();
					changeView();
					break;
				case 1:
					name = backpack.getItemList().get(arg2);
					if(backpack.hasItem(name))
						backpack.returnItem(name).throwItem();
					else
						backpack.returnItem(name).getItem();
					changeView();
					break;
				case 2:
					name = backpack.getItemList().get(arg2);
					if(backpack.hasItem(name))
						backpack.returnItem(name).throwItem();
					else
						backpack.returnItem(name).getItem();
					changeView();
					break;
				}*/
				
				showDialogue(backpack.returnItem(name));
			}
        	
        });
    }

	protected void showDialogue(final Item item) {
		// TODO Auto-generated method stub
    	LayoutInflater inflater = LayoutInflater.from(this);  
        final View textEntryView = inflater.inflate(R.layout.item_dialogue, null);  
        ImageView itemIcon = (ImageView) textEntryView.findViewById(R.id.itemIcon);
        TextView itemDescription = (TextView) textEntryView.findViewById(R.id.itemDescript);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(textEntryView);
        
        itemIcon.setImageResource(item.getImage());
        itemDescription.setText(item.hasItem()?item.getDescript():"?????");
        
        
        builder.setPositiveButton("Throw", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder2 = new AlertDialog.Builder(ItemSystem.this);
				builder2.setMessage("Are you sure you want to throw the item?")
				       .setCancelable(false)
				       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               item.throwItem();
				               changeView();
				           }
				       })
				       .setNegativeButton("No", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				           }
				       });
				builder2.show();
			}
		});
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
        builder.show();
	}

	protected void changeView() {
		// TODO Auto-generated method stub
    	listItem.clear();
    	adapter.notifyDataSetChanged();
    	switch(page) {
        case 0: 
	        stoneBtn.setBackgroundResource(R.drawable.button_red);
	        stoneBtn.setTextAppearance(getApplicationContext(), R.style.boldText);
	        itemBtn.setBackgroundResource(R.drawable.button);
	        itemBtn.setTextAppearance(getApplicationContext(), R.style.boldText);
	        eventBtn.setBackgroundResource(R.drawable.button);
	        eventBtn.setTextAppearance(getApplicationContext(), R.style.boldText);
	        
	        for(int i = 0; i < backpack.getStoneLength(); i++) {
	        	HashMap<String, Object> map = new HashMap<String, Object>();
	        	String name = backpack.getStoneList().get(i);
	        	map.put("ItemImage", backpack.returnItem(name).getImage());
        		listItem.add(map);
        		adapter.notifyDataSetChanged();
	        }
	        image.setImageResource(R.drawable.treasure);
	        
	        break;
        case 1:
        	itemBtn.setBackgroundResource(R.drawable.button_red);
	        itemBtn.setTextAppearance(getApplicationContext(), R.style.boldText);
	        stoneBtn.setBackgroundResource(R.drawable.button);
	        stoneBtn.setTextAppearance(getApplicationContext(), R.style.boldText);
	        eventBtn.setBackgroundResource(R.drawable.button);
	        eventBtn.setTextAppearance(getApplicationContext(), R.style.boldText);
	        
	        for(int i = 0; i < backpack.getItemLength(); i++) {
	        	HashMap<String, Object> map = new HashMap<String, Object>();
	        	String name = backpack.getItemList().get(i);
	        	map.put("ItemImage", backpack.returnItem(name).getImage());
        		listItem.add(map);
        		adapter.notifyDataSetChanged();
	        }
	        image.setImageResource(R.drawable.bag);
	        
	        break;
        case 2:
        	eventBtn.setBackgroundResource(R.drawable.button_red);
	        eventBtn.setTextAppearance(getApplicationContext(), R.style.boldText);
	        stoneBtn.setBackgroundResource(R.drawable.button);
	        stoneBtn.setTextAppearance(getApplicationContext(), R.style.boldText);
	        itemBtn.setBackgroundResource(R.drawable.button);
	        itemBtn.setTextAppearance(getApplicationContext(), R.style.boldText);
	        
	        for(int i = 0; i < backpack.getItemLength(); i++) {
	        	HashMap<String, Object> map = new HashMap<String, Object>();
	        	String name = backpack.getItemList().get(i);
	        	map.put("ItemImage", backpack.returnItem(name).getImage());
        		listItem.add(map);
        		adapter.notifyDataSetChanged();
	        }
	        image.setImageResource(R.drawable.paper);
	        
	        break;
        }
    }
	
	/*********************************************************/
	/*******************Saving preferences********************/
	/*********************************************************/
	
	protected void onPause() {
		super.onPause();
		backpack.savePref();
	}
    
    /*private void savePref() {
		// TODO Auto-generated method stub
    	Log.d("DebugLog", "Saving preferences...");
    	Log.d("DebugLog", "Frist time visit = "+((isFirstVisit)?"True":"False"));
    	SharedPreferences settings = getSharedPreferences("PREF_LIST", 0);
    	settings.edit().putBoolean("FIRST_VISIT", isFirstVisit).commit();
    	settings.edit().putInt("ITEM_LIST_LENGTH", backpack.getItemLength()).commit();
    	settings.edit().putInt("STONE_LIST_LENGTH", backpack.getStoneLength()).commit();
    	for(int i = 0; i < backpack.getItemLength(); i++) {
    		String name = backpack.getItemList().get(i);
    		Item item = backpack.returnItem(name);
    		settings.edit().putString("ITEM"+i, name).commit();
    		settings.edit().putBoolean("HAS_ITEM"+i, item.hasItem()).commit();
    		settings.edit().putString("DESCRIPT_ITEM"+i, item.getDescript()).commit();
    	}
    	for(int i = 0; i < backpack.getStoneLength(); i++) {
    		String name = backpack.getStoneList().get(i);
    		Item item = backpack.returnItem(name);
    		settings.edit().putString("STONE"+i, name).commit();
    		settings.edit().putBoolean("HAS_STONE"+i, item.hasItem()).commit();
    		settings.edit().putString("DESCRIPT_STONE"+i, item.getDescript()).commit();
    	}
	}
    
    private void restorePref() {
		// TODO Auto-generated method stub
    	SharedPreferences settings = getSharedPreferences("PREF_LIST", 0);
		int stoneListSize = settings.getInt("STONE_LIST_LENGTH", 0);
		int itemListSize = settings.getInt("ITEM_LIST_LENGTH", 0);
		for(int i = 0; i < stoneListSize; i++) {
			list.add(settings.getString("STONE"+i, ""));
			descript.add(settings.getString("DESCRIPT_STONE"+i, ""));
			hasItem.add(settings.getBoolean("HAS_STONE"+i, false));
		}
		for(int i = 0; i < itemListSize; i++) {
			list.add(settings.getString("ITEM"+i, ""));
			descript.add(settings.getString("DESCRIPT_ITEM"+i, ""));
			hasItem.add(settings.getBoolean("HAS_ITEM"+i, false));
		}
	}*/
    
    /*********************************************************/
    /*********************************************************/

	private static final String[] stone = new String[]
    {"Ruby", "Sapphire", "Emerald", "Topaz", "Amethyst", 
     "Diamond", "Aquamarine", "Citrine", "Peridot"};
    
    private static final String[] des = new String[] {
    "", "", "", "", "", "", "", "", ""
    };
}