package second.prototype;

import java.util.ArrayList;
import java.util.HashMap;

import control.stage.DrawableIndex;
import control.stage.Stage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MapView extends View {
	
	/** Members */
	private Stage stage = ContainerBox.currentStage;
	
	private float viewCenterh,viewCenterw;
	private float myX,myY;
	
	private float[] rotateMatrix = {1,0,1,0};
	
	private static final float mag = ContainerBox.meterPerPixel; // one pixel = 10 meters
	private static final float ruler = 100/mag; //m
	
	Bitmap location;
	private int scanTheta = 0;
	
	public MapView(Context context) {
		super(context);
		init();
	}

	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
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
		myX = rotateX(currentX/mag,-currentY/mag) + viewCenterw;
		myY = rotateY(currentX/mag,-currentY/mag) + viewCenterh;

		invalidate();
	}
	
	public void setOrientation(float theta) {
		// rotate matrix 
		// r[0] r[1]
		// r[2] r[3]
		rotateMatrix[0] = (float) Math.cos(theta/180*Math.PI);
		rotateMatrix[1] = (float) Math.sin(theta/180*Math.PI);
		rotateMatrix[2] = (float)-Math.sin(theta/180*Math.PI);
		rotateMatrix[3] = (float) Math.cos(theta/180*Math.PI);
		
		invalidate();
	}
	
	private float rotateX(float _x,float _y) {
		return rotateMatrix[0]*_x+rotateMatrix[1]*_y;
	}
	
	private float rotateY(float _x,float _y) {
		return rotateMatrix[2]*_x+rotateMatrix[3]*_y;
	}
	
	
	
	/** Utilities */
	private void init() {
		location = BitmapFactory.decodeResource(getResources(), R.drawable.location_icon);
		this.setBackgroundResource(DrawableIndex.BACK_GROUND);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {

		Paint self,tar,text,white,blue,green,empty;
		
		self = new Paint();
		self.setColor(Color.RED);
		
		tar = new Paint();
		tar.setColor(Color.CYAN);
		tar.setAlpha(255);
		
		text = new Paint();
		text.setColor(Color.WHITE);
		text.setStyle(Paint.Style.STROKE);
		
		white = new Paint();
		white.setColor(Color.WHITE);
		white.setStyle(Paint.Style.STROKE);
		white.setStrokeWidth(3);
		
		blue = new Paint();
		blue.setColor(Color.BLUE);
		blue.setAlpha(100);
		blue.setStrokeWidth(8);
		
		empty = new Paint();
		empty.setColor(Color.BLACK);
		empty.setAlpha(255);
		
		green = new Paint();
		green.setColor(Color.GREEN);
		green.setAlpha(128);
		green.setStrokeWidth(4);
		
		// scan bar
		float scanX,scanY;
		scanX = 1000*(float)Math.cos(0.02*scanTheta);
		scanY = 1000*(float)Math.sin(0.02*scanTheta);
		canvas.drawLine(viewCenterw,viewCenterh,viewCenterw+scanX,viewCenterh+scanY,green);
		scanTheta++;
		
		// radar
		canvas.drawText("Radar Mode ! White circle is visible range.", 30, 30, text);
		canvas.drawText("Visable range = "+ContainerBox.visibleRange, 30, 45, text);
		canvas.drawText("Scale = "+ContainerBox.meterPerPixel+" meters per pixel", 30, 60, text);

		canvas.drawText("Current Center = "+stage.getMapCenter("X")+":"+stage.getMapCenter("Y"),30, 110, text);
		
		// visible range
		canvas.drawCircle(myX, myY, ContainerBox.visibleRange/mag, white);
		canvas.drawCircle(myX, myY, 10, self);
		
		// north arrow
		canvas.drawLine(myX+rotateX(0,-ContainerBox.visibleRange/mag*2/3),myY+rotateY(0,-ContainerBox.visibleRange/mag*2/3),
				myX+rotateX(0,-ContainerBox.visibleRange/mag*4/3),myY+rotateY(0,-ContainerBox.visibleRange/mag*4/3),white);
		
		
		canvas.drawLine(viewCenterw-ruler/2, viewCenterh, viewCenterw+ruler/2, viewCenterh,text);
		canvas.drawLine(viewCenterw-ruler/2, viewCenterh-10, viewCenterw-ruler/2, viewCenterh+10,text);
		canvas.drawLine(viewCenterw+ruler/2, viewCenterh-10, viewCenterw+ruler/2, viewCenterh+10,text);
		canvas.drawText(ruler*mag+" m", viewCenterw+ruler/2+10, viewCenterh+20, text);
		
		
		// links
		for(int i=0;i<stage.links();i++) {
			String name = stage.getLink(i).nameOfEnd;
			float sX,sY,eX,eY;
			sX = rotateX(stage.getLink(i).startX,-stage.getLink(i).startY)/mag + viewCenterw;
			sY = rotateY(stage.getLink(i).startX,-stage.getLink(i).startY)/mag + viewCenterh;
			eX = rotateX(stage.getLink(i).endX,-stage.getLink(i).endY)/mag + viewCenterw;
			eY = rotateY(stage.getLink(i).endX,-stage.getLink(i).endY)/mag + viewCenterh;
			
			canvas.drawLine(sX,sY,eX,eY, stage.getPointOf(name).isVisible?blue:empty);
		}
		
		// points
				for(int i=0;i<stage.length();i++) {
					
					float x = rotateX(stage.getPointOf(i).x/mag,-stage.getPointOf(i).y/mag) + viewCenterw;
					float y = rotateY(stage.getPointOf(i).x/mag,-stage.getPointOf(i).y/mag) + viewCenterh;
					
					canvas.drawCircle(x , y, 10, stage.getPointOf(i).isVisible?tar:empty);
					//canvas.drawBitmap(location, x-location.getWidth()/2, y-location.getHeight(), stage.getPointOf(i).isVisible?tar:empty);
					canvas.drawText(stage.getPointOf(i).getName(), x+15 , y-15, stage.getPointOf(i).isVisible?text:empty);
				}
		
		
		
		super.onDraw(canvas);
	}

}
