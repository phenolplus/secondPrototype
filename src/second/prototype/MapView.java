package second.prototype;

import java.util.ArrayList;
import java.util.HashMap;

import control.stage.Stage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MapView extends View {
	
	/** Members */
	private Stage stage = ContainerBox.currentStage;
	
	private float viewCenterh,viewCenterw;
	private float myX,myY;
	
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
	
	public void setViewCenter(float w,float h){
		// set center point of map
		viewCenterh = h/2;
		viewCenterw = w/2;
		
		myX = viewCenterw;
		myY = viewCenterh;
	}
	
	public void setCurrentLocation(float currentX, float currentY) {
		myX = currentX/mag + viewCenterw;
		myY = -currentY/mag + viewCenterh;

		invalidate();
	}
	
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {

		Paint self,tar,white,blue,empty;
		
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
		
		empty = new Paint();
		empty.setColor(Color.BLACK);
		empty.setAlpha(0);
		
		// radar
		canvas.drawText("Radar Mode ! White circle is visible range.", 70, 30, white);
		canvas.drawText("Visable range = "+ContainerBox.visibleRange, 70, 45, white);
		canvas.drawText("Scale = "+ContainerBox.meterPerPixel+" meters per pixel", 70, 60, white);

		canvas.drawText("Current Center = "+stage.getMapCenter("X")+":"+stage.getMapCenter("Y"),70, 110, white);
		// North arrow
		canvas.drawLines(new float[] {30, 100, 30, 50, 30 ,50, 40, 60}, 0, 8, white);
		
		// visible range
		canvas.drawCircle(myX, myY, ContainerBox.visibleRange/mag, white);
		canvas.drawCircle(myX, myY, 10, self);
		
		canvas.drawLine(viewCenterw-ruler/2, viewCenterh, viewCenterw+ruler/2, viewCenterh,white);
		canvas.drawLine(viewCenterw-ruler/2, viewCenterh-10, viewCenterw-ruler/2, viewCenterh+10,white);
		canvas.drawLine(viewCenterw+ruler/2, viewCenterh-10, viewCenterw+ruler/2, viewCenterh+10,white);
		canvas.drawText(ruler*mag+" m", viewCenterw+ruler/2+10, viewCenterh+20, white);
		
		// points
		for(int i=0;i<stage.length();i++) {
			float x = stage.getPointOf(i).x/mag + viewCenterw;
			float y = -stage.getPointOf(i).y/mag + viewCenterh;
			
			canvas.drawCircle(x, y, 10, stage.getPointOf(i).isVisible?tar:empty);
			canvas.drawText(stage.getPointOf(i).getName(), x+15 , y-15, stage.getPointOf(i).isVisible?white:empty);
		}
		
		// links
		
		super.onDraw(canvas);
	}

}
