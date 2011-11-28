package second.prototype;

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
	
	public static String playingStageID;
	public static boolean modifyable=false;
	
	public static String mapCenterCord;
	public static String currentCord;
	
	// constant
	public static final float visableRange = 30;
	public static float meterPerPixel = (float)0.5;
	public static final float deg_index = 100000; // longitude/latitude degree to meter
	
	public static String default_stage_list;
}
