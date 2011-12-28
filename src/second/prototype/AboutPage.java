package second.prototype;

import control.stage.StageManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AboutPage extends Activity {
	/** Members */
	private ImageView plot;
	
	private long start;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.about);
        plot = (ImageView) findViewById(R.id.foo);
        ContainerBox.master = false;
        
        plot.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					start = event.getDownTime();
					return true;
				case MotionEvent.ACTION_UP:
					if((event.getEventTime()-start)>5000){
						if(!ContainerBox.master){
							plot.setImageResource(R.drawable.posterwwwp);
							ContainerBox.master = true;
						} else {
							plot.setImageResource(R.drawable.intro);
							ContainerBox.master = false;
						}
					}
				}
				return false;
			}
        	
        });
	}
}
