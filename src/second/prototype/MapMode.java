package second.prototype;

import java.util.ArrayList;
import java.util.HashMap;

import control.stage.Stage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MapMode extends Activity {
	/** Members */
	private Stage stage = ContainerBox.currentStage;
	
	private SensorManager manager;
	private Sensor sensor;
	private SensorEventListener listener;

	private boolean called = false;

	private MapView mapView;
	private ListView pointListView;
	private float myX = 0,myY = 0;

	private ArrayList<HashMap<String, String>> pointList = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter adapter;
	
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	private View storyBox;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Log.e("Version","APL level = "+Build.VERSION.SDK_INT);
		
		mapView = (MapView) findViewById(R.id.mapView);
		pointListView = (ListView) findViewById(R.id.listView);
		
		// set up sensors
		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		ContainerBox.topManager = manager;
		ContainerBox.topSensor = sensor;
		
		// set up location
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		mapView.setViewCenter(this.getWindowManager().getDefaultDisplay().getWidth()*3/4
				,this.getWindowManager().getDefaultDisplay().getHeight()*3/4);
		
		setTitle("Now Playing : "+stage.getName());
	}
	
	/** System works */
	@Override
	public void onResume() {
		super.onResume();
		buildList();
		
		// orientation sensor
		if (sensor != null) {
			listener = new SensorEventListener() {

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSensorChanged(SensorEvent event) {
					// TODO Auto-generated method stub
					mapView.setOrientation(event.values[0]);
					float para = ContainerBox.isTab?event.values[1]:event.values[2];
					if (Math.abs(para) > 45 && !called ) {
						// check for initial state
						// check for repeating call (one intent allowed)
						// check if playing (modify mode doesn't go camera)
						called = true;
						Intent intent = new Intent();
						intent.setClass(MapMode.this,CameraMode.class);
						ContainerBox.currentStage = stage;
						stage.setInRangeList(myX, myY);
						startActivity(intent);
					}

				}

			};
			manager.registerListener(listener, sensor,
					SensorManager.SENSOR_DELAY_GAME);
			called = false;
		}
		
		// location manager
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			locationListener = new LocationListener() {

				@Override
				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub
					myX = (float)location.getLongitude() - stage.getMapCenter("X");
					myY = (float)location.getLatitude() - stage.getMapCenter("Y");
					
					myX = myX*ContainerBox.deg_index;
					myY = myY*ContainerBox.deg_index;
					
					mapView.setCurrentLocation(myX,myY);
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
		manager.unregisterListener(listener);
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.removeUpdates(locationListener);
			
		}
		saveList();
	}

	/** Menu Control */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(1, 0, 0, "Check Up").setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(1, 1, 0, "Set Center Point").setIcon(android.R.drawable.ic_menu_myplaces);
		menu.add(1, 2, 0, "Backpack").setIcon(android.R.drawable.ic_menu_manage);
		menu.add(1, 3, 0, "Clear").setIcon(android.R.drawable.ic_menu_delete);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			checkStatus();
			break;
		case 1:
			setCurrentPointCenter();
			break;
		case 2:
			openBackpack();
			break;
		case 3:
			clearProgress();
			break;
		default :
		}
		
		return true;
	}

	
	/** Menu operations */	
	private void checkStatus() {
		
	}
	
	private void openBackpack() {
		// do something
	}
	
	private void clearProgress() {
		stage.updateProgress(0);
	}
	
	private void setCurrentPointCenter() {
		float nowX,nowY;
		nowX = (float) locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		nowY = (float) locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
		
		stage.setMapCenter(nowX, nowY);
		mapView.setCurrentLocation(0, 0);
	}
	
	private void readStory(int number) {
		LayoutInflater lf = LayoutInflater.from(this);
		storyBox = lf.inflate(R.layout.storybox, null);
		TextView story = (TextView) storyBox.findViewById(R.id.story);
		story.setText(stage.getPointOf(number).getStory());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(stage.getPointOf(number).getName());
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(storyBox);
		
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.gc();
			}
			
		});
		
		builder.show();
	}
	
	

	/** Build up list */
	private void buildList() {
		
		pointList.clear();
		
		for(int i=0;i<stage.length();i++){
			if(stage.getPointOf(i).isVisible) {
				HashMap<String,String> item = new HashMap<String,String>();
				item.put("Name", stage.getPointOf(i).getName());
				item.put("Brief", stage.getPointOf(i).getBrief());
				pointList.add(item);
			}
		}
		
		adapter = new SimpleAdapter(this, pointList, R.layout.pointitem, new String[] {"Name","Brief"},new int[] {R.id.pointName,R.id.pointLocation});
		pointListView.setAdapter(adapter);
		
		pointListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				readStory(arg2);
			}
			
		});
		
	}

	private void saveList() {
		// store list
		stage.commit();
	}
}
