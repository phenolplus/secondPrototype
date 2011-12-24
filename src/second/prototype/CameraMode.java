package second.prototype;

import control.appearance.BackgroundMusic;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class CameraMode extends Activity {
	
	/** Members */
	private DrawingSurface mSurface;
	
	private SensorManager manager = ContainerBox.topManager;
	private Sensor sensor = ContainerBox.topSensor;
	private SensorEventListener listener;
	// fix roll range problem
	private Sensor acc;
	private SensorEventListener accListener;
	
	
	private boolean called = false;
	private boolean visited = false;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        acc = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSurface = new DrawingSurface(this);
        setContentView(mSurface);
        mSurface.setVisablePoints();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	BackgroundMusic.play();
    	
    	Log.e("CameraMode","onResume called");
    	listener = new SensorEventListener(){

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				mSurface.setCurrentFace(event.values);
				float para = ContainerBox.isTab?event.values[1]:event.values[2];
				
				if(Math.abs(para)>50){
					visited = true;
				}
				
				if(Math.abs(para)<10&&!called&&visited&&!mSurface.inEvent()){
					Log.e("Sensor","back : para = "+para);
					CameraMode.this.finish();
					called = true;
				}
				//Log.e("Sensor","now para = "+para);
			}
    		
    	};
    	
    	manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
    	called = false;
    	visited = false;
    	
    	accListener = new SensorEventListener(){

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				ContainerBox.faceUp = event.values[2]<0;
				//Log.e("Face", "aY = "+event.values[2]);
			}
    		
    	};
    	
    	manager.registerListener(accListener, acc, SensorManager.SENSOR_DELAY_GAME);
    	
    }
        
    @Override
    public void onPause(){
    	super.onPause();
    	BackgroundMusic.pause();
    	
    	manager.unregisterListener(listener);
    	manager.unregisterListener(accListener);
    	Log.e("CameraMode"," done onPause");
    }
    
    @Override
    public void onDestroy(){
    	super.onDestroy();
    	Log.e("CameraMode"," done onDestroy");
    }
    
    /** Key control */
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
    	if(keyCode==KeyEvent.KEYCODE_BACK){
    		Toast.makeText(this, "Put your device flat!", Toast.LENGTH_SHORT).show();
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
    
   
}
