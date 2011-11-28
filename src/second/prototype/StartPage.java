package second.prototype;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
	private View addNew;

	private SharedPreferences savedStages;
	private SharedPreferences.Editor editor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stagescreen);

		stagesView = (ListView) findViewById(R.id.stagelist);
	}

	public void onPause() {
		super.onPause();
		saveStageList();
	}
	
	public void onResume() {
		super.onResume();
		copyInitialFiles();
		reBuildStageList();
	}

	/** Utilities */
	private void reBuildStageList() {
		savedStages = this.getSharedPreferences("Global Data",
				Context.MODE_PRIVATE);
		
		
		try {
			JSONArray list = new JSONArray(savedStages.getString("Stages", ContainerBox.default_stage_list));
			
			for(int i=0;i<list.length();i++){
				String fileName = list.getJSONObject(i).getString("Name")+list.getJSONObject(0).getString("ID");
				loadStage(fileName);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	}

	private void saveStageList() {
		// store list
		
	}
	
	/** File manipulation */
	private void copyInitialFiles() {
		Log.e("Files","Start copying");
		try {
			InputStream inFile = getResources().openRawResource(R.raw.defaultstagesavaliable);
			InputStreamReader reader = new InputStreamReader(inFile);
			BufferedReader buffreader = new BufferedReader(reader);
			String buffer,text;
			text = "";
			while((buffer = buffreader.readLine())!=null){
				text = text + buffer;
			}
			inFile.close();
			
			JSONObject defaultSettings = new JSONObject(text);
			
			JSONArray defaultFiles = defaultSettings.getJSONArray("Default Files");
			for(int i=0;i<defaultFiles.length();i++){
				String fileName = defaultFiles.getJSONObject(i).getString("File Name");
				String content = defaultFiles.getJSONObject(i).getString("Content");
				
				FileOutputStream outFile = openFileOutput( fileName, Context.MODE_PRIVATE ); 
				outFile.write(content.getBytes()); 
				outFile.close();
			}
			
			ContainerBox.default_stage_list = defaultSettings.getJSONArray("Default List").toString();
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Files","IO error");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("Files","JSON error");
		}
		Log.e("Files","Done copying");
	}
	
	private void loadStage(String fileName) throws IOException, JSONException {
		Log.e("File","Stage loading "+fileName);
		FileInputStream inFile = openFileInput(fileName);
		InputStreamReader reader = new InputStreamReader(inFile);
		BufferedReader buffreader = new BufferedReader(reader);
		String buffer,text;
		text = "";
		while((buffer = buffreader.readLine())!=null){
			text = text + buffer;
		}
		inFile.close();
		
		JSONObject stage = new JSONObject(text);
		
		HashMap<String,String> item = new HashMap<String,String>();
		item.put("Name", stage.getString("Name"));
		item.put("Description", stage.getString("Description"));
		
		stageList.add(item);
	}
	

	/** Menu Control */

	/** Button onClick listeners */
	public void playClicked(View view) {
		if (cursor < 0) {
			Toast.makeText(this, "What Stage to play ?", Toast.LENGTH_SHORT)
					.show();
		} else {
			ContainerBox.modifyable = false;
			ContainerBox.playingStageID = stageList.get(cursor).get("Code_ID");
			//Intent stage = new Intent();
			//stage.setClass(this, MapMode.class);
			//startActivity(stage);
		}
		cursor = -1;
	}

	public void addClicked(View view) {
		LayoutInflater flat = this.getLayoutInflater();
		addNew = flat.inflate(R.layout.addnew, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Create Stage :");
		builder.setView(addNew);

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}

				});

		builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				EditText nameField = (EditText) addNew
						.findViewById(R.id.name_ip);
				String name = nameField.getText().toString();
				if (!name.contentEquals("")) {
					String codeId = name + (int)(Math.random()*2147483647);
					SharedPreferences stageInfo = StartPage.this
							.getSharedPreferences(codeId, Context.MODE_PRIVATE);
					// Check empty input
					// save to preference
					editor = stageInfo.edit();
					editor.putString("Name", name);
					editor.putString("Description", "");
					editor.commit();

					// update list
					HashMap<String, String> item = new HashMap<String, String>();
					item.put("Code_ID", codeId);
					item.put("Name", name);
					item.put("Description", "");
					stageList.add(item);
					adapter.notifyDataSetChanged();

				}

			}
		});
		builder.show();
		cursor = -1;
	}

	public void deleteClicked(View view) {
		if (cursor < 0) {
			Toast.makeText(this, "Select a stage first", Toast.LENGTH_SHORT)
					.show();
		} else {
			String target = stageList.get(cursor).get("Code_ID");
			editor = this.getSharedPreferences(target, Context.MODE_PRIVATE)
					.edit();
			editor.clear();
			editor.commit();
			stageList.remove(cursor);
			adapter.notifyDataSetChanged();
		}
		cursor = -1;
	}

	public void modifyClicked(View view) {
		if (cursor < 0) {
			Toast.makeText(this, "What Stage to modify ?", Toast.LENGTH_SHORT)
					.show();
		} else {
			ContainerBox.modifyable = true;
			ContainerBox.playingStageID = stageList.get(cursor).get("Code_ID");
			Intent stage = new Intent();
			stage.setClass(this, MapMode.class);
			startActivity(stage);
		}
		cursor = -1;
	}
}