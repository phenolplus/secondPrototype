package control.stage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import second.prototype.ContainerBox;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This class controls a single stage.
 * 
 * 
 * @author vincentlee
 *
 */
public class Stage {
	
	private JSONObject coreData;
	
	private ArrayList<PointBox> pointList = new ArrayList<PointBox>();
	private ArrayList<LinkElement> linkTree = new ArrayList<LinkElement>();
	
	private boolean centerChangeable;
	private float centerX,centerY;
	
	private SharedPreferences progressData;
	private SharedPreferences.Editor editor;
	
	private int currentProgress;
	
	/** Constructor */
	public Stage(String fileName,Context owner) {
		Log.e("File","Stage loading "+fileName);
		
		progressData = owner.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		editor = progressData.edit();
		
		FileInputStream inFile;
		try {
			inFile = owner.openFileInput(fileName);
			InputStreamReader reader = new InputStreamReader(inFile);
			BufferedReader buffreader = new BufferedReader(reader);
			String buffer,text;
			text = "";
			while((buffer = buffreader.readLine())!=null){
				text = text + buffer;
			}
			inFile.close();
			
			coreData =  new JSONObject(text);
			setupPoints();
			setupCenter();
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			onExceptionOccur(e);
		} catch (JSONException e) {
			e.printStackTrace();
			onExceptionOccur(e);
		} catch (IOException e) {
			e.printStackTrace();
			onExceptionOccur(e);
		}
		
		currentProgress = progressData.getInt("Progress", 0);
		checkVisibleList();
	}
	
	/** Utilities & Management */
	public String getName() {
		try {
			return coreData.getString("Name");
		} catch (JSONException e) {
			e.printStackTrace();
			onExceptionOccur(e);
			return null;
		}
	}

	public String getDescription() {
		try {
			return coreData.getString("Description");
		} catch (JSONException e) {
			e.printStackTrace();
			onExceptionOccur(e);
			return null;
		}
	}
	
	public PointBox getPointOf(int index) {
		return pointList.get(index);
	}
	
	public int length() {
		return pointList.size();
	}
	
	public void setMapCenter(float newX, float newY) {
		centerX = newX;
		centerY = newY;
	}
	
	public float getMapCenter(String which) {
		return which.contentEquals("X")?centerX:centerY;
	}
	
	public void setInRangeList(float myX, float myY) {
		for(int i=0;i<pointList.size();i++){
			if(pointList.get(i).isVisible){
				pointList.get(i).checkRange(myX, myY, ContainerBox.visibleRange);
			}
		}
	}
	
	public void updateProgress(){
		currentProgress++;
		checkVisibleList();
	}
	
	public void updateProgress(int setProgress) {
		currentProgress = (setProgress<0)?0:setProgress;
	}
	
	public void commit() {
		editor.putFloat("CenterPoint X", centerX);
		editor.putFloat("CenterPoint Y", centerY);
		
		editor.putInt("Progress", currentProgress);
		
		editor.commit();
	}
	
	/** internal utilities */
	private void onExceptionOccur(Exception e) {
		Log.e("Stage Class",e.getClass().getName());
	}
	
	private void setupPoints() throws NumberFormatException, JSONException {
		JSONArray array = coreData.getJSONArray("Location List");
		for(int i=0;i<array.length();i++){
			PointBox point = new PointBox(array.getJSONObject(i));
			pointList.add(point);
		}
	}
	
	private void setupCenter() throws JSONException {
		centerChangeable = coreData.getBoolean("Center Changeable");
		
		float defaultX = Float.parseFloat(coreData.getString("CenterPoint X"));
		float defaultY = Float.parseFloat(coreData.getString("CenterPoint Y"));
		
		centerX = centerChangeable?progressData.getFloat("CenterPoint X", defaultX):defaultX;
		centerY = centerChangeable?progressData.getFloat("CenterPoint Y", defaultY):defaultY;
	}
	
	private void checkVisibleList() {
		for(int i=0;i<pointList.size();i++){
			pointList.get(i).checkVisable(currentProgress);
		}
	}
	
	private void buildLinkTree() {
		
	}
	
	/** Inner classes */
	class LinkElement {
		
	}
	
}
