package control.stage;

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

import second.prototype.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class StageManager {
	
	private JSONArray coreData;
	private ArrayList<HashMap<String,String>> stageList = new ArrayList<HashMap<String,String>>();
	private Context owner;
	
	private SharedPreferences stageData;
	private SharedPreferences.Editor editor;
	
	public StageManager(Context _owner) {
		owner = _owner;
		stageData = owner.getSharedPreferences("Global Data",Context.MODE_PRIVATE);
		editor = stageData.edit();
		
		try {
			coreData = new JSONArray(stageData.getString("Stage List", ""));
			Log.e("Manager",coreData.toString());
			for(int i=0;i<coreData.length();i++){
				HashMap<String,String> item = new HashMap<String,String>();
				item.put("ID", coreData.getJSONObject(i).getString("ID"));
				stageList.add(item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public boolean firstPlay(){
		return stageData.getBoolean("First Play", true);
	}
	
	public Stage getStage(int index) {
		String fileName = stageList.get(index).get("ID");
		return new Stage(fileName,owner);
	}
	
	public String getFileName(int index){
		return stageList.get(index).get("ID");
	}
	
	public int numOfStages() {
		return stageList.size();
	}
	
	public boolean importStage(String fileName) {
		HashMap<String,String> item = new HashMap<String,String>();
		item.put("ID", fileName);
		stageList.add(item);
		Log.e("Import", fileName);
		return true;
	}
	
	public boolean deleteStage(int number) {
		String fileName = stageList.get(number).get("ID");
		owner.deleteFile(fileName);
		stageList.remove(number);
		return true;
	}
	
	public void commit() {
		coreData = new JSONArray();
		for(int i=0;i<stageList.size();i++){
			try {
				coreData.put(new JSONObject().put("ID", stageList.get(i).get("ID")));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		editor.putString("Stage List", coreData.toString());
		editor.commit();
	}
	
	public static void initFileSettings(Context owner) {
		Log.e("Files","Start copying");
		SharedPreferences stageData;
		SharedPreferences.Editor editor;
		
		stageData = owner.getSharedPreferences("Global Data",Context.MODE_PRIVATE);
		editor = stageData.edit();
		editor.clear();
		
		try {
			InputStream inFile = owner.getResources().openRawResource(R.raw.defaultstagesavaliable);
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
				
				FileOutputStream outFile = owner.openFileOutput( fileName, Context.MODE_PRIVATE ); 
				outFile.write(content.getBytes()); 
				outFile.close();
			}
			
			editor.putString("Stage List", defaultSettings.getJSONArray("Default List").toString());
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Files","IO error");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("Files","JSON error");
		}
		Log.e("Files","Done copying");
		
		
		editor.putBoolean("First Play", false);
		editor.commit();
		
		System.gc();
	}
}
