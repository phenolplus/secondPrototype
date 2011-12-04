package second.prototype;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import control.stage.Stage;
import control.stage.StageManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class StartPage extends Activity {
	/** Members */
	private ListView stagesView;
	private ArrayList<HashMap<String, String>> stageList = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter adapter;
	private int cursor = -1;

	private SharedPreferences savedStages;
	private SharedPreferences.Editor editor;
	
	private StageManager manager;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stagescreen);

		stagesView = (ListView) findViewById(R.id.stagelist);
		savedStages = this.getSharedPreferences("Global Data",
				Context.MODE_PRIVATE);
	}

	public void onPause() {
		super.onPause();
		saveStageList();
	}
	
	public void onResume() {
		super.onResume();
		if(savedStages.getBoolean("First Play", true)){
			StageManager.initFileSettings(this);
		}
		reBuildStageList();
	}

	/** Utilities */
	private void reBuildStageList() {
		
		
		manager = new StageManager(this);
		
		for(int i=0;i<manager.numOfStages();i++){
			Stage stage = manager.getStage(i);
			
			HashMap<String,String> item = new HashMap<String,String>();
			item.put("Name", stage.getName());
			item.put("Description", stage.getDescription());
			stageList.add(item);
		}
		
		adapter = new SimpleAdapter(this, stageList,
				android.R.layout.simple_list_item_2, new String[] { "Name",
						"Description" }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		stagesView.setAdapter(adapter);

		stagesView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				cursor = arg2;
				StartPage.this.setTitle("Selected Stage "
						+ stageList.get(cursor).get("Name"));
			}

		});
		
		System.gc();
	}

	private void saveStageList() {
		// store list
		manager.commit();
		stageList.clear();
		
		System.gc();
	}
	
	/** Menu Control 
	 *  These are Programmer tasks ...*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "Refreash initial Data").setIcon(android.R.drawable.ic_menu_upload);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()){
		case 0:
			StageManager.initFileSettings(this);
			break;
		default :		
		}
		return true;
	}
	
	/** Button onClick listeners */
	public void playClicked(View view) {
		if (cursor < 0) {
			Toast.makeText(this, "What Stage to play ?", Toast.LENGTH_SHORT)
					.show();
		} else {
			ContainerBox.currentStage = manager.getStage(cursor);
			Intent playStage = new Intent();
			playStage.setClass(this, MapMode.class);
			startActivity(playStage);
		}
		cursor = -1;
	}

	public void addClicked(View view) {
		
		cursor = -1;
	}

	public void deleteClicked(View view) {
		if (cursor < 0) {
			Toast.makeText(this, "Select a stage first", Toast.LENGTH_SHORT)
					.show();
		} else {
			
		}
		cursor = -1;
	}

}