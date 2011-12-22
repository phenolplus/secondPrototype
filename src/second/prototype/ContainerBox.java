package second.prototype;

import item.Backpack;
import control.stage.Stage;
import control.stage.StageManager;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public final class ContainerBox {
	/** Pass your global data with this box 
	    Put top_ in front of your variable name */
	
	// pass-by
	public static SensorManager topManager;
	public static Sensor topSensor;
	public static String visablePoints;
	public static boolean isTab = false;
	public static boolean faceUp = true;
	
	public static Stage currentStage;
	public static StageManager stageManager;
	public static Backpack backback;
	
	public static int visibleRange;
	
	// constant
	public static float meterPerPixel = (float)0.5;
	public static final float deg_index = 100000; // longitude/latitude degree to meter
	
}
