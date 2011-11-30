package control.stage;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;

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
			String fileName = stageList.getJSONObject(index).getString("Name")+stageList.getJSONObject(index).getString("ID");
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
}
