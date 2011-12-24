package second.prototype;

import item.Backpack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import control.stage.Stage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DrawingSurface extends android.view.SurfaceView implements
		SurfaceHolder.Callback {

	/** Members */
	private Stage stage = ContainerBox.currentStage;
	private Backpack backpack = ContainerBox.backback;
	private Context owner;

	private Camera camera;
	private SurfaceHolder holder;

	private final float[] north = { 0, -90, 90 };
	private ArrayList<ViewPoint> targetList = new ArrayList<ViewPoint>();
	private float[] current = new float[3];
	private final float distance = ContainerBox.isTab ? 35 : 10;
	
	private boolean touched=false;
	private String which;
	private View eventView;
	private ArrayList<HashMap<String,String>> messages = new ArrayList<HashMap<String,String>>();
	private SimpleAdapter adapter;
	private int num,end;

	private Bitmap icon;

	public DrawingSurface(Context context) {
		super(context);
		owner = context;
		this.setWillNotDraw(false);
		
		holder = this.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// TODO Auto-generated constructor stub

		icon = BitmapFactory.decodeResource(getResources(), R.drawable.target);
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas = aquireNorth(canvas);
		canvas = showTargets(canvas);
		invalidate();
	}
	
	/** SurfaceView.Callback interface */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.e("Surface", "   creating");
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException exp) {
			camera.release();
			camera = null;
		}
		Camera.Parameters para = camera.getParameters();
		Log.e("Camera", "camera size = " + para.getPreviewSize().height + ":"
				+ para.getPreviewSize().width);
		Log.e("Camera",
				"screen size = " + this.getWidth() / 2 + ":" + this.getHeight()
						/ 2);
		
		List<Size> sizeList = para.getSupportedPreviewSizes();
		
		for (int i = 0; i < sizeList.size(); i++) {
			int avaliableX, avaliableY;
			avaliableX = sizeList.get(i).width;
			avaliableY = sizeList.get(i).height;
			Log.e("Avaliable Size",
					sizeList.get(i).width + ":" + sizeList.get(i).height);

			if (avaliableX * this.getHeight() == this.getWidth() * avaliableY) {
				para.setPreviewSize(avaliableX, avaliableY);
				break;
			}

		}
		camera.setParameters(para);
		camera.startPreview();
		Log.e("Surface", "   did created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.stopPreview();
		camera.release();
		camera = null;
		Log.e("Surface", "   did destroyed");

	}

	/** Importing data */
	public void setCurrentFace(float[] direction) {
		current[0] = ContainerBox.isTab ? direction[0]
				: ((direction[0] + 90) % 360);
		current[1] = ContainerBox.isTab ? direction[1]
				: ContainerBox.faceUp ? (direction[2] - 180) : (-direction[2]);
		current[2] = ContainerBox.isTab ? direction[2] : direction[1];
		// Log.e("Orientation","theta = "+direction[2]);
	}

	public void setVisablePoints() {
		// The LIST should have the form :
		// Name:Lat:Long!(next)!...
		// longitude and latitude are relative
		for (int i = 0; i < stage.length(); i++) {
			if (stage.getPointOf(i).inRange) {
				ViewPoint target = new ViewPoint();
				target.name = stage.getPointOf(i).getName();
				float x, y;
				x = stage.getPointOf(i).x;
				y = stage.getPointOf(i).y;

				if (x > 0) {
					target.phi = (float) ((y >= 0) ? Math.atan(x / y)
							: (Math.PI + Math.atan(x / y)));
				} else {
					target.phi = (float) ((y >= 0) ? (2 * Math.PI + Math.atan(x
							/ y)) : (Math.PI + Math.atan(x / y)));
				}

				target.phi = (float) Math.toDegrees(target.phi);
				target.theta = -85; // horizontal (default)

				Log.e("Points", target.name + " phi = " + target.phi);
				targetList.add(target);
			}
		}

	}
	
	public boolean inEvent() {
		return touched;
	}

	/** Utilities */
	private Canvas aquireNorth(Canvas canvas) {
		float[] point = new float[2];
		// point = [x,y] = [polar,roll]

		point[1] = north[1] - current[1];
		if (current[0] > 180) {
			point[0] = north[0] - (current[0] - 360);

		} else {
			point[0] = north[0] - current[0];

		}
		point[0] = point[0] * distance;
		point[1] = point[1] * distance;

		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setAlpha(127);

		float x, y;
		y = canvas.getHeight() / 2 + point[1];
		x = canvas.getWidth() / 2 + point[0];

		canvas.drawCircle(x, y, 30, paint);
		paint.setAlpha(5);
		paint.setColor(Color.WHITE);
		canvas.drawText("N", x, y, paint);

		// Log.e("Canvas","update point @ x = "+point[0]+"  "+"y = "+point[1]);
		return canvas;
	}

	private Canvas showTargets(Canvas canvas) {
		Paint cPaint = new Paint();
		cPaint.setColor(Color.CYAN);
		cPaint.setAlpha(127);

		Paint tPaint = new Paint();
		tPaint.setColor(Color.WHITE);

		float x, y;
		float[] point = new float[2];
		for (int i = 0; i < targetList.size(); i++) {
			point[1] = targetList.get(i).theta - current[1];
			point[0] = targetList.get(i).phi - current[0];
			// ============
			point[0] = (point[0] + 360) % 360;
			if (point[0] > 270) {
				point[0] = point[0] - 360;
			}

			point[0] = point[0] * distance;
			point[1] = point[1] * distance;
			x = this.getWidth() / 2 + point[0];
			y = this.getHeight() / 2 + point[1];
			// canvas.drawCircle(x, y, 50, cPaint);
			canvas.drawBitmap(icon, x - icon.getWidth() / 2,
					y - icon.getHeight() / 2, null);
			canvas.drawText(targetList.get(i).name, x, y, tPaint);
		}
		return canvas;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			which = checkIfTouched(event.getX(),event.getY());
			Log.e("Touch","at "+event.getX()+" : "+event.getY()+" = "+which);
			break;
		case MotionEvent.ACTION_UP:
			if(touched){
				showEvent();
			}
		default:
				
		}
		return true; 
	}
	
	private String checkIfTouched(float tX,float tY) {
		float x, y;
		float[] point = new float[2];
		for (int i = 0; i < targetList.size(); i++) {
			point[1] = targetList.get(i).theta - current[1];
			point[0] = targetList.get(i).phi - current[0];
			// ============
			point[0] = (point[0] + 360) % 360;
			if (point[0] > 270) {
				point[0] = point[0] - 360;
			}

			point[0] = point[0] * distance;
			point[1] = point[1] * distance;
			x = this.getWidth() / 2 + point[0];
			y = this.getHeight() / 2 + point[1];
			
			Log.e("Touch","point at "+x+" : "+y);
			if((Math.pow((x-tX),2)+Math.pow((y-tY),2))<Math.pow((icon.getHeight()/2),2)) {
				touched = true;
				return targetList.get(i).name;
			}
		}
		touched = false;
		return null;
	}
	
	private void showEvent() {
		AlertDialog.Builder builder = new AlertDialog.Builder(owner);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(which);
		
		LayoutInflater infla = LayoutInflater.from(owner);
		eventView = infla.inflate(R.layout.eventdialog, null);
		builder.setView(eventView);
		
		ListView messageList = (ListView) eventView.findViewById(R.id.event);
		messageList.setBackgroundColor(Color.BLACK);
		
		num = 1;
		putMessageTo(which);
		adapter = new SimpleAdapter(owner,messages,android.R.layout.simple_list_item_1,new String[] {"Message"},new int[] {android.R.id.text1});
		messageList.setAdapter(adapter);
		messageList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(arg2 == end){
					num++;
					putMessageTo(which);
					adapter.notifyDataSetChanged();
				}
			}
			
		});
		
		builder.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int typeWhich) {
				// TODO Auto-generated method stub
				touched = false;
				if(!stage.getPointOf(which).hasVisited){
					stage.getPointOf(which).hasVisited = true;
					stage.updateProgress();
				}
			}
		});
		
		builder.show();
	}
	
	private void putMessageTo(String name) {
		messages.clear();
		for(int i=0;i<Math.min(num,stage.getPointOf(name).numOfEvents());i++){
			HashMap<String,String> item = new HashMap<String,String>();
			String message = stage.getPointOf(name).eventList.get(i).postMessage();
			item.put("Message", message);
			
			if(message.contentEquals("[END]")){
				stage.finish();
			}
			if(!message.contentEquals("")){
				messages.add(item);
			} else {
				num++;
			}
		}
		
		if(num < stage.getPointOf(name).numOfEvents()){
			HashMap<String,String> item = new HashMap<String,String>();
			item.put("Message", "More");
			messages.add(item);
			end = messages.size()-1;
		} else {
			end = -1;
		}
	}

	// point box
	class ViewPoint {
		public float phi;
		public float theta;
		public String name;

	}

}
