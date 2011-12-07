package sys.item;

import item.Backpack;
import item.Item;

import java.util.ArrayList;
import java.util.HashMap;

import second.prototype.ContainerBox;
import second.prototype.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

public class ItemSystem extends Activity {
    
	private GridView grid;
    private ImageView image;
    private ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
    private SimpleAdapter adapter;
    
    // for construction purpose
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
        image = (ImageView) findViewById(R.id.imageView1);

        
        adapter = new SimpleAdapter(this, listItem, R.layout.grid_item,
        		new String[] {"ItemImage"},
        		new int[] {R.id.itemImage});
        grid.setAdapter(adapter);
        
        changeView();
        
        grid.setOnItemClickListener(new GridView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String name = backpack.getItemList().get(arg2);
				if(Backpack.returnItem(name).hasSeenItem())
					showDialogue(Backpack.returnItem(name));
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
        for(int i = 0; i < backpack.getItemLength(); i++) {
        	HashMap<String, Object> map = new HashMap<String, Object>();
        	String name = backpack.getItemList().get(i);
        	map.put("ItemImage", Backpack.returnItem(name).hasSeenItem()?
        			Backpack.returnItem(name).getImage():R.drawable.question);
    		listItem.add(map);
    		adapter.notifyDataSetChanged();
        }
        image.setImageResource(R.drawable.bag);
    }
	
	/*********************************************************/
	/*******************Saving preferences********************/
	/*********************************************************/
	
	protected void onPause() {
		super.onPause();
		//backpack.savePref();
	}
    
    
    /*********************************************************/
    /*********************************************************/
}