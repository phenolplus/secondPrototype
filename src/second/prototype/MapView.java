package second.prototype;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MapView extends View {
	
	/** Members */
	private String[] names;
	private float[] x,y;
	private float viewCenterh,viewCenterw;
	private float myX=0,myY=0;
	private int num=0;
	
	private static final float mag = ContainerBox.meterPerPixel; // one pixel = 10 meters
	private static final float ruler = 100/mag; //m
	
	public MapView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	/** Control signal */
	public void readMap(ArrayList<HashMap<String,String>> readin){
		num = readin.size() + 1;
		names = new String[num];
		x = new float[num];
		y = new float[num];
		
		names[0] = "Self";
		x[0] = myX/mag + viewCenterw;
		y[0] = -myY/mag + viewCenterh;
		
		for(int i=0;i<readin.size();i++){
			names[i+1] = readin.get(i).get("Name");
			x[i+1] = viewCenterw + Float.parseFloat(readin.get(i).get("Data").split(":")[1])/mag;
			y[i+1] = viewCenterh - Float.parseFloat(readin.get(i).get("Data").split(":")[2])/mag;
		}
		invalidate();
		
	}
	
	public void setViewCenter(float w,float h){
		// set center point of map
		viewCenterh = h/2;
		viewCenterw = w/2;
	}
	
	public void setCurrentLocation(float currentX, float currentY) {
		myX = currentX;
		myY = currentY;
		x[0] = myX/mag + viewCenterw;
		y[0] = -myY/mag + viewCenterh;
		invalidate();
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {

		Paint self,tar,white,blue;
		
		self = new Paint();
		self.setColor(Color.RED);
		
		tar = new Paint();
		tar.setColor(Color.CYAN);
		tar.setAlpha(200);
		
		white = new Paint();
		white.setColor(Color.WHITE);
		white.setStyle(Paint.Style.STROKE);
		
		blue = new Paint();
		blue.setColor(Color.BLUE);
		blue.setAlpha(100);
		blue.setStrokeWidth(8);
		
		// radar
		canvas.drawText("Radar Mode ! White circle is visable range.", 70, 30, white);
		canvas.drawText("Visable range = "+ContainerBox.visableRange, 70, 45, white);
		canvas.drawText("Scale = "+ContainerBox.meterPerPixel+" meters per pixel", 70, 60, white);

		canvas.drawText("Current Center = "+ContainerBox.mapCenterCord, 70, 110, white);
		// North arrow
		canvas.drawLines(new float[] {30, 100, 30, 50, 30 ,50, 40, 60}, 0, 8, white);
		
		if(!ContainerBox.modifyable) {
			// visible range
			canvas.drawCircle(x[0], y[0], ContainerBox.visableRange/mag, white);
			
			canvas.drawText("Current point = "+x[0]+":"+y[0], 70, 75, white);
			
			canvas.drawText("Current Location = "+ContainerBox.currentCord, 70, 95, white);
			
			canvas.drawLine(viewCenterw-ruler/2, viewCenterh, viewCenterw+ruler/2, viewCenterh,white);
			canvas.drawLine(viewCenterw-ruler/2, viewCenterh-10, viewCenterw-ruler/2, viewCenterh+10,white);
			canvas.drawLine(viewCenterw+ruler/2, viewCenterh-10, viewCenterw+ruler/2, viewCenterh+10,white);
			canvas.drawText(ruler*mag+" m", viewCenterw+ruler/2+10, viewCenterh+20, white);
			
		} else {
			// box
			canvas.drawLine(viewCenterw-ruler/2, viewCenterh-ruler/2, viewCenterw+ruler/2, viewCenterh-ruler/2,white);
			canvas.drawLine(viewCenterw+ruler/2, viewCenterh-ruler/2, viewCenterw+ruler/2, viewCenterh+ruler/2,white);
			canvas.drawLine(viewCenterw+ruler/2, viewCenterh+ruler/2, viewCenterw-ruler/2, viewCenterh+ruler/2,white);
			canvas.drawLine(viewCenterw-ruler/2, viewCenterh+ruler/2, viewCenterw-ruler/2, viewCenterh-ruler/2,white);
			
			canvas.drawLine(viewCenterw-10, viewCenterh-10, viewCenterw+10, viewCenterh+10,white);
			canvas.drawLine(viewCenterw-10, viewCenterh+10, viewCenterw+10, viewCenterh-10,white);
			
			canvas.drawText("The box is "+ruler*mag+" meters large", 70, 130, white);
		}
		
		// links
		for(int i=1;i<(num-1);i++){
			canvas.drawLine(x[i], y[i], x[i+1], y[i+1], blue);
		}
		
		// points
		for(int i=ContainerBox.modifyable?1:0;i<num;i++){
			canvas.drawCircle(x[i], y[i], 10, (i==0)?self:tar);
		}
		
		// names
		for(int i=1;i<num;i++){
			canvas.drawText(names[i], x[i]+10, y[i]-10, white);
		}
		
		super.onDraw(canvas);
	}

}
