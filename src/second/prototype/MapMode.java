package second.prototype;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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

public class MapMode extends Activity {
	/** Members */
	private String stageID = ContainerBox.playingStageID;
	private String stage;
	
	private SensorManager manager;
	private Sensor sensor;
	private SensorEventListener listener;

	private boolean called = false;

	private MapView mapView;
	private ListView pointListView;

	private ArrayList<HashMap<String, String>> pointList = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter adapter;
	private int cursor = -1;
	
	private float myX = 0,myY = 0;
	private float mapCX,mapCY;
	
	private View setData;
	private SharedPreferences savedPoints;
	private SharedPreferences.Editor editor;
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		ContainerBox.isTab = (Build.VERSION.SDK_INT > 10);
		Log.e("Version","APL level = "+Build.VERSION.SDK_INT);
		
		mapView = (MapView) findViewById(R.id.mapView);
		pointListView = (ListView) findViewById(R.id.listView);
		
		savedPoints = getSharedPreferences(stageID, Context.MODE_PRIVATE);
		
		// set up sensors
		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		ContainerBox.topManager = manager;
		ContainerBox.topSensor = sensor;
		
		// set up location
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		loadGPSData();
		
		buildList();
		mapView.setViewCenter(this.getWindowManager().getDefaultDisplay().getWidth()*3/4
				,this.getWindowManager().getDefaultDisplay().getHeight()*3/4);
		mapView.readMap(pointList);
		
		freshTitle();
	}
	
	/** System works */
	@Override
	public void onResume() {
		super.onResume();
		
		// orientation sensor
		if (sensor != null && !ContainerBox.modifyable) {
			listener = new SensorEventListener() {

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSensorChanged(SensorEvent event) {
					// TODO Auto-generated method stub

					float para = ContainerBox.isTab?event.values[1]:event.values[2];
					if (Math.abs(para) > 45 && !called ) {
						// check for initial state
						// check for repeating call (one intent allowed)
						// check if playing (modify mode doesn't go camera)
						called = true;
						Intent intent = new Intent();
						intent.setClass(MapMode.this,CameraMode.class);
						MapMode.this.quickPass();
						startActivity(intent);
					}

				}

			};
			manager.registerListener(listener, sensor,
					SensorManager.SENSOR_DELAY_GAME);
			called = false;
		}
		
		// location manager
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !ContainerBox.modifyable){
			locationListener = new LocationListener() {

				@Override
				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub
					myX = (float)location.getLongitude() - mapCX;
					myY = (float)location.getLatitude() - mapCY;
					
					myX = myX*ContainerBox.deg_index;
					myY = myY*ContainerBox.deg_index;
					
					mapView.setCurrentLocation(myX,myY);
					
					Log.e("GPS something"," myX = "+myX+" myY = "+myY);
					ContainerBox.currentCord = (float)location.getLongitude()+" : "+(float)location.getLatitude();
				}

				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					Log.e("GPS something","Provider disabled");
				}

				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					Log.e("GPS something","Provider enabled");
				}

				@Override
				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// TODO Auto-generated method stub
					Log.e("GPS something"," status changed");
				}
				
			};
			Log.e("GPS something"," Did requested");
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
		} else {
			if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				Toast.makeText(this, "This Game Requrires GPS to Play", Toast.LENGTH_LONG);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if(!ContainerBox.modifyable) {
			manager.unregisterListener(listener);
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locationManager.removeUpdates(locationListener);
			}
		}
		saveList();
	}

	/** Menu Control */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (ContainerBox.modifyable) {
			menu.add(1, 0, 1, "Edit Name");
			menu.add(1, 1, 4, "Add Next Point");
			menu.add(1, 2, 3, "Edit Story");
			menu.add(1, 3, 5, "Delete Point"); 
			menu.add(1, 4, 2, "Edit Description");
		} else {
			menu.add(2, 0, 0, "Read Story");
			menu.add(2, 1, 0, "Set Center Point");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if (item.getGroupId()==1) {
			switch (item.getItemId()) {
			case 0:
				editName();
				break;
			case 1:
				addNewPoint();
				break;
			case 2:
				editStory();
				break;
			case 3:
				deletePoint();
				break;
			case 4:
				editDescription();
				break;
			default :
			}
		} else {
			switch (item.getItemId()) {
			case 0:
				readStory();
				break;
			case 1:
				setCurrentPointCenter();
				break;
			default :
			}
		}
		return true;
	}

	/** Dialog control */
	private void addNewPoint() {
		LayoutInflater infla = LayoutInflater.from(this);
		setData = infla.inflate(R.layout.popup, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add New Point");
		builder.setView(setData);
		
		// Cancel
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				MapMode.this.cursor = -1;
				Log.e("MapView",""+mapView.getWidth()+" : "+mapView.getHeight());
			}
			
		});
		
		builder.setPositiveButton("Add", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				EditText nameIn = (EditText) setData.findViewById(R.id.nameField);
				EditText xIn = (EditText) setData.findViewById(R.id.x_in);
				EditText yIn = (EditText) setData.findViewById(R.id.y_in);
				
				String name;
				float x,y;
				name = nameIn.getText().toString();
				x = Float.parseFloat(xIn.getText().toString());
				y = Float.parseFloat(yIn.getText().toString());
				
				HashMap<String,String> item = new HashMap<String,String>();
				putDataToHash(item,name,x,y);
				
				
				if(cursor>0){
					pointList.add(cursor, item);
				} else {
					pointList.add(item);
				}
				MapMode.this.adapter.notifyDataSetChanged();
				MapMode.this.cursor = -1;
				mapView.readMap(pointList);
			}
			
		});
		
		builder.show();
	}

	private void deletePoint() {
		if(cursor<0){
			Toast.makeText(this, "Select first !!", Toast.LENGTH_SHORT).show();
		} else {
			pointList.remove(cursor);
			adapter.notifyDataSetChanged();
			mapView.readMap(pointList);
		}
		cursor = -1;
	}
	

	private void readStory() {
		savedPoints = getSharedPreferences(stageID, Context.MODE_PRIVATE);
		Toast.makeText(this, savedPoints.getString("Story", "Empty"), Toast.LENGTH_SHORT).show();
		cursor = -1;
	}
	
	private void editName() {
		LayoutInflater flat = this.getLayoutInflater();
		setData = flat.inflate(R.layout.addnew, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Change Map Name into :");
		builder.setView(setData);
		EditText nameField = (EditText) setData.findViewById(R.id.name_ip);
		nameField.setHint(stage);
		
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}

				});

		builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				EditText nameField = (EditText) setData.findViewById(R.id.name_ip);
				String name = nameField.getText().toString();
				name = name.contentEquals("")?stage:name;
				
				savedPoints = getSharedPreferences(stageID, Context.MODE_PRIVATE);
				editor = savedPoints.edit();
				
				editor.putString("Name", name);
				editor.commit();
				MapMode.this.freshTitle();
			}
		});
		builder.show();
	}
	
	private void editStory() {
		LayoutInflater flat = this.getLayoutInflater();
		setData = flat.inflate(R.layout.addnew, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Edit Story :");
		builder.setView(setData);
		
		savedPoints = getSharedPreferences(stageID, Context.MODE_PRIVATE);
		EditText storyField = (EditText) setData.findViewById(R.id.name_ip);
		storyField.setText(savedPoints.getString("Story", "New Story ..."));
		storyField.setMaxLines(5);
		
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}

				});

		builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				EditText storyField = (EditText) setData.findViewById(R.id.name_ip);
				String story = storyField.getText().toString();
				
				savedPoints = getSharedPreferences(stageID, Context.MODE_PRIVATE);
				editor = savedPoints.edit();
				
				editor.putString("Story", story);
				editor.commit();
			}
		});
		builder.show();
	}
	
	private void setCurrentPointCenter() {
		float nowX,nowY;
		nowX = (float) locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		nowY = (float) locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
		
		mapCX = nowX;
		mapCY = nowY;
		
		savedPoints = getSharedPreferences(stageID, Context.MODE_PRIVATE);
		editor = savedPoints.edit();
		
		editor.putFloat("mapCenterX", mapCX);
		editor.putFloat("mapCenterY", mapCY);
		
		editor.commit();
		
		ContainerBox.mapCenterCord = nowX+":"+nowY;
		Log.e("GPS something"," mapCX = "+mapCX+" mapCY = "+mapCY);
	}
	
	private void editDescription() {
		LayoutInflater flat = this.getLayoutInflater();
		setData = flat.inflate(R.layout.addnew, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Add Breif Discription :");
		builder.setView(setData);
		
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}

				});

		builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				EditText nameField = (EditText) setData.findViewById(R.id.name_ip);
				String des = nameField.getText().toString();
				
				savedPoints = getSharedPreferences(stageID, Context.MODE_PRIVATE);
				editor = savedPoints.edit();
				
				editor.putString("Description", des);
				editor.commit();
			}
		});
		builder.show();
	}

	/** Build up list */
	private void putDataToHash(HashMap<String,String> item,String name, float x, float y) {
		item.put("Data", name+":"+x+":"+y);
		item.put("Name", name);
		item.put("Value", "x = "+x+" : y = "+y);
		item.put("xCord", ""+x);
		item.put("yCord", ""+y);
	}
	
	private void buildList() {
		savedPoints = getSharedPreferences(stageID, Context.MODE_PRIVATE);
		String list = savedPoints.getString("LocationList", "North:0.01:1000!");
		
		String[] entries = list.split("!");
		
		for(int i=0;i<entries.length;i++){
			HashMap<String,String> item = new HashMap<String,String>();
			String [] place = entries[i].split(":");
			float x,y;
			x = Float.parseFloat(place[1]);
			y = Float.parseFloat(place[2]);
			putDataToHash(item,place[0],x,y);
			pointList.add(item);
		}
		adapter = new SimpleAdapter(this, pointList, R.layout.pointitem, new String[] {"Name","Value"},new int[] {R.id.pointName,R.id.pointLocation});
		pointListView.setAdapter(adapter);
		
		pointListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				MapMode.this.cursor = arg2;
				freshTitle();
			}
			
		});
		
	}

	private void saveList() {
		// store list
		String write = "";
		savedPoints = getSharedPreferences(stageID,Context.MODE_PRIVATE);
		editor = savedPoints.edit();
		for(int i=0;i<pointList.size();i++){
			write = write + pointList.get(i).get("Data") + "!";
		}
		editor.putString("LocationList", write);
		editor.commit();
		
	}
	
	private void quickPass(){
		String pass = "";
		for(int i=0;i<pointList.size();i++){
			float rx,ry;
			rx = Float.parseFloat(pointList.get(i).get("xCord")) - myX;
			ry = Float.parseFloat(pointList.get(i).get("yCord")) - myY;
			float range = rx*rx + ry*ry;
			if(range<(ContainerBox.visableRange*ContainerBox.visableRange)) {
				pass = pass + pointList.get(i).get("Name") + ":" + rx + ":" + ry + "!";
			}
		}
		ContainerBox.visablePoints = pass;
		Log.e("Pass data","Pass = "+pass);
	}
	
	private void freshTitle() {
		stage = getSharedPreferences(stageID, Context.MODE_PRIVATE).getString("Name", "John Doe");
		String statusBar = (!ContainerBox.modifyable? "":(" = Modify Mode  " + "current cursor = "+cursor));
		setTitle("Map : "+stage+statusBar);
	} 
	
	private void loadGPSData() {
		mapCX = savedPoints.getFloat("mapCenterX", 0);
		mapCY = savedPoints.getFloat("mapCenterY", 0);
		
		Log.e("GPS something","Load initial center"+mapCX+":"+mapCY);
		ContainerBox.mapCenterCord = mapCX+" : "+mapCY;
	}
}
