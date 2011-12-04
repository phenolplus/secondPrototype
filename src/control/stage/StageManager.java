package control.stage;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import second.prototype.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class StageManager {
	
	private JSONArray stageList;
	private Context owner;
	
	private SharedPreferences stageData;
	private SharedPreferences.Editor editor;
	
	public StageManager(Context _owner) {
		owner = _owner;
		stageData = owner.getSharedPreferences("Global Data",Context.MODE_PRIVATE);
		editor = stageData.edit();
		
		try {
			stageList = new JSONArray(stageData.getString("Stage List", ""));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public Stage getStage(int index) {
		try {
			String fileName = stageList.getJSONObject(index).getString("ID");
			return new Stage(fileName,owner);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public int numOfStages() {
		return stageList.length();
	}
	
	public boolean importStage() {
		return true;
	}
	
	public boolean deleteStage(int number) {
		return true;
	}
	
	public void commit() {
		editor.putString("Stage List", stageList.toString());
		editor.commit();
	}
	
	public static void initFileSettings(Context owner) {
		Log.e("Files","Start copying");
		SharedPreferences stageData;
		SharedPreferences.Editor editor;
		
		stageData = owner.getSharedPreferences("Global Data",Context.MODE_PRIVATE);
		editor = stageData.edit();
		
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
