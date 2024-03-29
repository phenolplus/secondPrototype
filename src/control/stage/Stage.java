package control.stage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import second.prototype.ContainerBox;
import second.prototype.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private HashMap<String,PointBox> pointIndex = new HashMap<String,PointBox>();
	private ArrayList<LinkElement> linkTree = new ArrayList<LinkElement>();
	
	private boolean centerChangeable;
	private float centerX,centerY;
	private int visibleRange;
	
	private SharedPreferences progressData;
	private SharedPreferences.Editor editor;
	
	private int currentProgress;
	private boolean hasFinished;
	private boolean firstPlay;
	
	public static Bitmap[] icons;
	public static Bitmap smile;
	
	/** Constructor */
	public Stage(String fileName,Context owner) {
		Log.e("File","Stage loading "+fileName);
		
		progressData = owner.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		editor = progressData.edit();
		
		// load static data
		
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
			visibleRange = coreData.getInt("Visible Range");
			ContainerBox.visibleRange = visibleRange;
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
		
		// setup user data
		
		currentProgress = progressData.getInt("Progress", 0);
		hasFinished = progressData.getBoolean("Stage Clear", false);
		firstPlay = progressData.getBoolean("First Play", true);
		
		for(int i=0;i<pointList.size();i++) {
			pointList.get(i).hasVisited = progressData.getBoolean(pointList.get(i).getName()+"Visit", false);
		}
		buildLinkTree();
		checkVisibleList();
	}
	
	/** Utilities & Management */
	/** Overall Stage Information */
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
	
	public JSONArray getItemList() {
		try {
			return coreData.getJSONArray("Item List");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public int getProgress() {
		return currentProgress;
	}
	
	public boolean isCleared() {
		return hasFinished;
	}
	
	public boolean isFirstPlay() {
		return firstPlay;
	}
	
	/** Point Operation */
	public PointBox getPointOf(int index) {
		return pointList.get(index);
	}
	
	public PointBox getPointOf(String name) {
		return pointIndex.get(name);
	}
	
	public int length() {
		return pointList.size();
	}
	
	/** Link Operation */
	public int links() {
		return linkTree.size();
	}
	
	public LinkElement getLink(int i) {
		return linkTree.get(i);
	}
	
	/** Map Operation */
	public void setMapCenter(float newX, float newY) {
		centerX = newX;
		centerY = newY;
	}
	
	public float getMapCenter(String which) {
		return which.contentEquals("X")?centerX:centerY;
	}
	
	public boolean isCenterChangable() {
		return centerChangeable;
	}
	
	public void setInRangeList(float myX, float myY) {
		for(int i=0;i<pointList.size();i++){
			if(pointList.get(i).isVisible){
				pointList.get(i).checkRange(myX, myY, visibleRange);
			}
		}
	}
	
	public void finish() {
		hasFinished = true;
	}
	
	public void played() {
		firstPlay = false;
	}
	
	public void updateProgress() {
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
		
		for(int i=0;i<pointList.size();i++) {
			editor.putBoolean(pointList.get(i).getName()+"Visit", pointList.get(i).hasVisited);
		}
		
		editor.putBoolean("Stage Clear", hasFinished);
		editor.putBoolean("First Play", firstPlay);
		
		editor.commit();
	}
	
	public void clear() {
		updateProgress(0);
		for(int i=0;i<pointList.size();i++) {
			pointList.get(i).hasVisited = false;
		}
		checkVisibleList();
		hasFinished = false;
		firstPlay = true;
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
			pointIndex.put(point.getName(), point);
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
		for(int i=0;i<pointList.size();i++){
			String[] nextPoints = pointList.get(i).getNextPoints();
			for(int j=0;j<nextPoints.length;j++){
				if(!nextPoints[j].contentEquals("NULL")){
					LinkElement link = new LinkElement();
					link.setStart(pointList.get(i).x, pointList.get(i).y);
					Log.e("Link","end :"+nextPoints[j]);
					link.setEnd(pointIndex.get(nextPoints[j]).x, pointIndex.get(nextPoints[j]).y);
					link.setNameE(nextPoints[j]);
					link.setNameS(pointList.get(i).getName());
					linkTree.add(link);
					Log.e("Link", "Added link "+pointList.get(i).getName()+" to "+nextPoints[j]);
				}
			}
		}
	}
	
	/** static methods */
	public static void buildBitmap(Context owner){
		if(smile==null){
			smile = BitmapFactory.decodeResource(owner.getResources(), ContainerBox.isTab?R.drawable.targets_large07:R.drawable.targets07);
			
			icons = new Bitmap[6];
			icons[0] = BitmapFactory.decodeResource(owner.getResources(), ContainerBox.isTab?R.drawable.targets_large01:R.drawable.targets01);
			icons[1] = BitmapFactory.decodeResource(owner.getResources(), ContainerBox.isTab?R.drawable.targets_large02:R.drawable.targets02);
			icons[2] = BitmapFactory.decodeResource(owner.getResources(), ContainerBox.isTab?R.drawable.targets_large03:R.drawable.targets03);
			icons[3] = BitmapFactory.decodeResource(owner.getResources(), ContainerBox.isTab?R.drawable.targets_large04:R.drawable.targets04);
			icons[4] = BitmapFactory.decodeResource(owner.getResources(), ContainerBox.isTab?R.drawable.targets_large05:R.drawable.targets05);
			icons[5] = BitmapFactory.decodeResource(owner.getResources(), ContainerBox.isTab?R.drawable.targets_large06:R.drawable.targets06);
		}
	}
}
